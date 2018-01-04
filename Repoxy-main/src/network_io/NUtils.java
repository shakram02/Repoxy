package network_io;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
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

    public static String read(ByteBuffer buffer) {
        buffer.flip();

        StringBuilder builder = new StringBuilder();

        while (buffer.remaining() > 0) {
            builder.append((char) buffer.get());
        }

        buffer.clear();

        return builder.toString();
    }

    public static String readLine(Socket socket) throws IOException {
        return new Scanner(socket.getInputStream(), "UTF-8").nextLine();
    }

    public static void writeLine(Socket socket, String message) throws IOException {
        final PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.print(message);
        writer.flush();
    }

    public static void writeLine(SocketChannel channel, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        buffer.flip();
        channel.write(buffer);
    }

    public static void write(ByteBuffer buffer, Collection<String> toWrite) {
        buffer.clear();

        for (Iterator<String> iterator = toWrite.iterator(); iterator.hasNext(); ) {

            final String next = iterator.next();

            if (buffer.remaining() < next.length() / 2) {
                break;
            }

            for (int i = 0, length = next.length(); i < length; i++) {
                buffer.put((byte) next.charAt(i));
            }

            iterator.remove();
        }

        buffer.flip();
    }

    public static interface SRunnable {
        void run() throws Exception;
    }

    public static Runnable run(SRunnable r) {
        return () -> {
            try {
                r.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
