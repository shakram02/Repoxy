package network_io;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;

/**
 * Created by ronn on 22.05.16.
 */
public class NUtils {

    public static <V, A> CompletionHandler<V, A> toHandler(BiConsumer<V, A> lambda) {
        return new CompletionHandler<V, A>() {
            @Override
            public void completed(V result, A attachment) {
                lambda.accept(result, attachment);
            }

            @Override
            public void failed(Throwable exc, A attachment) {

            }
        };
    }

    public static <V, A> CompletionHandler<V, A> toHandler(BiConsumer<V, A> handler, BiConsumer<Throwable, A> errorHandler) {
        return new CompletionHandler<V, A>() {

            @Override
            public void completed(V result, A attachment) {
                handler.accept(result, attachment);
            }

            @Override
            public void failed(Throwable exc, A attachment) {
                errorHandler.accept(exc, attachment);
            }
        };
    }

    public static ByteArrayDataOutput read(ByteBuffer buffer) {
        buffer.flip();

        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        while (buffer.hasRemaining()) {
            dataOutput.write(buffer.get());
        }

        buffer.clear();
        return dataOutput;
    }
}
