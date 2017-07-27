package network_io;

import org.jetbrains.annotations.NotNull;
import utils.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class ConnectionAcceptorIOHandler extends CommonIOHandler {
    protected final Logger logger;

    public ConnectionAcceptorIOHandler() {
        super(SenderType.SwitchesRegion);
        this.logger = Logger.getLogger(this.selfType.toString());
    }

    @Override
    protected void handleSpecialKey(@NotNull SelectionKey key) throws IOException {
        if (key.isValid() && key.isAcceptable()) {
            ServerSocketChannel ch = (ServerSocketChannel) key.channel();
            this.onConnection(ch);
        }
    }

    @Override
    protected void handleSpecialEvent(@NotNull SocketEventArguments arg) {
        throw new IllegalStateException("Switch acceptor doesn't have special events");
    }

    private void onConnection(@NotNull ServerSocketChannel server) throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);

        // This shouldn't be created if the caller is the mediator
        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        ConnectionIdEventArg eventArg = new ConnectionIdEventArg(this.selfType, EventType.Connection, id);
        this.addToOutputQueue(eventArg);
    }

    /**
     * Creates a server socket and binds it to the given ip address and port
     *
     * @param arg Information containing IP address and port number for the open server
     * @throws IOException Port is already in use
     */
    public void createServer(@NotNull SocketAddressInfoEventArg arg) throws IOException {
        String address = arg.getIp();
        int port = arg.getPort();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        SelectionKey key = server.register(this.selector, SelectionKey.OP_ACCEPT);

        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        server.socket().bind(new InetSocketAddress(address, port));
    }

    @Override
    protected void addToOutputQueue(SocketEventArguments arg) {
        if (arg.getReplyType() != EventType.SendData) {
            this.logger.info(String.format("Output event %s", arg));
        }

        super.addToOutputQueue(arg);
    }
}
