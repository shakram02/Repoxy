package regions;

import com.google.common.eventbus.Subscribe;
import network_io.ConnectionCreatorIOHandler;
import network_io.interfaces.ConnectionCreator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.SenderType;
import utils.SocketEventArg;

import java.io.IOException;

public final class ControllersRegion extends WatchedRegion implements ConnectionCreator {
    private static ControllersRegion activeController;
    private final ConnectionCreatorIOHandler ioHandler;
    private final String address;
    private final int port;

    public ControllersRegion(ConnectionCreatorIOHandler ioHandler, String address, int port) {
        super(ControllersRegion.class, ioHandler);
        this.ioHandler = ioHandler;
        this.address = address;
        this.port = port;

        // Set the first controller as the active one
        if (activeController == null) {
            ControllersRegion.activeController = this;
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

    @Subscribe
    public void mediatorEventHandler(SocketEventArg arg) {
        throw new NotImplementedException();
    }

    /**
     * As multiple controllers will be connected to the mediator.
     * the mediator needs to know whether the sender is an active
     * or an idle controller thus notifyMediator() needs to be
     * overridden to this behaviour.
     *
     * @param arg Event info
     */
    @Override
    protected void notifyMediator(SocketEventArg arg) {
        if (this == ControllersRegion.activeController) {
            arg = SocketEventArg.Redirect(SenderType.ControllerRegion, arg);
        } else {
            arg = SocketEventArg.Redirect(SenderType.ReplicaRegion, arg);
        }

        super.notifyMediator(arg);
    }
}
