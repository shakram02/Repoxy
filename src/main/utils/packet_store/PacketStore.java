package utils.packet_store;

import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.QueueMap;

public class PacketStore extends QueueMap<PacketStoreKey, OFPacket> {


    public void storePacket(ConnectionId id, OFPacket packet) {
        PacketStoreKey key = createKey(id, packet.getHeader().getMessageCode());
        this.addObject(key, packet);
    }

    public boolean exists(ConnectionId id, Byte messageCode) {
        PacketStoreKey key = createKey(id, messageCode);
        return super.hasItems(key);
    }

    @NotNull
    public OFPacket getPacket(ConnectionId id, Byte messageCode) {
        PacketStoreKey key = createKey(id, messageCode);
        return super.getNext(key);
    }

    private PacketStoreKey createKey(ConnectionId id, Byte messageCode) {
        return PacketStoreKey.create(id, messageCode);
    }
}
