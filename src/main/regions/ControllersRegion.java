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
        System.out.println(String.format("[ControllerRegion] %s", arg));

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
        if (Objects.equals(this.address, a.getIp()) && this.port == a.getPort()) {
            this.logger.log(Level.INFO, String.format("Controller [%d] is now activated", this.id));
            this.senderType = SenderType.ControllerRegion;
            ControllersRegion.activeController = this;
        } else {
            this.senderType = SenderType.ReplicaRegion;
        }
    }
}
