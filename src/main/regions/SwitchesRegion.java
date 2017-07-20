package regions;

import proxylet.Proxylet;
import utils.SocketEventArg;

import java.io.IOException;


/**
 * I/O Handler for server socket
 */
public final class SwitchesRegion extends WatchedRegion {

    private Proxylet mediator;

    public SwitchesRegion(Proxylet mediator) {
        super(SwitchesRegion.class);

        // Switches region is coupled to mediator
        this.mediator = mediator;
    }

    public void startListening(String address, int port) throws IOException {
        this.ioHandler.createServer(address, port);
        logger.info("Listening on [" + port + "]");
    }

    @Override
    public void onConnection(SocketEventArg arg) {
        logger.info("Accepted [" + arg.getId().toString() + "]: "
                + this.ioHandler.getRemoteAddress(arg.getId()));
    }

    @Override
    protected void onDisconnect(SocketEventArg arg) {
        super.onDisconnect(arg);
        this.logger.info("[" + this.ioHandler.getRemoteAddress(arg.getId()) + "] Disconnected");
    }

    /**
     * Data arrived to region from sockets
     *
     * @param arg socket event data
     */
    @Override
    protected void onData(SocketEventArg arg) {
        switch (arg.getSenderType()) {
            case Socket:
                this.mediator.dispatchEvent(arg);
                break;
            default:
                throw new IllegalStateException();
        }
        System.out.println(String.format("Got %d bytes!!", arg.getExtraData().size()));
    }

}
