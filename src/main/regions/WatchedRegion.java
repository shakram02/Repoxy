package regions;

import com.google.common.eventbus.EventBus;
import mediators.BaseMediator;
import network_io.CommonIOHandler;
import network_io.interfaces.BasicSocketIOCommands;
import network_io.interfaces.BasicSocketIOWatcher;
import org.jetbrains.annotations.NotNull;
import proxylet.Proxylet;
import utils.ConnectionId;
import utils.EventType;
import utils.SenderType;
import utils.SocketEventArg;

import java.io.IOException;

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
     * @param senderType type of the overriding class, for logging
     */
    public WatchedRegion(@NotNull SenderType senderType, @NotNull CommonIOHandler commander) {
        super(senderType);
        this.mediatorNotifier = new EventBus(senderType.toString());
        this.ioHandler = commander;
    }

    public void setMediator(@NotNull BaseMediator mediator) {
        assert this.mediator == null : "Mediator is being set twice";

        this.mediator = mediator;
        this.mediatorNotifier.register(mediator);
    }

    @Override
    public void dispatchEvent(@NotNull SocketEventArg arg) {
        // Check before performing socket I/O operations
        assert this.isReceiverAlive(arg) : String.format("Event receiver for {%s} isn't alive", arg);

        EventType eventType = arg.getReplyType();

        if (eventType == EventType.Disconnection) {
            this.onDisconnect(arg);
        } else if (eventType == EventType.SendData) {
            this.sendData(arg);
        } else {
            assert false : "Invalid event sender: " + arg;
        }
    }

    /**
     * All watchers notify the mediators when
     * someone disconnects
     *
     * @param arg disconnecting element info
     */
    @Override
    public void onDisconnect(@NotNull SocketEventArg arg) {
        SenderType sender = arg.getSenderType();
        if (sender == SenderType.Socket) {
            this.notifyMediator(arg);
        } else if (sender == SenderType.Mediator) {
            this.ioHandler.closeConnection(arg);
        } else {
            assert false : "Invalid event sender: " + arg;
        }
    }

    /**
     * All watchers notify the mediators when
     * data arrives
     *
     * @param arg data info
     */
    @Override
    public void onData(@NotNull SocketEventArg arg) {
        this.notifyMediator(arg);
    }

    @Override
    public boolean isReceiverAlive(@NotNull SocketEventArg arg) {
        return this.ioHandler.isReceiverAlive(arg);
    }


    @NotNull
    @Override
    public String getConnectionInfo(@NotNull ConnectionId id) {
        return this.ioHandler.getConnectionInfo(id);
    }

    /**
     * Someone wants the region to send a message to socket layer
     *
     * @param arg Event argument containing what to send
     */
    @Override
    public void sendData(@NotNull SocketEventArg arg) {
        // a sendTo coming from a Terminal will always throw an exception
        this.ioHandler.sendData(arg);
    }

    @Override
    public void closeConnection(@NotNull SocketEventArg arg) {
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

    protected final void notifyMediator(SocketEventArg arg) {
        arg = SocketEventArg.Redirect(this.senderType, arg);
        this.mediatorNotifier.post(arg);
    }
}
