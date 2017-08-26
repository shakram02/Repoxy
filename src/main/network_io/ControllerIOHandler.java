package network_io;

import network_io.io_synchronizer.SynchronizationFacade;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.SenderType;
import utils.events.ImmutableSocketDataEventArg;
import utils.events.SocketAddressInfoEventArg;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Creates connections to controller
 */
public class ControllerIOHandler extends CommonIOHandler {
    private static ControllerIOHandler activeControllerHandler;
    SynchronizationFacade synchronizer;
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
        this.synchronizer = new SynchronizationFacade(super::addOutput, super::addInput);

        // If no active controllers are set. make this one the active controller
        if (ControllerIOHandler.activeControllerHandler == null) {
            ControllerIOHandler.activeControllerHandler = this;
            this.selfType = SenderType.ControllerRegion;
        }
    }

    @Override
    protected void addOutput(SocketEventArguments arg) {
        if (arg instanceof SocketDataEventArg) {
            SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
            this.synchronizer.manageOutput(dataEventArg);
        } else {
            super.addOutput(arg);
        }
    }

    @Override
    public void addInput(@NotNull SocketEventArguments arg) {
        if (arg instanceof SocketDataEventArg) {
            SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
            this.synchronizer.manageInput(dataEventArg);
        } else {
            super.addInput(arg);
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
