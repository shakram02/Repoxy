package proxylet;

import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.SocketEventArguments;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * proxylet.Proxylet, main base class
 */
public abstract class Proxylet implements Closeable {
    protected Logger logger;
    protected SenderType senderType;

    public Proxylet(SenderType senderType) {
        this.senderType = senderType;
        this.logger = Logger.getLogger(senderType.toString());
    }


    /**
     * Do one I/O cycle
     */
    protected abstract void cycle();


    @Subscribe
    public abstract void dispatchEvent(@NotNull SocketEventArguments arg);

    /**
     * Proxylet is about to close, clean up!
     *
     * @throws IOException I/O exception out of network elements
     */
    @Override
    public abstract void close() throws IOException;

}
