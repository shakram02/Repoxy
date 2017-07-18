package network_io;

import base_classes.ConnectionId;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Buffer for packets
 */
public class PacketBuffer {

    private ConcurrentHashMap<ConnectionId, ConcurrentLinkedQueue<List<Byte>>> packets;

    public void addPacket(ConnectionId id, List<Byte> packet) {
        if (!this.packets.containsKey(id)) {
            this.packets.put(id, new ConcurrentLinkedQueue<>());
        }
        this.packets.get(id).add(packet);
    }

    public void clearAllData(ConnectionId id) {
        if (!this.packets.containsKey(id)) {
            return;
        }
        this.packets.remove(id);
    }

    public boolean hasPendingPackets(ConnectionId id) {
        return this.packets.containsKey(id);
    }

    @NotNull
    public ByteBuffer getNextPacket(ConnectionId id) {

        List<Byte> nextPacket = this.packets.get(id).poll();
        if (nextPacket == null) {
            throw new IllegalArgumentException("No pending packets");
        }

        byte[] result = new byte[nextPacket.size()];
        for (int i = 0; i < nextPacket.size(); i++) {
            result[i] = nextPacket.get(i);
        }
        return ByteBuffer.wrap(result);
    }
}
