package utils;

import org.jetbrains.annotations.NotNull;
import utils.events.SocketDataEventArg;

import java.nio.ByteBuffer;

/**
 * Buffer for packets
 */
public class PacketBuffer extends QueueMap<ConnectionId, SocketDataEventArg> {


    public void addPacket(ConnectionId id, SocketDataEventArg packet) {
        super.addObject(id, packet);
    }

    public void clearAllData(ConnectionId id) {
        super.clearAll(id);
    }

    public boolean hasPendingPackets(ConnectionId id) {
        return super.hasItems(id);
    }

    @NotNull
    public SocketDataEventArg getNextPacket(ConnectionId id) {
        return super.getNext(id);
    }
}
