package regions;

import network_io.ConnectionCreatorIOHandler;
import network_io.interfaces.ConnectionCreator;
import org.jetbrains.annotations.NotNull;
import utils.ControllerChangeEventArg;
import utils.EventType;
import utils.SenderType;
import utils.SocketEventArg;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

/**
 * A logical mapping for a controller, a controller might be an active
 * or a secondary one. Each controller is identified by its ip and port
 * <p>
 * Another function this class is doing is passing all bidirectional
 * events through the {@link ControllersRegion#dispatchEvent(SocketEventArg)} method, this enables logging
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
     * @param arg Event info
     */
    @Override
    public void dispatchEvent(@NotNull SocketEventArg arg) {
        EventType eventType = arg.getReplyType();

        String state = this == ControllersRegion.activeController ? "Active" : "Replicated";
        System.out.println(String.format("[%s-ControllerRegion] %s", state, arg));

        // If you're not creating a new connection and the receiver isn't alive
        // and you're not changing controllers (as this doesn't perform any I/O)
        if (eventType != EventType.Connection
                && eventType != EventType.ChangeController
                && !this.ioHandler.isReceiverAlive(arg)) {
            throw new IllegalStateException(String.format("Event receiver for {%s} isn't alive", arg));
        }

        if (eventType == EventType.Disconnection || eventType == EventType.SendData) {
            super.dispatchEvent(arg);
        } else if (eventType == EventType.ChangeController) {
            this.changeActiveController(arg);
        } else if (eventType == EventType.Connection) {
            this.connectTo(arg);
        } else {
            throw new RuntimeException("Invalid event:" + eventType);
        }
    }

    /**
     * Creates a new connection to the controller when a client
     * connects to {@link SwitchesRegion}
     *
     * @param args
     */
    @Override
    public void connectTo(@NotNull SocketEventArg args) {
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
    private void changeActiveController(@NotNull SocketEventArg arg) {

        ControllerChangeEventArg a = (ControllerChangeEventArg) arg;
        // If I'm the selected controller and I'm not the active one
        if (Objects.equals(this.address, a.getIp()) && this.port == a.getPort() &&
                ControllersRegion.activeController != this) {
            this.logger.log(Level.INFO, String.format("Controller on port [%d] is now activated", this.port));
            this.senderType = SenderType.ControllerRegion;
            ControllersRegion.activeController = this;
        } else {
            this.senderType = SenderType.ReplicaRegion;
        }
    }
}
