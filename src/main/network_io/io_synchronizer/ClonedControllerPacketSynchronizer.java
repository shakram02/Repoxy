package network_io.io_synchronizer;

import of_packets.OFMsgType;
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
public class ClonedControllerPacketSynchronizer {

    private final PacketBuffer replies;
    private final LinkedList<SocketDataEventArg> syncedPacketsToController;

    public ClonedControllerPacketSynchronizer() {
        this.replies = new PacketBuffer();
        this.syncedPacketsToController = new LinkedList<>();
    }

    public void insertUnSynced(SocketDataEventArg dataEventArg) {
        final byte messageCode = dataEventArg.getPacket().getMessageCode();
        final boolean isReply = OFMsgType.isReply(messageCode);
        final boolean isQuery = OFMsgType.isQuery(messageCode);
        final boolean isSentBySwitch = dataEventArg.getSenderType() == SenderType.SwitchesRegion;

        // Condition can be simplified, but left for readability
        if (isSentBySwitch && (isQuery || !isReply)) {
            // Switch queries don't need to wait for reply
            // Switch async messages don't need to wait for reply
            this.syncedPacketsToController.add(dataEventArg);
            return;
        }

//        // !isSentBySwitch is always true here, but left for readability
//        //noinspection ConstantConditions
//        if (isQuery && !isSentBySwitch) {
//            // Store queries coming from controller in order to release their replies
//            // once the replies arrive
//            this.queries.addPacket(dataEventArg.getId(), dataEventArg);
//            return;
//        }

        if (this.addToOutputIfMatch(dataEventArg)) {
            // Incremental packet sync
            // The reply was matched with a query and added to the output list
            return;
        }

        // A reply coming from switches is stored until its query is available
        this.replies.addPacket(dataEventArg.getId(), dataEventArg);
    }


    public Optional<SocketDataEventArg> getSynced() {
        return Optional.ofNullable(this.syncedPacketsToController.poll());
    }

    private boolean addToOutputIfMatch(SocketDataEventArg arg) {
        Optional<SocketDataEventArg> match = this.removeMatchingPacket(arg);
        if (match.isPresent()) {
            this.syncedPacketsToController.add(match.get());
            return true;
        }

        return false;
    }

    private Optional<SocketDataEventArg> removeMatchingPacket(SocketDataEventArg queryEventArg) {
        final ConnectionId id = queryEventArg.getId();
        final Iterator<SocketDataEventArg> iterator = this.replies.packetIterator(id);

        while (iterator.hasNext()) {
            SocketDataEventArg iteratorEventArg = iterator.next();

            if (queryEventArg.isCounterpartOf(iteratorEventArg)) {
                iterator.remove();  // Remove from fragmented buffer
                return Optional.of(iteratorEventArg);
            }
        }

        return Optional.empty();
    }

    /**
     * Matched OpenFlow request/reply packets
     *
     * @param dataEventArg
     * @param otherDataArg
     * @return
     */
    private boolean replyRequestMatch(SocketDataEventArg dataEventArg, SocketDataEventArg otherDataArg) {
        byte messageCode = dataEventArg.getPacket().getMessageCode();
        byte otherMessageCode = otherDataArg.getPacket().getMessageCode();

        boolean sameConnectionId = dataEventArg.getId().equals(otherDataArg.getId());
        boolean isOfOpposite = OFMsgType.getOppositeMessage(messageCode) == otherMessageCode;

        return sameConnectionId && isOfOpposite;
    }
}
