package network_io;

import com.google.common.collect.ImmutableList;
import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.*;
import utils.events.*;
import utils.packet_store.PacketStore;
import utils.xid_sync.XidSynchronizer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Creates connections to controller
 */
public class ControllerIOHandler extends CommonIOHandler {
    private static ControllerIOHandler activeControllerHandler;

    @NotNull
    private final String address;
    private final int port;
    private final Logger logger;
    private final XidSynchronizer xidSynchronizer;

    public ControllerIOHandler(@NotNull String address, int port) {
        // Start as replica, then the status will be updated
        super(SenderType.ReplicaRegion);

        this.address = address;
        this.port = port;
        this.logger = Logger.getLogger(String.format("%s/%d", this.address, this.port));

        // If no active controllers are set. make this one the active controller
        if (ControllerIOHandler.activeControllerHandler == null) {
            ControllerIOHandler.activeControllerHandler = this;
            this.selfType = SenderType.ControllerRegion;
        }
        this.logger.info(String.format("Connection created: %s , %s", this.selfType, port));
        xidSynchronizer = new XidSynchronizer(new PacketStore());
    }

    @Override
    protected void addToOutputQueue(SocketEventArguments arg) {
        if (arg instanceof SocketDataEventArg && this.selfType == SenderType.ReplicaRegion) {
            super.addToCommandQueue(arg);
        } else {
            super.addToOutputQueue(arg);
        }
    }

    private SocketDataEventArg syncDataEventArgs(SocketDataEventArg arg) {
        // TODO implement this function
//        ImmutableList<OFPacket> modified = xidSynchronizer.syncListXids(arg.getId(),
//                arg.getPackets());
//
//        return SocketDataEventArg.createWithPackets(arg, modified);
        return arg;
    }

    /**
     * @param arg Command for socket IO (CloseConnection/SendData)
     */
    @Override
    public void addToCommandQueue(@NotNull SocketEventArguments arg) {
        if (arg instanceof SocketDataEventArg && selfType == SenderType.ReplicaRegion) {
            super.addToCommandQueue(arg);
        } else {
            super.addToCommandQueue(arg);
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
        if (this.address.equals(controllerChangeArg.getIp()) &&
                this.port == controllerChangeArg.getPort()) {
            this.logger.info(String.format("[%d] Set to main controller", this.port));
            this.selfType = SenderType.ControllerRegion;
        } else {
            this.logger.info(String.format("[%d] Set to secondary controller", this.port));
            this.selfType = SenderType.ReplicaRegion;
        }
    }

    @NotNull
    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
