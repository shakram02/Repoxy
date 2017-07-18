package base_classes;

import com.google.common.eventbus.Subscribe;
import network_io.AddressBook;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.logging.Logger;

/**
 * Proxylet, main base class
 */
public abstract class Proxylet implements Closeable {
    private AddressBook addressMap;
    protected Logger logger;


    public Proxylet(Class<?> childClass) {
        this.logger = Logger.getLogger(childClass.getName());
        addressMap = new AddressBook();
    }

    public abstract void sendTo(ConnectionId id, List<Byte> data);

    protected abstract void onData(ConnectionId id, List<Byte> data);

    protected abstract void onSentTo(ConnectionId id);

    protected abstract void onConnection(ConnectionId id);

    protected abstract void onDisconnect(ConnectionId id);

    protected abstract void cycle() throws IOException;

    @Subscribe
    protected final void dispatchEvent(SocketEventArg arg) {
        switch (arg.type) {
            case DataIn:
                this.onData(arg.id, arg.extraData);
                break;
            case DataOut:
                this.onSentTo(arg.id);
                break;
            case Connection:
                this.onConnection(arg.id);
                break;
            case Disconnection:
                this.onDisconnect(arg.id);
                break;
        }
    }

    @Override
    public abstract void close() throws IOException;

    @NotNull
    public final ConnectionId getConnectionId(SocketChannel channel) {
        return this.addressMap.getId(channel);
    }

    @NotNull
    public final ConnectionId getConnectionId(SocketAddress address) {
        return this.addressMap.getId(address);
    }

}
