package regions;

import utils.SocketEventArg;

import java.io.IOException;


/**
 * I/O Handler for server socket
 */
public final class SwitchesRegion extends WatchedRegion {

    public SwitchesRegion() {
        super(SwitchesRegion.class);
    }

    public void startListening(String address, int port) throws IOException {
        this.ioHandler.createServer(address, port);
        logger.info("Listening on [" + port + "]");
    }

    @Override
    public void onConnection(SocketEventArg arg) {
        logger.info("Accepted [" + arg.getId().toString() + "]: "
                + this.ioHandler.getRemoteAddress(arg.getId()));
    }

    @Override
    protected void onDisconnect(SocketEventArg arg) {
        super.onDisconnect(arg);
        this.logger.info("[" + this.ioHandler.getRemoteAddress(arg.getId()) + "] Disconnected");
    }
}
