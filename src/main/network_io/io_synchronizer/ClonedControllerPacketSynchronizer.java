package network_io.io_synchronizer;

import of_packets.OFMsgType;
import utils.ConnectionId;
import utils.PacketBuffer;
import utils.events.SocketDataEventArg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Acts as the IO queue for the replicated controller
 * All packets going to and coming out of the controller must be inserted to this objects.
 * <p>
 * Synchronized packets can be obtained by {@link #getSynced()}
 *
 * Packets are synced with disregard to the sender, to make a concise interface and code possible.
 */
public class ClonedControllerPacketSynchronizer implements Synchronizer {

    private final PacketBuffer replies;
    private final LinkedList<SocketDataEventArg> syncedPacketsToController;

    public ClonedControllerPacketSynchronizer() {
        this.replies = new PacketBuffer();
        this.syncedPacketsToController = new LinkedList<>();
    }

    public void addUnSynchronized(SocketDataEventArg dataEventArg) {
        final byte messageCode = dataEventArg.getPacket().getMessageCode();
        final boolean isReply = OFMsgType.isReply(messageCode);
        final boolean isQuery = OFMsgType.isQuery(messageCode);

        if (!isQuery && !isReply) {
            return;
        }

        // All queries are always forwarded, eventually the reply entring will remove the copy
        // in the fragmented items
        if (isQuery) {
            this.syncedPacketsToController.add(dataEventArg);
        }

        if (this.addToOutputIfApplicable(dataEventArg)) {
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

    private boolean addToOutputIfApplicable(SocketDataEventArg arg) {
        Optional<SocketDataEventArg> match = this.removeMatchingPacket(arg);
        if (match.isPresent()) {
            this.syncedPacketsToController.add(match.get());
            return true;
        }

        return false;
    }

    private Optional<SocketDataEventArg> removeMatchingPacket(SocketDataEventArg dataEventArg) {
        final ConnectionId id = dataEventArg.getId();
        final Iterator<SocketDataEventArg> iterator = this.replies.packetIterator(id);

        while (iterator.hasNext()) {
            SocketDataEventArg iteratorEventArg = iterator.next();

            if (dataEventArg.isCounterpartOf(iteratorEventArg)) {
                iterator.remove();  // Remove from fragmented buffer

                // If the counterpart is a query, return the reply packet
                // (the query has already been added to output)
                // So, Whenever we compare between a query and a reply, the reply gets returned
                if (OFMsgType.isQuery(iteratorEventArg.getPacket().getMessageCode())) {
                    return Optional.of(dataEventArg);
                } else {
                    return Optional.of(iteratorEventArg);
                }
            }
        }

        return Optional.empty();
    }
}
