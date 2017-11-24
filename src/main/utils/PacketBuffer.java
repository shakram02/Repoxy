package tests.utils;

import org.jetbrains.annotations.NotNull;
import tests.utils.events.SocketDataEventArg;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Buffer for packets
 */
public class PacketBuffer extends QueueMap<ConnectionId, SocketDataEventArg> {

    public void addPacket(SocketDataEventArg packet) {
        super.addObject(packet.getId(), packet);
    }

    public void clearAllData(ConnectionId id) {
        super.clearAll(id);
    }

    public boolean hasPendingPackets(ConnectionId id) {
        return super.hasItems(id);
    }

    public Iterator<SocketDataEventArg> packetIterator(ConnectionId id) {
        return super.iterator(id);
    }

    @NotNull
    public ConcurrentLinkedQueue<SocketDataEventArg> getPacketQueue(ConnectionId id) {
        return super.getList(id);
    }

    @NotNull
    public SocketDataEventArg getNextPacket(ConnectionId id) {
        return super.getNext(id);
    }
}
