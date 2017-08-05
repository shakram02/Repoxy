package mediators;

import com.google.common.eventbus.Subscribe;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ControllerIOHandler;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;


/**
 * The mediator registers for events from the controller side and switches side
 */
public class ProxyMediator implements Closeable, SocketEventObserver {
    private final ConnectionAcceptorIOHandler switchSockets;
    private final ArrayList<ControllerIOHandler> controllerHandlers;
    private final ArrayList<SocketEventObserver> packetWatchers;
    protected Logger logger;


    public ProxyMediator(ConnectionAcceptorIOHandler switchSockets) {

        // Run the event checkers on other threads
        this.controllerHandlers = new ArrayList<>();
        this.switchSockets = switchSockets;
        this.logger = Logger.getLogger(ProxyMediator.class.getName());

        packetWatchers = new ArrayList<>();
    }


    public void registerController(ControllerIOHandler region) {
        this.controllerHandlers.add(region);
    }

    /**
     * Registers a packet verifier for packet events
     *
     * @param verifier An object that contains event verification code
     */
    public void registerWatcher(@NotNull SocketEventObserver verifier) {
        this.packetWatchers.add(verifier);
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
            this.notifyControllers(arg);
        } else if (arg instanceof ControllerFailureArgs) {
            this.elevateSecondaryController();
        } else {
            if (senderType == SenderType.ControllerRegion) {

                // Log non data events
                if (arg.getReplyType() != EventType.SendData) {
                    this.logger.info(arg.toString());
                }

                if (arg.getReplyType() == EventType.Disconnection) {
                    this.logger.info("Elevating secondary controller, main one disconnected!!!");
                    this.elevateSecondaryController();
                } else {
                    this.switchSockets.addToCommandQueue(arg);
                }


            } else if (senderType == SenderType.ReplicaRegion) {
                this.onReplicaEvent(arg);
            }
        }

        this.notifyWatchers(arg);
    }

    private void notifyWatchers(SocketEventArguments arg) {
        for (SocketEventObserver observer : this.packetWatchers) {
            observer.dispatchEvent(arg);
        }
    }


    private void onReplicaEvent(@NotNull SocketEventArguments arg) {
        if (arg.getReplyType() == EventType.Disconnection) {
            this.logger.warning("Replicated controller disconnected, this will be ignored");
        }
    }

    public void setActiveController(@NotNull String ip, int port) {
        this.notifyControllers(new SocketAddressInfoEventArg(ip, port));
    }

    @Override
    public void close() throws IOException {
        this.switchSockets.close();
        this.closeControllers();
    }

    public void cycle() throws IOException {
        try {

            this.readNetworkIoEvents();
            this.readControllerIoEvents();

            this.cycleControllers();
            this.switchSockets.cycle();

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void readNetworkIoEvents() {
        Optional<SocketEventArguments> event = this.switchSockets.getOldestEvent();
        while (event.isPresent()) {
            this.dispatchEvent(event.get());
            event = this.switchSockets.getOldestEvent();
        }
    }

    private void readControllerIoEvents() {
        for (ControllerIOHandler controller : this.controllerHandlers) {
            this.dispatchAllControllerEvents(controller);
        }
    }

    private void dispatchAllControllerEvents(ControllerIOHandler controller) {
        Optional<SocketEventArguments> event = controller.getOldestEvent();
        while (event.isPresent()) {
            this.dispatchEvent(event.get());
            event = controller.getOldestEvent();
        }
    }

    private void cycleControllers() throws IOException {
        for (ControllerIOHandler controller : this.controllerHandlers) {
            controller.cycle();
        }
    }

    private void closeControllers() throws IOException {
        for (ControllerIOHandler controller : this.controllerHandlers) {
            controller.close();
        }
    }


    private void notifyControllers(SocketEventArguments arg) {
        for (ControllerIOHandler controller : this.controllerHandlers) {
            controller.addToCommandQueue(arg);
        }
    }

    public void elevateSecondaryController() {
        if (this.controllerHandlers.size() <= 1) {
            return;
        }

        ControllerIOHandler backup = this.controllerHandlers.get(1);
        this.setActiveController(backup.getAddress(), backup.getPort());
        this.controllerHandlers.remove(0);
    }
}
