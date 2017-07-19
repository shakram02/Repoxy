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

    @Override
    protected void onData(SocketEventArg arg) {
        System.out.println(String.format("Got %d bytes!!", arg.extraData.size()));
    }

    @Override
    public void sendTo(ConnectionId id, List<Byte> data) {
        this.packetBuffer.addPacket(id, data);
        this.ioHandler.addOutput(id);
    }

    @Override
    protected void onSentTo(SocketEventArg arg) {
        this.ioHandler.removeOutput(arg.id);
    }

    @Override
    protected void onDisconnect(SocketEventArg arg) {
        this.packetBuffer.clearAllData(arg.id);
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
