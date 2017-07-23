package network_io;

import network_io.interfaces.ConnectionAcceptor;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.EventType;
import utils.SenderType;
import utils.ConnectionIdEventArg;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionAcceptorIOHandler extends CommonIOHandler {
    private ConnectionAcceptor connectionAcceptor;

    @Override
    protected void handleSpecialKey(@NotNull SelectionKey key) throws IOException {
        if (key.isValid() && key.isAcceptable()) {
            ServerSocketChannel ch = (ServerSocketChannel) key.channel();
            this.onConnection(ch);
        }
    }

    private void onConnection(@NotNull ServerSocketChannel server) throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);

        // This shouldn't be created if the caller is the mediator
        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        this.connectionAcceptor.onConnectionAccepted(new ConnectionIdEventArg(SenderType.Socket,
                EventType.Connection, id));
    }

    public void createServer(@NotNull String address, int port) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        SelectionKey key = server.register(this.selector, SelectionKey.OP_ACCEPT);

        ConnectionId id = ConnectionId.CreateNext();
        this.keyMap.put(key, id);

        server.socket().bind(new InetSocketAddress(address, port));
    }

    public void setConnectionAcceptor(@NotNull ConnectionAcceptor connectionAcceptor) {
        this.connectionAcceptor = connectionAcceptor;
        super.setUpperLayer(connectionAcceptor);
    }
}
