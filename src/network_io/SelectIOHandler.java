package network_io;

import utils.*;
import proxylet.Proxylet;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.Vector;

/**
 * Handles low level socket I/O operations
 */
public class SelectIOHandler implements Closeable {
    public static int BUFFER_SIZE = 1;
    private Selector selector;  // Selector is made private to avoid confusion in sub classes
    private HashBiMap<SelectionKey, ConnectionId> keyMap;
    private PacketBuffer packetBuffer;
    private ByteBuffer buffer;
    private Proxylet proxylet;

    public SelectIOHandler(Proxylet proxylet, PacketBuffer packetBuffer)
            throws IOException {
        this.selector = Selector.open();
        this.keyMap = HashBiMap.create();
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);

        this.packetBuffer = packetBuffer;
        this.proxylet = proxylet;
    }

    public void cycle() throws IOException {
        int count = this.selector.selectNow();
        if (count == 0) {
            return;
        }
        // TODO will be used later when having threads for controller and switches
        // this.selector.select();
        Set<SelectionKey> keys = this.selector.selectedKeys();
        for (SelectionKey key : keys) {
            keys.remove(key);
            this.handleKey(key);
        }
    }

    /**
     * Called by upper classes Cycle() method
     *
     * @param key: SelectionKey in question
     * @throws IOException When socket I/O operation fails
     */
    private void handleKey(SelectionKey key) throws IOException {

        if (key.isAcceptable()) {
            ServerSocketChannel ch = (ServerSocketChannel) key.channel();
            this.onConnection(ch);
            return;
        }

        SocketChannel channel = (SocketChannel) key.channel();
        ConnectionId id = keyMap.get(key);

        if (key.isReadable()) {

            int read = channel.read(buffer);
            if (read == -1) {
                this.onDisconnected(id);
                return;
            }

            this.onData(id, channel, read);
        }
        if (key.isWritable()) {
            this.onWritable(id, channel);
        }

        if (key.isConnectable()) {
            this.onConnectable(key, channel);
        }
    }

    private void onConnectable(SelectionKey key, SocketChannel channel) throws IOException {
        key.interestOps(SelectionKey.OP_READ);
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }

        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        this.proxylet.dispatchEvent(new SocketEventArg(SenderType.Socket, EventType.Connection, id));
    }

    private void onWritable(ConnectionId id, SocketChannel channel) throws IOException {
        this.writePackets(id, channel);

        this.proxylet.dispatchEvent(new SocketEventArg(SenderType.Socket,
                EventType.DataOut, id));
    }

    private void onData(ConnectionId id, SocketChannel channel, int read) throws IOException {
        Vector<Byte> data = readRemainingBytes(channel, read);

        this.proxylet.dispatchEvent(new SocketEventArg(SenderType.Socket,
                EventType.DataIn, id, data));
    }


    private void onConnection(ServerSocketChannel server) throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);

        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        this.proxylet.dispatchEvent(new SocketEventArg(SenderType.Socket,
                EventType.Connection, id));
    }

    private void onDisconnected(ConnectionId id) throws IOException {
        SelectionKey key = this.keyMap.inverse().get(id);
        this.keyMap.remove(key);

        key.cancel();
        key.channel().close();

        this.proxylet.dispatchEvent(new SocketEventArg(SenderType.Socket,
                EventType.Disconnection, id));
    }

    public void createServer(String address, int port) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        SelectionKey key = server.register(this.selector, SelectionKey.OP_ACCEPT);

        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        server.socket().bind(new InetSocketAddress(address, port));
    }

    public void createConnection(String address, int port) throws IOException {
        SocketChannel client = SocketChannel.open();
        client.configureBlocking(false);

        client.connect(new InetSocketAddress(address, port));
        client.register(this.selector, SelectionKey.OP_CONNECT);

    }

    public void addOutput(ConnectionId id) {
        SelectionKey key = this.keyMap.inverse().get(id);
        int oldOps = key.interestOps();
        key.interestOps(oldOps | SelectionKey.OP_WRITE);
    }

    public void removeOutput(ConnectionId id) {
        SelectionKey key = this.keyMap.inverse().get(id);
        int oldOps = key.interestOps();
        key.interestOps(oldOps & ~SelectionKey.OP_WRITE);
    }


    /**
     * Reads the remaining amount of bytes after a first successful read
     *
     * @param channel socket channel to be read
     * @return read bytes
     * @throws IOException Socket I/O Error
     */
    @NotNull
    private Vector<Byte> readRemainingBytes(SocketChannel channel, int count)
            throws IOException {
        Vector<Byte> bytes = new Vector<>(BUFFER_SIZE * 2);

        while (count > 0) {
            buffer.rewind();
            for (int i = 0; i < count; i++) {
                bytes.add(buffer.get());
            }
            buffer.clear();
            count = channel.read(buffer);
        }
        return bytes;
    }


    private void writePackets(ConnectionId id, SocketChannel channel)
            throws IOException {
        while (this.packetBuffer.hasPendingPackets(id)) {
            channel.write(this.packetBuffer.getNextPacket(id));
        }
    }

    @NotNull
    public String getRemoteAddress(ConnectionId id) {
        // The exception is irrelevant as the channel is always connected,
        // that's why it's handled here
        try {
            SocketChannel ch = (SocketChannel) this.keyMap.inverse().get(id).channel();
            return ch.getRemoteAddress().toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        // Un-register for selection event and close connection
        for (SelectionKey key : this.keyMap.keySet()) {
            key.channel().close();
        }
    }
}
