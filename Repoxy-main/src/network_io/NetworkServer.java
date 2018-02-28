package network_io;

import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.ImmutableConnectionId;
import utils.SenderType;
import utils.events.EventType;
import utils.events.ImmutableSocketConnectionIdArgs;
import utils.events.SocketEventArguments;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;

import static network_io.NUtils.toHandler;

public class NetworkServer extends CommonNioHandler implements Closeable {

    private final String address;
    private final int port;
    private AsynchronousServerSocketChannel channel;
    private CompletionHandler<AsynchronousSocketChannel, Void> handler;
    private final Logger logger;

    NetworkServer(@NotNull String address, int port) {
        super(SenderType.SwitchesRegion);
        this.address = address;
        this.port = port;
        this.logger = Logger.getLogger(String.format("%s/%d", this.address, this.port));

        handler = toHandler((channel, attach) -> processAccept(channel));
    }

    public void start() throws IOException {
        channel = AsynchronousServerSocketChannel.open();
        channel.bind(new InetSocketAddress(address, port));
        channel.accept(null, handler);
    }

    private void processAccept(AsynchronousSocketChannel clientChannel) {

        try {
            ConnectionId id = ImmutableConnectionId.builder().build();
            NetworkClient networkClient = new NetworkClient(id, clientChannel, super::onData);

            int localPort = ((InetSocketAddress) channel.getLocalAddress()).getPort();
            this.keyMap.put(networkClient, id);

            this.logger.info("[" + this.selfType + "] ConnId [" + id + "] -> " + localPort + " On controller");

            SocketEventArguments eventArg = ImmutableSocketConnectionIdArgs
                    .builder()
                    .senderType(this.selfType)
                    .replyType(EventType.Connection)
                    .id(id).build();
            this.emitToEventQueue(eventArg);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        channel.accept(null, handler);
    }

    public void stop() throws IOException {
        channel.close();
    }

    @Override
    public void close() throws IOException {
        this.stop();

        for (NetworkClient c : this.keyMap.keySet()) {
            c.close();
        }
    }
}