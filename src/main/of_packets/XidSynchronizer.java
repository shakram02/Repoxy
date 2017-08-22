package of_packets;

import com.google.common.collect.ImmutableList;
import of_packets.OFMsgType;
import of_packets.OFPacket;
import of_packets.OFPacketHeader;
import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.ConnectionId;
import utils.packet_store.PacketStore;

import java.util.Optional;

/**
 * Controllers might send different OFXid numbers. This class fixes this issue
 * <p>
 * FIXME
 * I assume that messages are sent back from switches in order. Hence, I use
 * a queue to store Xids. If this is not true, then a REQUEST - REPLY table should
 * be made and OFXid are fetched/stored according to message type.
 */
public class XidSynchronizer {
    PacketStore packetStore;

    public XidSynchronizer(PacketStore packetStore) {
        this.packetStore = packetStore;
    }

   /**
     * Adjust OFXid of request / reply packets
     *
     * @param packet OpenFlow packet
     */
    @NotNull
    public Optional<OFPacket> syncPacketXid(ConnectionId id, OFPacket packet) {
        OFPacketHeader header = packet.getHeader();
        Byte msgCode = header.getMessageCode();

        if (OFMsgType.isReply(msgCode)) {
            return Optional.of(modifyReply(id, packet));
        }

        if (OFMsgType.isQuery(msgCode)) {
            storeQuery(id, packet);
        }

        return Optional.empty();
    }

    private OFPacket modifyReply(ConnectionId id, OFPacket reply) {
        Byte messageCode = reply.getMessageCode();

        if (!hasCounterpart(id, messageCode)) {
            throw new IllegalStateException("No query is present for this reply");
        }

        OFPacket query = retrievePacket(id, OFMsgType.getOppositeMessage(messageCode));
        int queryXid = query.getXid();

        return reply.withXid(queryXid);
    }

    private void storeQuery(ConnectionId id, OFPacket query) {
        Byte messageCode = query.getMessageCode();

        // TODO remove this function as soon as Packet Synchronizer is ready
        if (hasCounterpart(id, messageCode)) {
            throw new IllegalStateException("Reply is present before its query");
        }

        storePacket(id, query);
    }

    private boolean hasCounterpart(ConnectionId id, Byte messageCode) {
        return packetStore.exists(id, OFMsgType.getOppositeMessage(messageCode));
    }

    private void storePacket(ConnectionId id, OFPacket packet) {
        this.packetStore.storePacket(id, packet);
    }

    private OFPacket retrievePacket(ConnectionId id, Byte messageCode) {
        return this.packetStore.getPacket(id, messageCode);
    }
}
