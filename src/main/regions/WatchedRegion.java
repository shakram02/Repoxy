package regions;

import utils.ConnectionId;
import proxylet.Proxylet;
import utils.PacketBuffer;
import network_io.SelectIOHandler;
import utils.SocketEventArg;

import java.io.IOException;
import java.util.List;

/**
 * Common implementation for I/O events
 */
public abstract class WatchedRegion extends Proxylet {
    protected PacketBuffer packetBuffer;
    protected SelectIOHandler ioHandler;

    /**
     * This layer is between the sockets and mediator
     *
     * @param childClass type of the overriding class, for logging
     */
    public WatchedRegion(Class<?> childClass) {
        super(childClass);

        this.packetBuffer = new PacketBuffer();

        try {
            this.ioHandler = new SelectIOHandler(this, this.packetBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Data arrived to region from sockets
     *
     * @param arg socket event data
     */
    @Override
    protected void onData(SocketEventArg arg) {
        // TODO notify mediator
        System.out.println(String.format("Got %d bytes!!", arg.getExtraData().size()));

    }

    /**
     * Someone wants the region to send a message to socket layer
     *
     * @param id   target connection
     * @param data what to send
     */
    @Override
    public void sendTo(ConnectionId id, List<Byte> data) {
        this.packetBuffer.addPacket(id, data);
        this.ioHandler.addOutput(id);
    }

    // Rename to onSendFinish

    @Override
    protected void onSentTo(SocketEventArg arg) {
        this.ioHandler.removeOutput(arg.getId());
    }

    @Override
    protected void onDisconnect(SocketEventArg arg) {
        this.packetBuffer.clearAllData(arg.getId());
    }

    @Override
    public void cycle() throws IOException {
        this.ioHandler.cycle();
    }

    @Override
    public void close() throws IOException {
        this.ioHandler.close();
    }

}
