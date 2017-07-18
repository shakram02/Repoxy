package network_io;

import base_classes.ConnectionId;
import base_classes.WatchedRegion;

import java.io.IOException;
import java.util.ArrayList;

/**
 * I/O Handler for server socket
 */
public final class SwitchIOHandler extends WatchedRegion {

    public SwitchIOHandler() {
        super(SwitchIOHandler.class);
    }

    public void startListening(String address, int port) throws IOException {
        this.ioHandler.createServer(address, port);
        logger.info("Listening on [" + port + "]");
    }

    @Override
    public void onConnection(ConnectionId id) {

        logger.info("Accepted [" + id.toString() + "]: "
                + this.ioHandler.getRemoteAddress(id));

        byte[] msg = "aaa\n".getBytes();
        ArrayList<Byte> bytes = new ArrayList<>();
        for (byte b : msg) {
            bytes.add(b);
        }

        this.sendTo(id, bytes);
    }
}
