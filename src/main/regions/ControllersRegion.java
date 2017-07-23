package regions;

import network_io.ConnectionCreatorIOHandler;
import network_io.interfaces.ConnectionCreator;
import org.jetbrains.annotations.NotNull;
import utils.*;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

/**
 * A logical mapping for a controller, a controller might be an active
 * or a secondary one. Each controller is identified by its ip and port
 * <p>
 * Another function this class is doing is passing all bidirectional
 * events through the {@link ControllersRegion#dispatchEvent(SocketEventArguments)} method, this enables logging
 * and doing actions at controller level.
 * <p>
 * A controller extends a {@link WatchedRegion} by the ability
 * to create connections
 */
public final class ControllersRegion extends WatchedRegion implements ConnectionCreator {
    private static ControllersRegion activeController;

    private final ConnectionCreatorIOHandler ioHandler;
    private final String address;
    private final int port;

    public ControllersRegion(@NotNull ConnectionCreatorIOHandler ioHandler, @NotNull String address, int port) {
        super(SenderType.ReplicaRegion, ioHandler);
        this.ioHandler = ioHandler;
        this.address = address;
        this.port = port;

        // Set the first controller as the active one
        if (activeController == null) {
            ControllersRegion.activeController = this;
            this.senderType = SenderType.ControllerRegion;
        }
    }

    /**
     * Dispatch events coming from the mediator
     *
     * @param arg Holds the type of the event and the
     *            sender type
     */
    @Override
    public void dispatchEvent(@NotNull SocketEventArguments arg) {
        EventType eventType = arg.getReplyType();

        // Log the main controller stuff
        String state = this == ControllersRegion.activeController ? "Active" : "Replicated";
        if (this.senderType == SenderType.ControllerRegion) {
            this.logger.info(String.format("[%s-ControllerRegion] %s", state, arg));
        } else {
            this.logger.finest(String.format("[%s-ControllerRegion] %s", state, arg));
        }

        if (eventType == EventType.ChangeController) {
            this.changeActiveController(arg);
            return;
        }

        assert arg instanceof ConnectionIdEventArg;
        ConnectionIdEventArg idEventArg = (ConnectionIdEventArg) arg;
        // If you're not creating a new connection and the receiver isn't alive
        // and you're not changing controllers (as this doesn't perform any I/O)
        if (eventType != EventType.Connection
                && !this.ioHandler.isReceiverAlive(idEventArg)) {

            // Recover if the controller is the target by reconnecting
            // and terminating this event, as the controller will send a HELLO.
            // and ignore if this is a replicated controller
            if (this.senderType == SenderType.ControllerRegion) {
                this.logger.log(Level.SEVERE, "## Trying to re-connect to main controller ##");
                this.restartConnection(idEventArg);
            }

            return;
        }

        if (eventType == EventType.Disconnection || eventType == EventType.SendData) {
            super.dispatchEvent(idEventArg);
        } else if (eventType == EventType.Connection) {
            this.connectTo(idEventArg);
        } else {
            assert false : "Illegal event " + eventType;
        }
    }

    private void restartConnection(ConnectionIdEventArg arg) {
        this.closeConnection(arg);
        this.connectTo(arg);
    }

    /**
     * Creates a new connection to the controller when a client
     * connects to {@link SwitchesRegion}
     *
     * @param args Event argument containing the ip address and
     *             port of the new connection
     */
    @Override
    public void connectTo(@NotNull ConnectionIdEventArg args) {
        try {
            this.ioHandler.createConnection(this.address, this.port, args.getId());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * As multiple controllers will be connected to the mediator.
     * the mediator needs to know whether the sender is an active
     * or an idle controller
     *
     * @param arg Event info
     */
    private void changeActiveController(@NotNull SocketEventArguments arg) {
        assert arg instanceof ControllerChangeEventArg;

        ControllerChangeEventArg a = (ControllerChangeEventArg) arg;

        // If I'm the selected controller and I'm not the active one
        if (Objects.equals(this.address, a.getIp()) && this.port == a.getPort() &&
                ControllersRegion.activeController != this) {

            this.senderType = SenderType.ControllerRegion;
            ControllersRegion.activeController = this;

        } else {
            this.senderType = SenderType.ReplicaRegion;
        }
    }
}
