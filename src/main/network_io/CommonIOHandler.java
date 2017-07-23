package network_io;

import com.google.common.collect.HashBiMap;
import network_io.interfaces.BasicSocketIOCommands;
import network_io.interfaces.BasicSocketIOWatcher;
import org.jetbrains.annotations.NotNull;
import utils.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Implementation of common socket IO events
 */
public abstract class CommonIOHandler implements BasicSocketIOCommands, Closeable {
    private static final int BUFFER_SIZE = 4096;

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
        Set<SelectionKey> selectionKeys = this.selector.selectedKeys();

        for (Iterator<SelectionKey> i = selectionKeys.iterator(); i.hasNext(); ) {
            SelectionKey key = i.next();
            i.remove();

            this.handleRWDOps(key);
            this.handleSpecialKey(key);
        }

    }

    /**
     * Each sub class will override this function to handle their special events
     *
     * @param key Selection key
     * @throws IOException Exception because of network elements
     */
    protected abstract void handleSpecialKey(@NotNull SelectionKey key) throws IOException;

    /**
     * Called by upper classes Cycle() method
     *
     * @param key: SelectionKey in question
     * @throws IOException When socket I/O operation fails
     */
    protected void handleRWDOps(@NotNull SelectionKey key) throws IOException {
        if (!key.isValid() || !(key.channel() instanceof SocketChannel)) {
            return;
        }

        SocketChannel channel = (SocketChannel) key.channel();
        ConnectionId id = keyMap.get(key);
        assert id != null : "Entry not found " + key;

        if (key.isValid() && key.isReadable()) {

            int read = channel.read(buffer);
            if (read == -1) {
                ConnectionIdEventArg arg = new ConnectionIdEventArg(SenderType.Socket, EventType.Disconnection, id);
                this.closeConnection(arg);
                // Notify mediator here as close connection when called by the mediator
                // re-notifies the upper layer
                this.upperLayer.onDisconnect(arg);
                return;
            }

            this.onData(id, channel, read);
        }
        if (key.isValid() && key.isWritable()) {
            this.writePackets(id, channel);
            this.removeOutput(id);
        }
    }

    @Override
    public void closeConnection(@NotNull ConnectionIdEventArg arg) {
        SelectionKey key = this.keyMap.inverse().get(arg.getId());
        this.keyMap.remove(key);

        if (key == null) {
            return;
        }

        key.cancel();
        try {
            key.channel().close();
            this.packetBuffer.clearAllData(arg.getId());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void onData(@NotNull ConnectionId id, @NotNull SocketChannel channel, int read) throws IOException {
        Vector<Byte> data = readRemainingBytes(channel, read);

        this.upperLayer.onData(new SocketDataEventArg(SenderType.Socket, id, data));
    }

    public void sendData(@NotNull SocketDataEventArg arg) {
        this.packetBuffer.addPacket(arg.getId(), arg.getExtraData());
        SelectionKey key = this.keyMap.inverse().get(arg.getId());
        this.addOutput(key);
    }

    public boolean isReceiverAlive(@NotNull ConnectionIdEventArg arg) {
        SelectionKey key = this.keyMap.inverse().get(arg.getId());
        return key != null && key.isValid();
    }

    @NotNull
    public String getConnectionInfo(@NotNull ConnectionId id) {
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
        Vector<Byte> bytes = new Vector<>(BUFFER_SIZE);

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

    void setUpperLayer(@NotNull BasicSocketIOWatcher upperLayer) {
        this.upperLayer = upperLayer;
    }
}
