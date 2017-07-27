package network_io;

import org.jetbrains.annotations.NotNull;
import utils.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Creates connections to controller
 */
public class ControllerIOHandler extends CommonIOHandler {
    private static ControllerIOHandler activeControllerHandler;
    @NotNull
    private final String address;
    private final int port;

    public ControllerIOHandler(@NotNull SenderType selfType, @NotNull String address, int port) {
        super(selfType);
        this.address = address;
        this.port = port;

        // If no active controllers are set. make this one the active controller
        if (ControllerIOHandler.activeControllerHandler == null) {
            ControllerIOHandler.activeControllerHandler = this;
            this.selfType = SenderType.ControllerRegion;
        }
    }


    /**
     * Called by upper class's Cycle() method
     *
     * @param key: SelectionKey in question
     * @throws IOException When socket I/O operation fails
     */
    @Override
    protected void handleSpecialKey(@NotNull SelectionKey key) throws IOException {

        if (key.isValid() && key.isConnectable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            this.onConnectable(key, channel);
        }
    }

    private void onConnectable(@NotNull SelectionKey key, @NotNull SocketChannel channel) throws IOException {

        if (channel.isConnectionPending()) {
            try {
                channel.finishConnect();
            } catch (IOException e) {
                this.keyMap.remove(key);
                throw e;
            }
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    public void createConnection(@NotNull ConnectionIdEventArg arg) {
        ConnectionId id = arg.getId();
        try {
            SocketChannel client = SocketChannel.open();

            client.configureBlocking(false);
            client.connect(new InetSocketAddress(this.address, this.port));

            SelectionKey key = client.register(this.selector, SelectionKey.OP_CONNECT);
            this.keyMap.put(key, id);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    /**
     * Handles special events that are not handled in the base class
     * <p>
     * The special event for this class is controller change event
     * and opening new connections
     *
     * @param arg Specialized event (ex. connectTo a controller)
     */
    @Override
    protected void handleSpecialEvent(@NotNull SocketEventArguments arg) {
        if (arg instanceof SocketAddressInfoEventArg) {
            SocketAddressInfoEventArg controllerChangeArg = (SocketAddressInfoEventArg) arg;
            this.setActiveControllerHandler(controllerChangeArg);
        } else {
            ConnectionIdEventArg connectionEventArg = (ConnectionIdEventArg) arg;
            this.createConnection(connectionEventArg);
        }

    }

    private void setActiveControllerHandler(SocketAddressInfoEventArg controllerChangeArg) {
        if (ControllerIOHandler.activeControllerHandler != this &&
                this.address.equals(controllerChangeArg.getIp()) &&
                this.port == controllerChangeArg.getPort()) {
            this.selfType = SenderType.ControllerRegion;
        } else {
            this.selfType = SenderType.ReplicaRegion;
        }
    }
}
