package mediators;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ControllerIOHandler;
import network_io.ControllerManager;
import network_io.interfaces.SocketIOer;
import org.jetbrains.annotations.NotNull;
import proxylet.Proxylet;
import utils.events.*;
import utils.SenderType;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * The mediator registers for events from the controller side and switches side
 */
public class BaseMediator extends Proxylet {
    private final ConnectionAcceptorIOHandler switchSockets;
    private final ControllerManager controllerManager;
    private final AsyncEventBus verifiersNotifier;


    public BaseMediator(ConnectionAcceptorIOHandler switchSockets) {
        super(SenderType.Mediator);

        // Run the event checkers on other threads
        this.verifiersNotifier = new AsyncEventBus(Executors.newCachedThreadPool());
        this.controllerManager = new ControllerManager(this);

        this.switchSockets = switchSockets;
        this.switchSockets.registerForEvents(this);

    }


    public void registerController(ControllerIOHandler region) {
        this.controllerManager.registerController(region);
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

        if (senderType == SenderType.SwitchesRegion) {
            // TODO notify the main controller first using the main thread
            this.controllerManager.notifyControllers(arg);
        } else if (arg instanceof ControllerFailureArgs) {
            this.controllerManager.elevateSecondaryController();
        } else {
            if (senderType == SenderType.ControllerRegion) {

                // Log non data events
                if (arg.getReplyType() != EventType.SendData) {
                    this.logger.info(arg.toString());
                }

                if (arg.getReplyType() == EventType.Disconnection) {
                    this.logger.info("Elevating secondary controller, main one disconnected!!!");
                    this.controllerManager.elevateSecondaryController();
                } else {
                    this.switchSockets.addToCommandQueue(arg);
                }


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
        // TODO change the main controller first using the main thread
        this.controllerManager.setActiveController(ip, port);
    }

    @Override
    public void close() throws IOException {
        this.switchSockets.close();
        this.controllerManager.closeControllers();
    }

    public void cycle() throws IOException {
        try {
            // Do socket io cycle
            this.switchSockets.cycle();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        this.controllerManager.cycleControllers();
    }

}
