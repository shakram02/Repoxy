package utils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * Buffer for packets
 */
public class PacketBuffer extends QueueMap<ConnectionId, byte[]> {


    public void addPacket(ConnectionId id, byte[] packet) {
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

        byte[] nextPacket = super.getNext(id);
        return ByteBuffer.wrap(nextPacket);
    }
}
