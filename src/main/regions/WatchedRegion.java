package regions;

import com.google.common.eventbus.EventBus;
import mediators.BaseMediator;
import network_io.CommonIOHandler;
import network_io.interfaces.BasicSocketIOCommands;
import network_io.interfaces.BasicSocketIOWatcher;
import org.jetbrains.annotations.NotNull;
import proxylet.Proxylet;
import utils.*;

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
    public void dispatchEvent(@NotNull SocketEventArguments arg) {
        // Check before performing socket I/O operations
        assert arg instanceof ConnectionIdEventArg;
        ConnectionIdEventArg idEventArg = (ConnectionIdEventArg) arg;

        // Re notification of a disconnected controller,
        // ignore it because it already disconnected
        if (arg.getReplyType() == EventType.Disconnection
                && !this.isReceiverAlive(idEventArg)) {
            return;
        }

        assert this.isReceiverAlive(idEventArg) : String.format("Event receiver for {%s} isn't alive", idEventArg);

        EventType eventType = idEventArg.getReplyType();

        if (eventType == EventType.Disconnection) {
            this.onDisconnect(idEventArg);
        } else if (eventType == EventType.SendData) {
            assert idEventArg instanceof SocketDataEventArg;
            SocketDataEventArg dataEventArg = (SocketDataEventArg) idEventArg;
            this.sendData(dataEventArg);
        } else {
            assert false : "Invalid event sender: " + idEventArg;
        }
    }

    /**
     * All watchers notify the mediators when
     * someone disconnects
     *
     * @param arg disconnecting element info
     */
    @Override
    public void onDisconnect(@NotNull ConnectionIdEventArg arg) {
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
    public void onData(@NotNull SocketDataEventArg arg) {
        this.notifyMediator(arg);
    }

    @Override
    public boolean isReceiverAlive(@NotNull ConnectionIdEventArg arg) {
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
    public void sendData(@NotNull SocketDataEventArg arg) {
        // a sendTo coming from a Terminal will always throw an exception
        this.ioHandler.sendData(arg);
    }

    @Override
    public void closeConnection(@NotNull ConnectionIdEventArg arg) {
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

    protected final void notifyMediator(SocketEventArguments arg) {
        arg = arg.createRedirectedCopy(this.senderType);
        this.mediatorNotifier.post(arg);
    }
}
