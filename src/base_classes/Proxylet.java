package base_classes;

import com.google.common.collect.BiMap;
import network_io.AddressBook;

import java.io.Closeable;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ahmed on 7/16/17.
 */
public abstract class Proxylet implements Closeable {
    private AddressBook addresMap;
    private ElementType type;
    private ConcurrentLinkedQueue<Iterable<Byte>> packets;

    public Proxylet() {
        packets = new ConcurrentLinkedQueue<>();
    }

    public abstract void onData(int connectionId, Iterable<Byte> data);

    public abstract void sendTo(int connectionId, Iterable<Byte> data);

    public abstract void onConnection(SocketChannel address);

    public abstract void onDisconnect(int connectionId);

    public abstract void cycle();

    public abstract byte[] getNextPacket();

    public ConnectionId getConnectionId(SocketChannel channel) {
        return this.addresMap.getId(channel);
    }

    public ConnectionId getConnectionId(SocketAddress address) {
        return this.addresMap.getId(address);
    }
}
