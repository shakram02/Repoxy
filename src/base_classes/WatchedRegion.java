package base_classes;

import network_io.PacketBuffer;
import network_io.SelectIOHandler;

import java.io.IOException;
import java.util.Vector;

/**
 * Watches IO event of a region
 */
public class WatchedRegion extends Proxylet {
    private PacketBuffer packetBuffer;
    private SelectIOHandler ioHandler;

    public WatchedRegion() {
        this.packetBuffer = new PacketBuffer();
        try {
            this.ioHandler = new SelectIOHandler(this, this.packetBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void onData(ConnectionId connectionId, Vector<Byte> data) {
        System.out.println(String.format("Got %d bytes!!", data.size()));
    }

    @Override
    public void sendTo(ConnectionId id, Vector<Byte> data) {
        this.packetBuffer.addPacket(id, data);
        this.ioHandler.addOutput(id);
    }

    @Override
    public void onDisconnect(ConnectionId connectionId) {
        System.out.println("Got disconnect!!");
    }

    @Override
    public void cycle() throws IOException {
        this.ioHandler.cycle();
    }

    public PacketBuffer getPacketBuffer() {
        return this.packetBuffer;
    }
}
