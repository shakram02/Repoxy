package network_io;

import network_io.interfaces.ConnectionCreator;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Creates connections to controller
 */
public class ConnectionCreatorIOHandler extends CommonIOHandler {

    /**
     * Called by upper classes Cycle() method
     *
     * @param key: SelectionKey in question
     * @throws IOException When socket I/O operation fails
     */
    @Override
    protected void handleSpecialKey(@NotNull SelectionKey key) throws IOException {

        if (key.isValid() && key.isConnectable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            this.onConnectable(key, channel);
        }
    }

    private void onConnectable(@NotNull SelectionKey key, @NotNull SocketChannel channel) throws IOException {

        if (channel.isConnectionPending()) {
            try {
                channel.finishConnect();
            } catch (IOException e) {
                this.keyMap.remove(key);
                throw e;
            }
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    public void createConnection(@NotNull String address, int port, @NotNull ConnectionId id) throws IOException {
        SocketChannel client = SocketChannel.open();
        client.configureBlocking(false);

        client.connect(new InetSocketAddress(address, port));

        SelectionKey key = client.register(this.selector, SelectionKey.OP_CONNECT);
        this.keyMap.put(key, id);
    }

    public void setUpperLayer(@NotNull ConnectionCreator upperLayer) {
        super.setUpperLayer(upperLayer);
    }
}
