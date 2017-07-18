package base_classes;

import network_io.AddressBook;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Vector;

/**
 * Proxylet, main base class
 */
public class Proxylet implements Closeable {
    private AddressBook addressMap;
    private ElementType type;

    public Proxylet() {
        addressMap = new AddressBook();
    }

    public void onData(ConnectionId connectionId, Vector<Byte> data) {
    }

    public void sendTo(ConnectionId connectionId, Vector<Byte> data) {
    }

    public void onConnection(SocketChannel address) {
    }

    public void onDisconnect(ConnectionId connectionId) {
    }

    public void cycle() throws IOException {
    }

    public ConnectionId getConnectionId(SocketChannel channel) {
        return this.addressMap.getId(channel);
    }

    public ConnectionId getConnectionId(SocketAddress address) {
        return this.addressMap.getId(address);
    }

    @Override
    public void close() throws IOException {

    }
}
