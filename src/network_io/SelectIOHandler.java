package network_io;

import base_classes.Proxylet;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by ahmed on 7/17/17.
 */
public class SelectIOHandler {
    Selector selector;
    private final Proxylet proxylet;
    public static int BUFFER_SIZE = 2048;

    public SelectIOHandler(Selector selector, Proxylet proxylet) {
        this.selector = selector;
        this.proxylet = proxylet;
    }

    public void handleKey(SocketChannel channel, SelectionKey key) throws IOException {
//        this.proxylet.
//        if (key.isWritable()) {
//
//            channel.write(ByteBuffer.wrap(proxylet.getNextPacket());
//
//        } else if (key.isReadable()) {
//
//            List<Byte> data = readAllBytes(channel);
//            if (data.size() == 0) {
//                proxylet.onDisconnect();
//            }
//            proxylet.onData(channel.getRemoteAddress(), data);
//
//        }
    }

    private List<Byte> readAllBytes(SocketChannel channel) throws IOException {
        Vector<Byte> bytes = new Vector<>(BUFFER_SIZE * 2);
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        int read = channel.read(buffer);
        while (read > 0) {
            while (buffer.hasRemaining()) {
                bytes.add(buffer.get());
            }

            buffer.clear();
            read = channel.read(buffer);
        }
        return Arrays.asList((Byte[]) bytes.toArray());
    }
}
