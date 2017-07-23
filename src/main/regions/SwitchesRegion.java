package regions;

import network_io.ConnectionAcceptorIOHandler;
import network_io.interfaces.ConnectionAcceptor;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionIdEventArg;
import utils.EventType;
import utils.SenderType;
import utils.SocketEventArguments;

import java.io.IOException;


/**
 * I/O Handler for server socket
 */
public final class SwitchesRegion extends WatchedRegion implements ConnectionAcceptor {

    private final ConnectionAcceptorIOHandler ioHandler;

    public SwitchesRegion(@NotNull ConnectionAcceptorIOHandler ioHandler) {
        super(SenderType.SwitchesRegion, ioHandler);
        this.ioHandler = ioHandler;
    }

    public void startListening(@NotNull String address, int port) throws IOException {
        this.ioHandler.createServer(address, port);
        logger.info(String.format("Listening on [%s]", port));
    }

    @Override
    public void dispatchEvent(@NotNull SocketEventArguments arg) {
        EventType eventType = arg.getReplyType();
        System.out.println(String.format("[SwitchRegion] %s", arg));

        if (eventType == EventType.Disconnection || eventType == EventType.SendData) {
            super.dispatchEvent(arg);
        } else if (eventType == EventType.Connection) {
            assert arg instanceof ConnectionIdEventArg;
            ConnectionIdEventArg idEventArg = (ConnectionIdEventArg) arg;
            this.onConnectionAccepted(idEventArg);
        } else {
            assert false : "Invalid event:" + eventType;
        }
    }

    @Override
    public void onConnectionAccepted(@NotNull ConnectionIdEventArg arg) {
        System.out.println(String.format("Accepted [%s] on %s",
                arg.getId(), this.ioHandler.getConnectionInfo(arg.getId())));
        this.notifyMediator(arg);
    }
}
