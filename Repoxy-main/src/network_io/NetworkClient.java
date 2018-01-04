package network_io;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.nio.ByteBuffer.allocateDirect;
import static network_io.NUtils.toHandler;

/**
 * Created by ronn on 22.05.16.
 */
public class NetworkClient {

    private AtomicBoolean isWriting = new AtomicBoolean();
    private Deque<String> toWrite = new ConcurrentLinkedDeque<>();
    private Consumer<String> readFunction;

    private CompletionHandler<Integer, ByteBuffer> readHandler = toHandler((byteCount, buffer) -> finishRead(byteCount));
    private CompletionHandler<Integer, ByteBuffer> writeHandler = toHandler((byteCount, buffer) -> finishWrite());

    private ByteBuffer rb = allocateDirect(1024);
    private ByteBuffer wb = allocateDirect(1024);

    private AsynchronousSocketChannel channel;

    NetworkClient(AsynchronousSocketChannel channel, Consumer<String> readFunction) {
        this.channel = channel;
        this.readFunction = readFunction;
        readNext();
    }

    private void finishRead(Integer byteCount) {
        if (byteCount.equals(-1)) return;
        readFunction.accept(NUtils.read(rb));
        readNext();
    }

    private void finishWrite() {
        if (isWriting.compareAndSet(true, false)) writeNext();
    }

    public void write(String message) {
        toWrite.add(message);
        if (isWriting.compareAndSet(false, true)) writeNext();
    }

    private void writeNext() {
        if (toWrite.isEmpty()) return;
        NUtils.write(wb, toWrite);
        channel.write(wb, wb, writeHandler);
    }

    private void readNext() {
        channel.read(rb, rb, readHandler);
    }
}
