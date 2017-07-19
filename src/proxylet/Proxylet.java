package proxylet;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import utils.AddressBook;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.SocketEventArg;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.List;
import java.util.logging.Logger;

/**
 * proxylet.Proxylet, main base class
 */
public abstract class Proxylet implements Closeable {
    protected Logger logger;
    protected EventBus nextLayerNotifier;

    public Proxylet(Class<?> childClass) {
        this.logger = Logger.getLogger(childClass.getName());
    }

    public abstract void sendTo(ConnectionId id, List<Byte> data);

    protected abstract void onData(SocketEventArg arg);

    protected abstract void onSentTo(SocketEventArg arg);

    protected abstract void onConnection(SocketEventArg arg);

    protected abstract void onDisconnect(SocketEventArg arg);

    protected abstract void cycle() throws IOException;

    public final void dispatchEvent(SocketEventArg arg) {
        switch (arg.type) {
            case DataIn:
                this.onData(arg);
                break;
            case DataOut:
                this.onSentTo(arg);
                break;
            case Connection:
                this.onConnection(arg);
                break;
            case Disconnection:
                this.onDisconnect(arg);
                break;
        }
    }

    @Override
    public abstract void close() throws IOException;

}
