package utils.xid_sync;

import of_packets.OFMsgType;
import of_packets.OFPacket;
import of_packets.OFPacketHeader;
import utils.ConnectionId;
import utils.events.SocketDataEventArg;

import java.util.HashSet;
import java.util.Set;

/**
 * Controllers might send different Xid numbers. This class fixes this issue
 * <p>
 * FIXME
 * I assume that messages are sent back from switches in order. Hence, I use
 * a queue to store Xids. If this is not true, then a REQUEST - REPLY table should
 * be made and Xid are fetched/stored according to message type.
 */
public class XidSynchronizer {
    private RequestBuffer requestBuffer;
    private static final Set<Byte> REQUEST_SET;
    private static final Set<Byte> REPLY_SET;

    public XidSynchronizer() {
        requestBuffer = new RequestBuffer();
    }

    /**
     * Adjust the message, it might be a request/reply
     * some requests are initiated by both network sides
     * that's why we just call it adjust as we don't yet know
     *
     * @param arg Socket argument containing OpenFlow packets
     */
    public void adjust(SocketDataEventArg arg) {
        for (OFPacket packet : arg.getPackets()) {
            this.adjustPacket(arg.getId(), packet);
        }
    }

    /**
     * Adjust Xid of request / reply packets
     *
     * @param packet OpenFlow packet
     */
    private void adjustPacket(ConnectionId id, OFPacket packet) {
        Byte messageCode = packet.getHeader().getMessageCode();

        if (REQUEST_SET.contains(messageCode)) {

            this.requestBuffer.addRequest(id, packet.getHeader());

        } else if (REPLY_SET.contains(messageCode)) {

            Integer adjustedXid = this.requestBuffer.getRequest(id, packet.getHeader());
            packet.getHeader().setXid(adjustedXid);
        }
    }

    // TODO this should be loaded from protocol def. somehow
    static {
        REQUEST_SET = new HashSet<>();

        REQUEST_SET.add(OFMsgType.OFPT_BARRIER_REQUEST);
        REQUEST_SET.add(OFMsgType.OFPT_ECHO_REQUEST);
        REQUEST_SET.add(OFMsgType.OFPT_FEATURES_REQUEST);
        REQUEST_SET.add(OFMsgType.OFPT_GET_CONFIG_REQUEST);
        REQUEST_SET.add(OFMsgType.OFPT_QUEUE_GET_CONFIG_REQUEST);
        REQUEST_SET.add(OFMsgType.OFPT_STATS_REQUEST);


        REPLY_SET = new HashSet<>();

        REPLY_SET.add(OFMsgType.OFPT_BARRIER_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_ECHO_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_FEATURES_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_GET_CONFIG_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_QUEUE_GET_CONFIG_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_STATS_REQUEST);
    }
}
