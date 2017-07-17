package network_io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * I/O Handler for server socket
 */
public class ServerIOHandler {
    private SocketChannel server;
    private Selector selector;
    private static final Logger logger = Logger.getLogger(ServerIOHandler.class.getName());

    public ServerIOHandler() throws IOException {
        server = SocketChannel.open();
        server.configureBlocking(false);
        selector = Selector.open();
        server.register(selector, SelectionKey.OP_CONNECT);
    }

    public void startListening(String address, int port) {
        try {
            server.socket().bind(new InetSocketAddress(address, port));
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
