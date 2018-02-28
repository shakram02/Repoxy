package network_io;

import com.google.common.io.ByteArrayDataOutput;
import utils.ConnectionId;
import utils.events.SocketDataEventArg;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static java.nio.ByteBuffer.allocateDirect;
import static network_io.NUtils.toHandler;

/**
 * Created by ronn on 22.05.16.
 */
public class NetworkClient implements Closeable {

    private AtomicBoolean isWriting = new AtomicBoolean();
    private Deque<SocketDataEventArg> toWrite = new ConcurrentLinkedDeque<>();
    private BiConsumer<NetworkClient, ByteArrayDataOutput> readFunction;

    private CompletionHandler<Integer, ByteBuffer> readHandler = toHandler((byteCount, buffer) -> finishRead(byteCount));
    private CompletionHandler<Integer, ByteBuffer> writeHandler = toHandler((byteCount, buffer) -> finishWrite());

    private ByteBuffer rb = allocateDirect(1024);

    private final ConnectionId id;
    private AsynchronousSocketChannel channel;

    NetworkClient(ConnectionId id, AsynchronousSocketChannel channel, BiConsumer<NetworkClient, ByteArrayDataOutput> readFunction) {
        this.id = id;
        this.channel = channel;
        this.readFunction = readFunction;
        readNext();
    }

    private void finishRead(Integer byteCount) {
        if (byteCount.equals(-1)) return;
        readFunction.accept(this, NUtils.read(rb));
        readNext();
    }

    private void finishWrite() {
        if (isWriting.compareAndSet(true, false)) writeNext();
    }

    public void write(SocketDataEventArg message) {
        toWrite.add(message);
        if (isWriting.compareAndSet(false, true)) writeNext();
    }

    public void writeRaw(byte[] bytes) {
        ByteBuffer wb = ByteBuffer.wrap(bytes);
        channel.write(wb, wb, writeHandler);
    }

    private void writeNext() {
        if (toWrite.isEmpty()) return;

        for (Iterator<SocketDataEventArg> iterator = toWrite.iterator(); iterator.hasNext(); ) {
            ByteBuffer wb = iterator.next().toByteBuffer();
            channel.write(wb, wb, writeHandler);

            iterator.remove();
        }
    }

    public ConnectionId getId() {
        return id;
    }

    private void readNext() {
        channel.read(rb, rb, readHandler);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NetworkClient)) return false;

        NetworkClient other = (NetworkClient) o;
        return other.id.equals(this.id);
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }
}
