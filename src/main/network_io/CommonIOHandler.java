package network_io;

import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import network_io.interfaces.SocketIOer;
import org.jetbrains.annotations.NotNull;
import utils.*;
import utils.events.SocketEventObserver;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * The basic socket interface takes commands and produces events
 * Commands and events are placed in queues to make the interface stable
 * <p>
 * If the subscriber wants to be notified for events, it can register for them
 */
public abstract class CommonIOHandler implements SocketIOer, Closeable {
    private static final int BUFFER_SIZE = 4096;

    protected final Selector selector;
    protected final HashBiMap<SelectionKey, ConnectionId> keyMap;

    // Adding to output queue should be done only through calling addToOutputQueue()
    // as child classes may want to override what happens when adding an event
    private final ConcurrentLinkedQueue<SocketEventArguments> eventQueue;
    private final ConcurrentLinkedQueue<SocketEventArguments> commandQueue;

    protected final EventBus notifier;
    protected SenderType selfType;

    // Packet buffer is kept as the selector notifies me when the
    // socket is writable, mean while the event processor doesn't
    // know that info.
    private final PacketBuffer packetBuffer;
    private final ByteBuffer buffer;

    public CommonIOHandler(SenderType selfType) {
        this.selfType = selfType;

        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        this.keyMap = HashBiMap.create();
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);

        this.commandQueue = new ConcurrentLinkedQueue<>();
        this.eventQueue = new ConcurrentLinkedQueue<>();
        this.notifier = new EventBus();
        this.packetBuffer = new PacketBuffer();
    }


    public void cycle() throws IOException {
        // FIXME use threads to avoid querying the queue a lot
        Optional<ConnectionIdEventArg> arg = this.fetchInputQueueItem();
        arg.ifPresent(this::processEvent);

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

    @Override
    public void processEvent(@NotNull SocketEventArguments arg) {
        assert arg instanceof ConnectionIdEventArg;

        this.dispatchEvent((ConnectionIdEventArg) arg);
    }

    /**
     * Called by the notifier
     *
     * @param args information about the raised event
     */
    @Subscribe
    protected void onEvent(SocketEventArguments args) {
        this.addToCommandQueue(args);
    }

    private Optional<ConnectionIdEventArg> fetchInputQueueItem() {
        if (this.commandQueue.isEmpty()) {
            return Optional.empty();
        }

        SocketEventArguments arg = this.commandQueue.poll();
        ConnectionIdEventArg idArg = (ConnectionIdEventArg) arg;

        // Check if the controller is alive when IO is needed
        if (idArg.getReplyType() == EventType.SendData &&
                !this.isReceiverReachable(idArg)) {
            // A receiver won't be reachable if it closed connection.
            // Upper layers will be notified for such disconnection but the
            // event queue isn't consistent at that instant.
            // Skipping the event makes the queue consistent.
            return Optional.empty();
        }

        return Optional.of(idArg);
    }

    /**
     * Each sub class will override this function to handle their special events
     *
     * @param key Selection key
     * @throws IOException Exception because of network elements
     */
    protected abstract void handleSpecialKey(@NotNull SelectionKey key) throws IOException;

    /**
     * Specialized handling for events in Input queue if needed
     *
     * @param arg Specialized event (ex. connectTo a controller)
     */
    protected abstract void handleSpecialEvent(@NotNull SocketEventArguments arg);

    /**
     * Adds a socket event in the event queue for processing
     *
     * @param arg Command for socket IO (CloseConnection/SendData)
     */
    @Override
    public void addToCommandQueue(@NotNull SocketEventArguments arg) {
        this.commandQueue.add(arg);
    }

    private void closeConnection(@NotNull ConnectionIdEventArg arg) {
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
        ByteArrayDataOutput data = readRemainingBytes(channel, read);

        SocketDataEventArg arg = new SocketDataEventArg(this.selfType, id, data);
        this.addToOutputQueue(arg);
    }

    private void sendData(@NotNull SocketDataEventArg arg) {
        this.packetBuffer.addPacket(arg.getId(), arg.getExtraData().toByteArray());
        SelectionKey key = this.keyMap.inverse().get(arg.getId());
        this.addOutput(key);
    }

    /**
     * Process an item from the input queue
     *
     * @param arg Item to process
     */
    private void dispatchEvent(@NotNull ConnectionIdEventArg arg) {

        EventType type = arg.getReplyType();

        if (type == EventType.Disconnection) {
            this.closeConnection(arg);
        } else if (type == EventType.SendData) {
            this.sendData((SocketDataEventArg) arg);
        } else {
            this.handleSpecialEvent(arg);
        }
    }


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
                ConnectionIdEventArg arg =
                        new ConnectionIdEventArg(this.selfType, EventType.Disconnection, id);

                this.closeConnection(arg);
                // Notify mediator here as close connection when called by the mediator
                // re-notifies the upper layer
                this.addToOutputQueue(arg);
                return;
            }

            this.onData(id, channel, read);
        }
        if (key.isValid() && key.isWritable()) {
            this.writePackets(id, channel);
            this.removeOutput(id);
        }
    }

    /**
     * Reads the remaining amount of bytes after a first successful read
     *
     * @param channel socket channel to be read
     * @return read bytes
     * @throws IOException Socket I/O Error
     */
    @NotNull
    private ByteArrayDataOutput readRemainingBytes(SocketChannel channel, int count)
            throws IOException {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        while (count > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                dataOutput.write(buffer.get());
            }
            buffer.clear();
            count = channel.read(buffer);
        }
        return dataOutput;
    }

    /**
     * Checks if the receiver has a registered key. and that it still
     * has a mapped socket
     *
     * @param arg Event argument containing the target id
     * @return true if the receiver exists in the keyMap and its key is valid
     */
    private boolean isReceiverReachable(@NotNull ConnectionIdEventArg arg) {
        SelectionKey key = this.keyMap.inverse().get(arg.getId());
        return key != null && key.isValid();
    }

    @Override
    public void close() throws IOException {
        // Un-register for selection event and close connection
        for (SelectionKey key : this.keyMap.keySet()) {
            key.channel().close();
        }
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

    public void registerForEvents(@NotNull SocketEventObserver observer) {
        this.notifier.register(observer);
    }

    @NotNull
    @Override
    public Optional<SocketEventArguments> getFromEventQueue() {
        return Optional.ofNullable(this.eventQueue.poll());
    }

    /**
     * Add an item to event queue.
     *
     * @param arg Event data argument to be added
     */
    protected void addToOutputQueue(SocketEventArguments arg) {
        this.eventQueue.add(arg);
        this.notifier.post(arg);
    }
}
