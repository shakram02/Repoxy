package network_io;

import base_classes.ConnectionId;
import base_classes.WatchedRegion;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Logger;

/**
 * I/O Handler for server socket
 */
public class ServerIOHandler extends SelectIOHandler {
    private ServerSocketChannel server;
    private static final Logger logger = Logger.getLogger(ServerIOHandler.class.getName());

    public ServerIOHandler(WatchedRegion region) throws IOException {
        super(region, region.getPacketBuffer());
    }

    @Override
    public void cycle() throws IOException {
        int count = this.selectNow();
        if (count == 0) {
            return;
        }

        Set<SelectionKey> keys = this.getSelectedKeys();
        for (SelectionKey key : keys) {
            keys.remove(key);

            if (key.isAcceptable()) {
                SocketChannel client = server.accept();
                this.acceptClient(client);

            } else {
                this.handleKey(key);
            }

        }
    }

    public void startListening(String address, int port) throws IOException {
        this.server = ServerSocketChannel.open();
        this.server.configureBlocking(false);
        this.addServer(new ConnectionId(), this.server);
        this.server.socket().bind(new InetSocketAddress(address, port));
        logger.info("Listening to: " + this.server.getLocalAddress().toString());
    }

    private void acceptClient(SocketChannel client) throws IOException {
        ConnectionId id = new ConnectionId();
        logger.info("Accepted [" + id.toString() + "]: " + client.getRemoteAddress().toString());
        client.configureBlocking(false);
        this.addConnection(id, client);
    }
}
