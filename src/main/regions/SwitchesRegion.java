package regions;

import network_io.ConnectionAcceptorIOHandler;
import network_io.interfaces.ConnectionAcceptor;
import utils.SocketEventArg;

import java.io.IOException;


/**
 * I/O Handler for server socket
 */
public final class SwitchesRegion extends WatchedRegion implements ConnectionAcceptor {

    private final ConnectionAcceptorIOHandler ioHandler;

    public SwitchesRegion(ConnectionAcceptorIOHandler ioHandler) {
        super(SwitchesRegion.class, ioHandler);
        this.ioHandler = ioHandler;
    }

    public void startListening(String address, int port) throws IOException {
        this.ioHandler.createServer(address, port);
        logger.info("Listening on [" + port + "]");
    }

    @Override
    public void onConnectionAccepted(SocketEventArg arg) {
        this.notifyMediator(arg);
    }
}
