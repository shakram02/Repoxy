package network_io;

import base_classes.ConnectionId;
import base_classes.Proxylet;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;
import java.util.Vector;

/**
 * Handles low level socket I/O operations
 */
public class SelectIOHandler {
    private Selector selector;
    private HashBiMap<SelectionKey, ConnectionId> connectionIdHashMap;
    protected final Proxylet proxylet;
    public static int BUFFER_SIZE = 1;
    private PacketBuffer packetBuffer;
    private ByteBuffer buffer;

    public SelectIOHandler(Proxylet proxylet, PacketBuffer packetBuffer)
            throws IOException {
        this.packetBuffer = packetBuffer;
        this.selector = Selector.open();
        this.proxylet = proxylet;
        this.connectionIdHashMap = HashBiMap.create();
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    public void cycle() throws IOException {
        int count = this.selector.selectNow();
        if (count == 0) {
            return;
        }

        for (SelectionKey key : this.selector.selectedKeys()) {
            this.handleKey(key);
        }
    }

    /**
     * Called by upper classes Cycle() method
     *
     * @param key: SelectionKey in question
     * @throws IOException When socket I/O operation fails
     */
    public void handleKey(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ConnectionId id = connectionIdHashMap.get(key);

        if (key.isReadable()) {

            int read = channel.read(buffer);
            if (read == -1) {
                this.proxylet.onDisconnect(id);
                this.removeConnection(id);
                return;
            }

            Vector<Byte> data = readRemainingBytes(channel, read);
            this.proxylet.onData(id, data);
        }
        if (key.isWritable()) {
            this.writePackets(id, channel);
            // All packets are now sent, unwatch this socket for writing
            // Re-adding the socket is done by the proxylet
            this.removeOutput(id);
        }
    }

    public void addConnection(ConnectionId id, SocketChannel channel) throws ClosedChannelException {
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
        this.connectionIdHashMap.put(key, id);
    }

    private void removeConnection(ConnectionId id) throws IOException {
        SelectionKey key = this.connectionIdHashMap.inverse().get(id);
        this.connectionIdHashMap.remove(key);
        key.cancel();
        key.channel().close();
    }

    public void addOutput(ConnectionId id) {
        SelectionKey key = this.connectionIdHashMap.inverse().get(id);
        int oldOps = key.interestOps();
        key.interestOps(oldOps | SelectionKey.OP_WRITE);
    }

    private void removeOutput(ConnectionId id) {
        SelectionKey key = this.connectionIdHashMap.inverse().get(id);
        int oldOps = key.interestOps();
        key.interestOps(oldOps & ~SelectionKey.OP_WRITE);
    }

    protected void addServer(ConnectionId id, ServerSocketChannel channel) throws ClosedChannelException {
        SelectionKey key = channel.register(this.selector, SelectionKey.OP_ACCEPT);
        this.connectionIdHashMap.put(key, id);
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

    protected int selectNow() throws IOException {
        return this.selector.selectNow();
    }

    protected Set<SelectionKey> getSelectedKeys() {
        return this.selector.selectedKeys();
    }
}
