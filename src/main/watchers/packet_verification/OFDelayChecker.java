package watchers.packet_verification;

import com.google.common.eventbus.EventBus;
import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.ImmutableStampedPacket;
import utils.LimitedSizeQueue;
import utils.SenderType;
import utils.StampedPacket;
import utils.events.ImmutableControllerFailureArgs;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.util.Iterator;
import java.util.logging.Logger;

public class OFDelayChecker implements SocketEventObserver {
    private final Logger logger;
    private final EventBus mediatorNotifier;
    private int packetFailThreshold;

    private final LimitedSizeQueue<StampedPacket> mainPackets;
    private final LimitedSizeQueue<StampedPacket> secondaryPackets;

    private TimeoutChecker timeoutChecker;
    private int unmatchedPackets = 0;
    private int timedOutPacketCount = 0;
    private PacketMatcher packetMatcher;

    private boolean willIgnoreComparison = false;

    public OFDelayChecker(int windowSize, SocketEventObserver mediator, int timestampThreshold) {
        this.logger = Logger.getLogger(OFDelayChecker.class.getName());
        this.packetFailThreshold = (windowSize / 2);

        this.mediatorNotifier = new EventBus(OFDelayChecker.class.getName());
        this.mediatorNotifier.register(mediator);

        this.mainPackets = new LimitedSizeQueue<>(windowSize);
        this.secondaryPackets = new LimitedSizeQueue<>(windowSize);

        this.timeoutChecker = new TimeoutChecker(timestampThreshold);
        this.packetMatcher = new PacketMatcher();
    }

    @Override
    public void dispatchEvent(@NotNull final SocketEventArguments arg) {

        if (!(arg instanceof SocketDataEventArg) || arg.getSenderType() == SenderType.SwitchesRegion) {
            return;
        }

        SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
        SenderType sender = dataEventArg.getSenderType();
        OFPacket packet = dataEventArg.getPacket();

        // Add the packet for validation cycle
        this.addToAppropriateQueue(sender, packet, dataEventArg.getTimestamp());

        if (this.willIgnoreComparison) {
            return;
        }

        // Loop on all elements to validate previously unvalidated packets
        this.updateState();

        this.logger.info("Unmatched:" + unmatchedPackets);

        if (unmatchedPackets >= packetFailThreshold) {
            logger.warning("Maximum number of unmatched packets exceeded");
            switchController();
        }

        if (this.timedOutPacketCount > 0) {
            logger.warning("A packet has timed out, switching controller");
            switchController();
        }
    }

    /**
     * Iterate over all queued packets, validate each packet
     */
    private void updateState() {
        this.unmatchedPackets = 0;
        this.timedOutPacketCount = 0;

        Iterator<StampedPacket> mainPacketIterator = this.mainPackets.descendingIterator();
        // TODO: run this code and test it
        while (mainPacketIterator.hasNext()) {
            Iterator<StampedPacket> secondaryPacketIterator = this.secondaryPackets.descendingIterator();
            StampedPacket mainPacket = mainPacketIterator.next();

            boolean matched = false;
            while (secondaryPacketIterator.hasNext()) {
                StampedPacket secondControllerPacket = secondaryPacketIterator.next();

                if (!this.packetMatcher.match(mainPacket, secondControllerPacket)) {
                    continue;
                }

                secondaryPacketIterator.remove();
                matched = true;

                if (this.timeoutChecker.hasTimedOut(mainPacket, secondControllerPacket)) {
                    this.timedOutPacketCount++;
                }
            }

            if (matched) {
                mainPacketIterator.remove();
            } else {
                this.unmatchedPackets++;
            }
        }
    }

    /**
     * Count the number of delayed packets
     *
     * @param sender    Current packet sender
     * @param packet    packet
     * @param timestamp Timestamp of the event
     */
    private void addToAppropriateQueue(SenderType sender, OFPacket packet, long timestamp) {
        StampedPacket stampedPacket = createStampedPacket(packet, timestamp);

        // Add each packet to its corresponding window
        if (sender == SenderType.ControllerRegion) {
            this.mainPackets.add(stampedPacket);
        } else if (sender == SenderType.ReplicaRegion) {
            this.secondaryPackets.add(stampedPacket);
        } else {
            throw new IllegalStateException("Invalid sender type");
        }
    }

    private void switchController() {
        // Alert!
        //TODO: update check timestamp with timeout class
        //TODO: clear packet queues as appropriate
        this.willIgnoreComparison = true;
        mediatorNotifier.post(ImmutableControllerFailureArgs.builder().build());
    }

    private StampedPacket createStampedPacket(OFPacket packet, long timestamp) {
        return ImmutableStampedPacket.builder()
                .packet(packet)
                .timestamp(timestamp)
                .build();
    }
}
