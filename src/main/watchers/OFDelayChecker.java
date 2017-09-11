package watchers;

import com.google.common.eventbus.EventBus;
import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.ImmutableControllerFailureArgs;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.util.List;
import java.util.logging.Logger;

public class OFDelayChecker implements SocketEventObserver {
    private final Logger logger;
    private final OFPacketDiffer differ;
    private final int windowSize;
    private final EventBus mediatorNotifier;

    public OFDelayChecker(int windSize, SocketEventObserver mediator, int timestampThreshold) {
        this.logger = Logger.getLogger(OFDelayChecker.class.getName());

        windowSize = windSize;
        this.differ = new OFPacketDiffer(windSize, timestampThreshold);

        mediatorNotifier = new EventBus(OFDelayChecker.class.getName());
        mediatorNotifier.register(mediator);
    }

    @Override
    public void dispatchEvent(@NotNull final SocketEventArguments arg) {

        // FIXME packet diffing is unusable if xid isn't synced
        // as the other controller rejects the packets and causes unmatched packet count
        // to go up.

        if (!(arg instanceof SocketDataEventArg) || arg.getSenderType() == SenderType.SwitchesRegion) {
            return;
        }

        SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
        SenderType sender = dataEventArg.getSenderType();
        OFPacket packet = dataEventArg.getPacket();

        int mismatchedPacketCount =
                this.countMismatchedPackets(sender, packet, dataEventArg.getTimestamp());

        this.logger.info("Unmatched:" + mismatchedPacketCount);
        if (mismatchedPacketCount >= (this.windowSize / 2)) {
            // Alert!
            logger.warning("Changing controller!!!");
            differ.setLastValidTime(arg.getTimestamp());
            mediatorNotifier.post(ImmutableControllerFailureArgs.builder().build());
        }
    }

    /**
     * Count the number of delayed packets
     *
     * @param sender    Current packet sender
     * @param packet    packet
     * @param timestamp Timestamp of the event
     * @return Number of mismatched packets
     */
    private int countMismatchedPackets(SenderType sender, OFPacket packet, long timestamp) {

        // Add each packet to its corresponding window
        if (sender == SenderType.ControllerRegion) {
            this.differ.addToPrimaryWindow(packet, timestamp);
        } else if (sender == SenderType.ReplicaRegion) {
            this.differ.addToSecondaryWindow(packet, timestamp);
        }

        return this.differ.countUnmatchedPackets();
    }
}
