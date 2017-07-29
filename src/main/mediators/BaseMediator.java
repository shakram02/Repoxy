package mediators;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import network_io.ConnectionAcceptorIOHandler;
import network_io.interfaces.SocketIOer;
import org.jetbrains.annotations.NotNull;
import proxylet.Proxylet;
import utils.events.EventType;
import utils.SenderType;
import utils.events.SocketAddressInfoEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * The mediator registers for events from the controller side and switches side
 */
public class BaseMediator extends Proxylet {
    private final ConnectionAcceptorIOHandler switchSockets;

    private final EventBus controllerNotifier;
    private final AsyncEventBus verifiersNotifier;
    private final ArrayList<SocketIOer> controllerRegions; // Replace with connection creator

    public BaseMediator(ConnectionAcceptorIOHandler switchSockets) {
        super(SenderType.Mediator);
        this.controllerNotifier = new EventBus(BaseMediator.class.getName());
        this.controllerRegions = new ArrayList<>();

        // Run the event checkers on other threads
        this.verifiersNotifier = new AsyncEventBus(Executors.newCachedThreadPool());

        this.switchSockets = switchSockets;
        this.switchSockets.registerForEvents(this);
    }


    /**
     * Adds a controller to the list of registered controller
     * to receive socket events
     *
     * @param region Properly initialized {@link SocketIOer}
     */
    public void registerController(@NotNull SocketIOer region) {
        this.controllerRegions.add(region);
        // Controller registers for socket events
        region.registerForEvents(this);

        // Mediator notifies controller with events
        this.controllerNotifier.register(region);

    }

    /**
     * Registers a packet verifier for packet events
     *
     * @param verifier An object that contains event verification code
     */
    public void registerWatcher(@NotNull SocketEventObserver verifier) {
        this.verifiersNotifier.register(verifier);
    }

    /**
     * Someone notified the proxylet for an event, dispatch it to the correct
     * method
     * <p>
     * This method also receives event bus notifications when the Mediator
     * is registered to lower layers
     * <p>
     * This method is marked as synchronized as events from controllers
     * and from switches will need to access this method. thus, this
     * method is its synchronization point
     *
     * @param arg socket event data containing the Sender and Event types
     */
    @Subscribe
    public synchronized void dispatchEvent(@NotNull SocketEventArguments arg) {
        SenderType senderType = arg.getSenderType();

        SocketEventArguments redirected = arg.createRedirectedCopy(SenderType.Mediator);

        if (senderType == SenderType.SwitchesRegion) {
            this.controllerNotifier.post(redirected);
        } else {
            if (senderType == SenderType.ControllerRegion) {
                // Log non data events
                if (arg.getReplyType() != EventType.SendData) {
                    this.logger.info(arg.toString());
                }

                this.switchSockets.addToCommandQueue(redirected);
            } else if (senderType == SenderType.ReplicaRegion) {
                this.onReplicaEvent(arg);
            }
        }

        this.verifiersNotifier.post(arg);
    }


    private void onReplicaEvent(@NotNull SocketEventArguments arg) {
        if (arg.getReplyType() == EventType.Disconnection) {
            this.logger.warning("Replicated controller disconnected, this will be ignored");
        }
    }

    public void setActiveController(@NotNull String ip, int port) {
        SocketAddressInfoEventArg arg = new SocketAddressInfoEventArg(ip, port);
        this.controllerNotifier.post(arg);
    }

    @Override
    public void close() throws IOException {
        this.switchSockets.close();
        for (SocketIOer region : this.controllerRegions) {
            region.close();
        }
    }

    public void cycle() throws IOException {
        try {
            // Do socket io cycle
            this.switchSockets.cycle();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (SocketIOer region : this.controllerRegions) {
            region.cycle();
        }
    }

}
