package network_io;


import middleware.blocking.io_synchronizer.SynchronizationFacade;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.SenderType;
import utils.events.SocketAddressInfoEventArg;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;

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
    SynchronizationFacade synchronizer; // TODO Remove sync. from io handler
    @NotNull
    private final String address;
    private final int port;
    private final Logger logger;

    public ControllerIOHandler(@NotNull String address, int port) {
        // Start as replica, then the status will be updated
        super(SenderType.ReplicaRegion);

        this.address = address;
        this.port = port;
        this.logger = Logger.getLogger(String.format("%s/%d", this.address, this.port));
        this.synchronizer = new SynchronizationFacade();

        // If no active controllers are set. make this one the active controller
        if (ControllerIOHandler.activeControllerHandler == null) {
            ControllerIOHandler.activeControllerHandler = this;
            this.selfType = SenderType.ControllerRegion;
        }
    }

    @Override
    protected void addOutput(@NotNull SocketEventArguments arg) {
        if (arg instanceof SocketDataEventArg) {
            SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
            this.synchronizer.addInput(dataEventArg);
        } else {
            super.addOutput(arg);
        }
    }

    @Override
    public void addInput(@NotNull SocketEventArguments arg) {
        if (arg instanceof SocketDataEventArg) {
            SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
            this.synchronizer.addInput(dataEventArg);
        } else {
            super.addInput(arg);
        }
    }

    @Override
    public void cycle() throws IOException {
        super.cycle();
        this.synchronizer.execute();

        // Move the packets from the synchronizer middleware
        // and hand it to the super class to send it
        while (this.synchronizer.hasOutput()) {

            SocketDataEventArg packet = this.synchronizer.getOutput();

            if (packet.getSenderType() == SenderType.SwitchesRegion) {
                super.addInput(packet);
            } else {
                super.addOutput(packet);
            }
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

                ConnectionId id = this.keyMap.get(key);
                int localPort = ((InetSocketAddress) channel.getLocalAddress()).getPort();
                this.logger.info("[" + this.selfType + "] ConnId [" + id + "] -> " + localPort + " On controller");
            } catch (IOException e) {
                this.keyMap.remove(key);
                throw e;
            }
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    public void createConnection(@NotNull SocketEventArguments arg) {
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
     * Handles special utils.events that are not handled in the base class
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
            this.createConnection(arg);
        }
    }

    private void setActiveControllerHandler(SocketAddressInfoEventArg controllerChangeArg) {
        if (this.address.equals(controllerChangeArg.getIp()) &&
                this.port == controllerChangeArg.getPort()) {
            this.logger.info(String.format("[%d] Set to main controller", this.port));
            // Set this instance of ControllerIOHandler as the main controller
            this.selfType = SenderType.ControllerRegion;
        } else {
            this.logger.info(String.format("[%d] Set to secondary controller", this.port));
            // Set this instance of ControllerIOHandler as the secondary controller
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
