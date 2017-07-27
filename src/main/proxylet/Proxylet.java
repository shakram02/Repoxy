package proxylet;

import utils.SenderType;
import utils.events.SocketEventObserver;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * proxylet.Proxylet, main base class
 */
public abstract class Proxylet implements Closeable, SocketEventObserver {
    protected Logger logger;
    protected SenderType senderType;

    public Proxylet(SenderType senderType) {
        this.senderType = senderType;
        this.logger = Logger.getLogger(senderType.toString());
    }


    /**
     * Proxylet is about to close, clean up!
     *
     * @throws IOException I/O exception out of network elements
     */
    @Override
    public abstract void close() throws IOException;

}
