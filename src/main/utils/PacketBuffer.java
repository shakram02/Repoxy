package utils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Buffer for packets
 */
public class PacketBuffer extends QueueMap<ConnectionId, List<Byte>> {


    public void addPacket(ConnectionId id, List<Byte> packet) {
        super.addObject(id, packet);
    }

    public void clearAllData(ConnectionId id) {
        super.clearAll(id);
    }

    public boolean hasPendingPackets(ConnectionId id) {
        return super.hasItems(id);
    }

    @NotNull
    public ByteBuffer getNextPacket(ConnectionId id) {

        List<Byte> nextPacket = super.getNext(id);

        byte[] result = new byte[nextPacket.size()];
        for (int i = 0; i < nextPacket.size(); i++) {
            result[i] = nextPacket.get(i);
        }
        return ByteBuffer.wrap(result);
    }
}
