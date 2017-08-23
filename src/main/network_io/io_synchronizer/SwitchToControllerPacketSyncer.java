package network_io.io_synchronizer;

import of_packets.OFMsgType;
import of_packets.OFPacket;
import utils.ConnectionId;
import utils.PacketBuffer;
import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

// FIXME take care of the SetConfig flow: request(C2S) >> reply(S2C) >> setConfig(C2S)

/**
 * Input: Controller messages, Switch messages
 * Assumptions:
 * Main controller is always ahead (so the switches might send replies before queries from replicated controller)
 * <p>
 * Output: Messages going to controller
 * <p>
 * Function: Delay Switch "reply messages" until the controller
 * sends the corresponding request.
 * <p>
 * When a packet arrives:
 * If it doesn't have a reply it's stored in the
 * fragmented packet buffer.
 * If the newly arriving packet has a reply stored
 * in the fragmented buffer, The reply is put in the ordered output.
 */
public class SwitchToControllerPacketSyncer {

    private final PacketBuffer fragmented;
    private final LinkedList<SocketDataEventArg> toController;

    public SwitchToControllerPacketSyncer() {
        this.fragmented = new PacketBuffer();
        this.toController = new LinkedList<>();
    }

    public void releaseMatchingReply(SocketDataEventArg dataEventArg) {
        final byte messageCode = dataEventArg.getPacket().getMessageCode();
        final boolean isQuery = OFMsgType.isQuery(messageCode);

        if (!isControllerSender(dataEventArg)) {
            throw new IllegalArgumentException("Expected a message from controller:" + dataEventArg.toString());
        }

        if (!isQuery) {
            return;
        }

        Optional<SocketDataEventArg> matchingPacket = this.removeMatchingPacket(dataEventArg);
        matchingPacket.ifPresent(this.toController::add);
    }

    public void insertUnSynced(SocketDataEventArg dataEventArg) {
        final byte messageCode = dataEventArg.getPacket().getMessageCode();
        final boolean isReply = OFMsgType.isReply(messageCode);

        if (dataEventArg.getSenderType() != SenderType.SwitchesRegion) {
            throw new IllegalArgumentException("Expected a message from switches:" + dataEventArg.toString());
        }

        // An async packet (not query/reply) or a query from switches. that doesn't need synchronization
        if (!isReply) {
            this.toController.add(dataEventArg);
            return;
        }

        // A reply coming from switches is stored until its query is available
        this.fragmented.addPacket(dataEventArg.getId(), dataEventArg);
    }


    public Optional<SocketDataEventArg> getSynced() {
        this.syncPackets();
        return Optional.ofNullable(this.toController.poll());
    }

    /**
     * Try to sync all packets in buffer
     * <p>
     * This function mainly takes packets from the fragmented queue
     * and syncs it if applicable
     */
    private void syncPackets() {
        for (SocketDataEventArg val : this.fragmented.values()) {
            Optional<SocketDataEventArg> match = this.removeMatchingPacket(val);
            match.ifPresent(this.toController::add);
        }
    }

    private Optional<SocketDataEventArg> removeMatchingPacket(SocketDataEventArg dataEventArg) {
        final ConnectionId id = dataEventArg.getId();
        final Iterator<SocketDataEventArg> iter = this.fragmented.packetIterator(id);
        final OFPacket packet = dataEventArg.getPacket();

        while (iter.hasNext()) {
            SocketDataEventArg iteratorEventArg = iter.next();

            if (!matches(packet, iteratorEventArg.getPacket())) {
                continue;
            }

            iter.remove();  // Remove from fragmented buffer
            return Optional.of(iteratorEventArg);
        }

        return Optional.empty();
    }

    /**
     * Matched OpenFlow request/reply packets
     *
     * @param packet
     * @param other
     * @return
     */
    private boolean matches(OFPacket packet, OFPacket other) {
        return packet.getHeader().equals(other.getHeader());
    }

    private boolean isControllerSender(SocketDataEventArg arg) {
        // FIXME this is just here until conflicts that are possible when switching
        // are resolved. (old message resolution)
        return arg.getSenderType() == SenderType.ControllerRegion
                || arg.getSenderType() == SenderType.ReplicaRegion;
    }
}
