package network_io;

import com.google.common.collect.HashBiMap;
import network_io.interfaces.BasicSocketIOCommands;
import network_io.interfaces.BasicSocketIOWatcher;
import org.jetbrains.annotations.NotNull;
import utils.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.Vector;

public abstract class CommonIOHandler implements BasicSocketIOCommands, Closeable {
    private static final int BUFFER_SIZE = 2048;

    protected BasicSocketIOWatcher upperLayer;
    protected final Selector selector;
    protected final HashBiMap<SelectionKey, ConnectionId> keyMap;

    private final PacketBuffer packetBuffer;
    private final ByteBuffer buffer;

    public CommonIOHandler() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        this.keyMap = HashBiMap.create();
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.packetBuffer = new PacketBuffer();
    }


    public void cycle() throws IOException {
        int count = this.selector.selectNow();
        if (count == 0) {
            return;
        }
        // TODO will be used later when having threads for controller and switches
        // this.selector.select();
        Set<SelectionKey> keys = this.selector.selectedKeys();
        try {
            for (SelectionKey key : keys) {
                keys.remove(key);
                this.handleRWDOps(key);
                this.handleSpecialKey(key);
            }
        } catch (CancelledKeyException ignored) {
        }
    }

    /**
     * Each sub class will override this function to handle their special events
     *
     * @param key Selection key
     * @throws IOException Exception because of network elements
     */
    protected abstract void handleSpecialKey(SelectionKey key) throws IOException;

    /**
     * Called by upper classes Cycle() method
     *
     * @param key: SelectionKey in question
     * @throws IOException When socket I/O operation fails
     */
    protected void handleRWDOps(SelectionKey key) throws IOException {
        if (!(key.channel() instanceof SocketChannel)) {
            return;
        }

        SocketChannel channel = (SocketChannel) key.channel();
        ConnectionId id = keyMap.get(key);

        if (key.isReadable()) {

            int read = channel.read(buffer);
            if (read == -1) {
                SocketEventArg arg = new SocketEventArg(SenderType.Socket, EventType.Disconnection, id);
                this.closeConnection(arg);
                // Notify mediator here as close connection when called by the mediator
                // re-notifies the upper layer
                this.upperLayer.onDisconnect(arg);
                return;
            }

            this.onData(id, channel, read);
        }
        if (key.isWritable()) {
            this.writePackets(id, channel);
            this.removeOutput(id);
        }
    }

    @Override
    public void closeConnection(SocketEventArg arg) {
        SelectionKey key = this.keyMap.inverse().get(arg.getId());
        this.keyMap.remove(key);

        key.cancel();
        try {
            key.channel().close();
            this.packetBuffer.clearAllData(arg.getId());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void onData(ConnectionId id, SocketChannel channel, int read) throws IOException {
        Vector<Byte> data = readRemainingBytes(channel, read);

        this.upperLayer.onData(new SocketEventArg(SenderType.Socket,
                EventType.SendData, id, data));
    }

    public void sendData(SocketEventArg arg) {
        this.packetBuffer.addPacket(arg.getId(), arg.getExtraData());
        SelectionKey key = this.keyMap.inverse().get(arg.getId());
        this.addOutput(key);
    }

    @NotNull
    public String getConnectionInfo(ConnectionId id) {
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


    private void writePackets(ConnectionId id, SocketChannel channel)
            throws IOException {
        while (this.packetBuffer.hasPendingPackets(id)) {
            channel.write(this.packetBuffer.getNextPacket(id));
        }
    }

    private void addOutput(SelectionKey key) {
        int oldOps = key.interestOps();
        key.interestOps(oldOps | SelectionKey.OP_WRITE);
    }

    private void removeOutput(ConnectionId id) {
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

    @Override
    public void close() throws IOException {
        // Un-register for selection event and close connection
        for (SelectionKey key : this.keyMap.keySet()) {
            key.channel().close();
        }
    }

    void setUpperLayer(BasicSocketIOWatcher upperLayer) {
        this.upperLayer = upperLayer;
    }
}
