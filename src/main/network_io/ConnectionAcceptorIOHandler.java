package network_io;

import utils.ImmutableConnectionId;
import utils.events.EventType;
import utils.events.ImmutableSocketConnectionIdArgs;
import utils.events.SocketAddressInfoEventArg;
import utils.events.SocketEventArguments;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.SenderType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class ConnectionAcceptorIOHandler extends CommonIOHandler {
    protected final Logger logger;
    private ServerSocketChannel server;

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
        throw new IllegalStateException("Switch acceptor doesn't have special utils.events");
    }

    private void onConnection(@NotNull ServerSocketChannel server) throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);

        // This shouldn't be created if the caller is the mediator
        ConnectionId id = ImmutableConnectionId.builder().build();
        this.keyMap.put(key, id);

        this.logger.info(key.channel().toString());

        SocketEventArguments eventArg = ImmutableSocketConnectionIdArgs
                .builder()
                .senderType(this.selfType)
                .replyType(EventType.Connection)
                .id(id).build();
        this.addOutput(eventArg);

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

        server = ServerSocketChannel.open();
        server.configureBlocking(false);

        // Don't wait for tcp connection to die, reuse the address
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        SelectionKey key = server.register(this.selector, SelectionKey.OP_ACCEPT);

        ConnectionId id = ImmutableConnectionId.builder().build();
        this.keyMap.put(key, id);

        server.socket().bind(new InetSocketAddress(address, port));
    }

    public void shutdownServer() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
