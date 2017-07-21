package network_io;

import network_io.interfaces.ConnectionAcceptor;
import utils.ConnectionId;
import utils.EventType;
import utils.SenderType;
import utils.SocketEventArg;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionAcceptorIOHandler extends CommonIOHandler {
    private final ConnectionAcceptor connectionAcceptor;

    public ConnectionAcceptorIOHandler(ConnectionAcceptor connectionAcceptor) {
        super(connectionAcceptor);
        this.connectionAcceptor = connectionAcceptor;
    }

    @Override
    protected void handleSpecialKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel ch = (ServerSocketChannel) key.channel();
            this.onConnection(ch);
        }
    }

    private void onConnection(ServerSocketChannel server) throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);

        // This shouldn't be created if the caller is the mediator
        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        this.connectionAcceptor.onConnectionAccepted(new SocketEventArg(SenderType.Socket,
                EventType.Connection, id));
    }

    public void createServer(String address, int port) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        SelectionKey key = server.register(this.selector, SelectionKey.OP_ACCEPT);

        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        server.socket().bind(new InetSocketAddress(address, port));
    }
}
