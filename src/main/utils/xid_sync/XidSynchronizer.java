package utils.xid_sync;

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
     * Adjust the message, it might be a request/reply
     * some requests are initiated by both network sides
     * that's why we just call it syncListXids as we don't yet know
     */
    public ImmutableList<OFPacket> syncListXids(@NotNull final ConnectionId id, @NotNull final ImmutableList<OFPacket> packets) {
        final ImmutableList.Builder<OFPacket> listBuilder = ImmutableList.builder();

        for (OFPacket packet : packets) {
            Optional<OFPacket> syncResult = syncPacketXid(id, packet);

            if (syncResult.isPresent()) {
                listBuilder.add(syncResult.get());
            } else {
                listBuilder.add(packet);
            }
        }

        return listBuilder.build();
    }

    /**
     * Adjust OFXid of request / reply packets
     *
     * @param packet OpenFlow packet
     */
    @NotNull
    private Optional<OFPacket> syncPacketXid(ConnectionId id, OFPacket packet) {
        OFPacketHeader header = packet.getHeader();
        Byte msgCode = header.getMessageCode();

        if (OFMsgType.isReply(msgCode)) {
            return Optional.of(modifyReply(id, packet));
        }

        if (OFMsgType.isQuery(msgCode)) {
            storePacket(id, packet);
        }

        return Optional.empty();
    }

    private OFPacket modifyReply(ConnectionId id, OFPacket reply) {
        Byte messageCode = reply.getHeader().getMessageCode();

        if (!hasCounterpart(id, messageCode)) {
            throw new IllegalStateException("No query is present for this reply");
        }

        OFPacket query = retrievePacket(id, OFMsgType.getOppositeMessage(messageCode));
        OFPacketHeader queryHeader = query.getHeader();
        // Replace the xid in reply with the query's xid
        throw new NotImplementedException();

    }

    private Optional<OFPacket> handleQuery(ConnectionId id, OFPacket query) {
        Byte messageCode = query.getHeader().getMessageCode();

        if (hasCounterpart(id, messageCode)) {
            throw new IllegalStateException("Reply is present before its query");
        }

        storePacket(id, query);
        return Optional.empty();
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
