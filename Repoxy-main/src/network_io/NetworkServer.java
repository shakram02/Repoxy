package network_io;

import utils.ConnectionId;
import utils.ImmutableConnectionId;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import static java.lang.System.out;
import static network_io.NUtils.toHandler;

public class NetworkServer {

    private final SocketAddress address;
    private AsynchronousServerSocketChannel channel;
    private CompletionHandler<AsynchronousSocketChannel, Void> handler;

    NetworkServer(SocketAddress address) {
        this.address = address;
        handler = toHandler((channel, attach) -> processAccept(channel));
    }

    public void start() throws IOException {
        channel = AsynchronousServerSocketChannel.open();
        channel.bind(address);
        channel.accept(null, toHandler((clientChannel, attach) -> processAccept(clientChannel)));
    }

    private void processAccept(AsynchronousSocketChannel clientChannel) {
        NetworkClient networkClient = new NetworkClient(clientChannel,
                message -> out.println("Server: received: " + message));

        ConnectionId id = ImmutableConnectionId.builder().build();


        networkClient.write("Hello! I'm NIO.2 server!\n");
        channel.accept(null, handler);
    }

    public void stop() throws IOException {
        channel.close();
    }
}