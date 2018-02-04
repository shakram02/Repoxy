package mediators;

import middleware.MiddlewareManager;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ControllerIOHandler;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.*;
import utils.events.ImmutableSocketAddressInfoEventArg;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;


/**
 * The mediator registers for utils.events from the controller side and switches side
 */
public class ProxyMediator implements Closeable, SocketEventObserver {
    private final ConnectionAcceptorIOHandler switchSockets;
    private final ConcurrentLinkedQueue<ControllerIOHandler> controllerHandlers;
    private final ArrayList<SocketEventObserver> packetWatchers;
    private final MiddlewareManager middlewareManager;
    protected Logger logger;


    public ProxyMediator(MiddlewareManager middlewareManager, ConnectionAcceptorIOHandler switchSockets) {
        // The middleware manager compares packets from the 2 controllers and going to switches to
        // find out which packets timed out.
        this.middlewareManager = middlewareManager;

        // Run the event checkers on other threads
        this.controllerHandlers = new ConcurrentLinkedQueue<>();
        this.switchSockets = switchSockets;
        this.logger = Logger.getLogger(ProxyMediator.class.getName());

        packetWatchers = new ArrayList<>();
    }


    public void registerController(ControllerIOHandler region) {
        this.controllerHandlers.add(region);
    }

    /**
     * Registers a packet verifier for packet utils.events
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
     * This method is marked as synchronized as utils.events from controllers
     * and from switches will need to access this method. thus, this
     * method is its synchronization point
     *
     * @param arg socket event data containing the Sender and Event types
     */
    public synchronized void dispatchEvent(@NotNull SocketEventArguments arg) {
        SenderType senderType = arg.getSenderType();

        if (senderType == SenderType.SwitchesRegion) {
            // TODO notify the main controller first using the main thread\
            this.notifyControllers(arg);
        } else if (arg instanceof ControllerFailureArgs) {
            this.elevateSecondaryController();
        } else {
            // TODO insert packet into middleware pipeline
            if (senderType == SenderType.ControllerRegion) {

                // Log non data utils.events
                if (arg.getReplyType() != EventType.SendData) {
                    this.logger.info(arg.toString());
                }

                if (arg.getReplyType() == EventType.Disconnection) {
                    this.logger.info("Elevating secondary controller, main one disconnected!!!");
                    this.elevateSecondaryController();
                } else {
                    throw new IllegalStateException("Invalid branch, data sending is handled by middleware");
                }

            } else if (senderType == SenderType.ReplicaRegion) {
                this.onReplicaEvent(arg);
            }
        }
    }

    private void onReplicaEvent(@NotNull SocketEventArguments arg) {
        if (arg.getReplyType() == EventType.Disconnection) {
            this.logger.warning("Replicated controller disconnected, this will be ignored");
        }
    }

    public void setActiveController(@NotNull String ip, int port) {
        this.notifyControllers(ImmutableSocketAddressInfoEventArg.builder()
                .ip(ip).port(port).build());
    }

    @Override
    public void close() throws IOException {
        this.switchSockets.close();
        this.closeControllers();
    }

    public void cycle() {
        try {
            this.switchSockets.cycle();
            ArrayList<SocketEventArguments> networkIoEvents = this.readNetworkIoEvents();
            notifyAndProcessEvents(networkIoEvents);

            this.cycleControllers();
            ArrayList<SocketEventArguments> controllerIoEvents = this.readControllerIoEvents();
            notifyAndProcessEvents(controllerIoEvents);

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private ArrayList<SocketEventArguments> readNetworkIoEvents() {
        ArrayList<SocketEventArguments> events = new ArrayList<>();

        Optional<SocketEventArguments> event = this.switchSockets.getOldestEvent();
        while (event.isPresent()) {
            SocketEventArguments arg = event.get();
            events.add(arg);

            event = this.switchSockets.getOldestEvent();
        }

        return events;
    }

    private ArrayList<SocketEventArguments> readControllerIoEvents() {
        ArrayList<SocketEventArguments> events = new ArrayList<>();

        for (ControllerIOHandler controller : this.controllerHandlers) {
            // TODO: make the controllerIOHandler a middleware?
            Optional<SocketEventArguments> event = controller.getOldestEvent();

            while (event.isPresent()) {
                SocketEventArguments arg = event.get();
                events.add(arg);

                event = controller.getOldestEvent();
            }
        }

        return events;
    }

    private void notifyAndProcessEvents(List<SocketEventArguments> eventArguments) {
        for (SocketEventArguments arg : eventArguments) {
            notifyAndProcessEvent(arg);
        }
    }

    private void notifyAndProcessEvent(SocketEventArguments arg) {
        this.notifyWatchers(arg);

        if (arg.getSenderType() == SenderType.SwitchesRegion || arg.getReplyType() != EventType.SendData) {
            this.dispatchEvent(arg);
            return;
        }

        middlewareManager.addToPipeline((SocketDataEventArg) arg);

        while (middlewareManager.hasOutput()) {
            SocketDataEventArg dataEventArg = middlewareManager.getOutput();
            this.switchSockets.addInput(dataEventArg);
        }
    }

    private void notifyWatchers(SocketEventArguments arg) {
        for (SocketEventObserver observer : this.packetWatchers) {
            observer.dispatchEvent(arg);
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
            controller.addInput(arg);
        }
    }

    public void elevateSecondaryController() {
        if (this.controllerHandlers.size() <= 1) {
            return;
        }

        this.middlewareManager.setMainControllerAlive(false);
        // HACK: old handler must be removed because it's now disconnected
        ControllerIOHandler old = this.controllerHandlers.remove();
        ControllerIOHandler backup = this.controllerHandlers.peek();
        Objects.requireNonNull(backup);
        this.setActiveController(backup.getAddress(), backup.getPort());
    }
}
