package regions;

import com.google.common.eventbus.EventBus;
import mediators.BaseMediator;
import network_io.CommonIOHandler;
import network_io.interfaces.BasicSocketIOCommands;
import network_io.interfaces.BasicSocketIOWatcher;
import proxylet.Proxylet;
import utils.SenderType;
import utils.SocketEventArg;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Common implementation for I/O events
 */
public abstract class WatchedRegion extends Proxylet implements BasicSocketIOWatcher, BasicSocketIOCommands {
    private final CommonIOHandler ioHandler;

    // Mediator notification is done only through notifyMediator method
    private BaseMediator mediator;
    private EventBus mediatorNotifier;

    /**
     * This layer is between the sockets and mediator
     *
     * @param childClass type of the overriding class, for logging
     */
    public WatchedRegion(Class<?> childClass, CommonIOHandler commander) {
        super(childClass);
        this.mediatorNotifier = new EventBus(childClass.getName());
        this.ioHandler = commander;
    }

    /**
     * This method is protected as a sub-class should implement
     * its own mediator setting either through constructor (SwitchesRegion)
     * or through methods (ControllerRegion) as multiple controllers
     * need to be handled, so it hides all the inactive ControllerRegions.
     *
     * @param mediator Mediator to notify
     */
    protected void setMediator(BaseMediator mediator) {
        if (this.mediator != null) {
            throw new IllegalStateException("Each region notifies only one mediator");
        }
        this.mediator = mediator;
        this.mediatorNotifier.register(mediator);
    }

    /**
     * All watchers notify the mediators when
     * someone disconnects
     *
     * @param arg disconnecting element info
     */
    @Override
    public void onDisconnect(SocketEventArg arg) {
        this.notifyMediator(arg);
    }

    /**
     * All watchers notify the mediators when
     * data arrives
     *
     * @param arg data info
     */
    @Override
    public void onData(SocketEventArg arg) {
        this.notifyMediator(arg);
    }


    /**
     * Someone wants the region to send a message to socket layer
     *
     * @param arg Event argument containing what to send
     */
    @Override
    public void sendData(SocketEventArg arg) {
        // a sendTo coming from a Terminal will always throw an exception
        this.ioHandler.sendData(arg);
    }

    @Override
    public void closeConnection(SocketEventArg arg) {
        this.ioHandler.closeConnection(arg);
    }

    @Override
    public void cycle() {
        try {
            this.ioHandler.cycle();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        this.ioHandler.close();
    }

    protected void notifyMediator(SocketEventArg arg) {
        assert this.mediator != null;
        this.mediatorNotifier.post(arg);
    }
}
