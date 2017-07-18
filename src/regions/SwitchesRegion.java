package regions;

import utils.ConnectionId;

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
    public void onConnection(ConnectionId id) {
        logger.info("Accepted [" + id.toString() + "]: "
                + this.ioHandler.getRemoteAddress(id));
    }

    @Override
    protected void onDisconnect(ConnectionId id) {
        super.onDisconnect(id);
        this.logger.info("[" + this.ioHandler.getRemoteAddress(id) + "] Disconnected");
    }
}
