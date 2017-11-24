package middleware.blocking.io_synchronizer;

import of_packets.OFMsgType;
import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.PacketBuffer;
import utils.events.ImmutableSocketDataEventArg;
import utils.events.SocketDataEventArg;

import java.util.Iterator;
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
    PacketBuffer packetBuffer;

    public XidSynchronizer() {
        this.packetBuffer = new PacketBuffer();
    }

    /**
     * Store the packet if it's a query and has xid of non 0. The function has no
     * effect it the packet isn't a query
     *
     * @param arg Event argument containing packet
     */
    public void saveCopyIfQuery(SocketDataEventArg arg) {
        if (!OFMsgType.isQuery(arg.getPacket().getMessageCode())) {
            return;
        }
        if (arg.getPacket().getXid() == 0) {
            return;
        }

        this.packetBuffer.addPacket(arg);
    }

    /**
     * Sync Packet's Xid if the provided packet is a reply and has xid of non 0. The function
     * has no effect if the packet wasn't a reply
     *
     * @param arg Event argument containing packet
     */
    @NotNull
    public Optional<SocketDataEventArg> syncIfReply(final SocketDataEventArg arg) {
        if (!OFMsgType.isReply(arg.getPacket().getMessageCode())) {
            return Optional.empty();
        }

        Optional<OFPacket> packet = modifyReply(arg);
        return packet.map(((ImmutableSocketDataEventArg) arg)::withPacket);
    }

    private Optional<OFPacket> modifyReply(final SocketDataEventArg replyArg) {
        OFPacket reply = replyArg.getPacket();

        if (reply.getXid() == 0) {
            return Optional.of(replyArg.getPacket());
        }

        if (!hasCounterpart(replyArg)) {
            // Packet's request might still be pending
            return Optional.empty();
        }

        OFPacket query = getOppositeOf(replyArg);
        int queryXid = query.getXid();

        return Optional.of(reply.withXid(queryXid));
    }

    private boolean hasCounterpart(final SocketDataEventArg arg) {
        ConnectionId id = arg.getId();
        byte oppositeMessageCode = OFMsgType.getOppositeMessage(arg.getPacket().getMessageCode());

        Iterator<SocketDataEventArg> iterator = packetBuffer.packetIterator(id);

        while (iterator.hasNext()) {
            OFPacket ofPacket = iterator.next().getPacket();

            // but with different data content
            if (ofPacket.getMessageCode() == oppositeMessageCode) {
                return true;
            }
        }
        return false;
    }

    private OFPacket getOppositeOf(final SocketDataEventArg arg) {

        Iterator<SocketDataEventArg> iterator = this.packetBuffer.packetIterator(arg.getId());

        while (iterator.hasNext()) {
            SocketDataEventArg dataEventArg = iterator.next();
            OFPacket packet = dataEventArg.getPacket();

            if (dataEventArg.isCounterpartOf(arg)) {
                return packet;
            }
        }

        throw new IllegalArgumentException("Inconsistent packet store / Second controller is faster");
    }
}
