package proxylet;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import utils.ConnectionId;
import utils.SocketEventArg;

import java.io.Closeable;
import java.io.IOException;
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

    /**
     * Data arrived to proxylet
     *
     * @param arg socket event data
     */
    protected abstract void onData(SocketEventArg arg);

    /**
     * Data transmission completed successfully
     *
     * @param arg socket event data
     */
    protected abstract void onSentTo(SocketEventArg arg);

    /**
     * A new connection arrived to the proxylet
     *
     * @param arg socket event data
     */
    protected abstract void onConnection(SocketEventArg arg);

    /**
     * A connected item went offline
     *
     * @param arg socket event data
     */
    protected abstract void onDisconnect(SocketEventArg arg);

    /**
     * Do one I/O cycle
     *
     * @throws IOException I/O exception out of network elements
     */
    protected abstract void cycle() throws IOException;

    /**
     * Someone notified the proxylet for an event, dispatch it to the correct
     * method
     * <p>
     * This method also receives event bus notifications when the proxylet
     * is registered
     *
     * @param arg socket event data
     */
    @Subscribe
    public final void dispatchEvent(SocketEventArg arg) {
        switch (arg.getEventType()) {
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

    /**
     * Proxylet is about to close, clean up!
     *
     * @throws IOException I/O exception out of network elements
     */
    @Override
    public abstract void close() throws IOException;

}
