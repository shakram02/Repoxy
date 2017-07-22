package regions;

import network_io.ConnectionCreatorIOHandler;
import network_io.interfaces.ConnectionCreator;
import utils.ControllerChangeEventArg;
import utils.EventType;
import utils.SenderType;
import utils.SocketEventArg;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public final class ControllersRegion extends WatchedRegion implements ConnectionCreator {
    private static int CONTROLLER_ID;
    private static ControllersRegion activeController;

    private final ConnectionCreatorIOHandler ioHandler;
    private final String address;
    private final int port;
    private final int id;

    public ControllersRegion(ConnectionCreatorIOHandler ioHandler, String address, int port) {
        super(SenderType.ReplicaRegion, ioHandler);
        this.ioHandler = ioHandler;
        this.address = address;
        this.port = port;
        this.id = CONTROLLER_ID++;

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
    public void dispatchEvent(SocketEventArg arg) {
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

    @Override
    public void connectTo(SocketEventArg args) {
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
    private void changeActiveController(SocketEventArg arg) {

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
