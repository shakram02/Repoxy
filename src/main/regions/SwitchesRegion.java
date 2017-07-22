package regions;

import network_io.ConnectionAcceptorIOHandler;
import network_io.interfaces.ConnectionAcceptor;
import org.jetbrains.annotations.NotNull;
import utils.EventType;
import utils.SenderType;
import utils.SocketEventArg;

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
    public void dispatchEvent(@NotNull SocketEventArg arg) {
        EventType eventType = arg.getReplyType();
        System.out.println(String.format("[SwitchRegion] %s", arg));

        if (eventType == EventType.Disconnection || eventType == EventType.SendData) {
            super.dispatchEvent(arg);
        } else if (eventType == EventType.Connection) {
            this.onConnectionAccepted(arg);
        } else {
            assert false : "Invalid event:" + eventType;
        }
    }

    @Override
    public void onConnectionAccepted(@NotNull SocketEventArg arg) {
        System.out.println(String.format("Accepted [%s] on %s",
                arg.getId(), this.ioHandler.getConnectionInfo(arg.getId())));
        this.notifyMediator(arg);
    }
}
