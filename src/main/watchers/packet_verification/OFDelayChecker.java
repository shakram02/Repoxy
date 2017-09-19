package watchers.packet_verification;

import com.google.common.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import utils.ExpiringArrayList;
import utils.LimitedSizeQueue;
import utils.SenderType;
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

    private final ExpiringArrayList mainPackets;
    private final ExpiringArrayList secondaryPackets;

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

        this.mainPackets = new ExpiringArrayList(timestampThreshold, this::onMainExpired);
        this.secondaryPackets = new ExpiringArrayList(timestampThreshold, this::onSecondaryExpired);

        this.timeoutChecker = new TimeoutChecker(timestampThreshold);
        this.packetMatcher = new PacketMatcher();
    }

    @Override
    public void dispatchEvent(@NotNull final SocketEventArguments arg) {

        if (!(arg instanceof SocketDataEventArg) || arg.getSenderType() == SenderType.SwitchesRegion) {
            return;
        }

        SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
        // Add the packet for validation cycle
        this.addToAppropriateQueue(dataEventArg);

        if (this.willIgnoreComparison) {
            logger.info("Comparison ignored");
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

        Iterator<SocketDataEventArg> mainPacketIterator = this.mainPackets.iterator();

        while (mainPacketIterator.hasNext()) {
            Iterator<SocketDataEventArg> secondaryPacketIterator = this.secondaryPackets.iterator();
            SocketDataEventArg mainPacket = mainPacketIterator.next();

            boolean matched = false;
            while (secondaryPacketIterator.hasNext()) {
                SocketDataEventArg secondControllerPacket = secondaryPacketIterator.next();

                if (!this.packetMatcher.match(mainPacket, secondControllerPacket)) {
                    continue;
                }

                if (this.timeoutChecker.hasTimedOut(mainPacket, secondControllerPacket)) {
                    this.timedOutPacketCount++;
                }

                secondaryPacketIterator.remove();
                matched = true;
                break;
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
     * @param arg packet
     */
    private void addToAppropriateQueue(SocketDataEventArg arg) {
        // Add each packet to its corresponding window
        if (arg.getSenderType() == SenderType.ControllerRegion) {
            this.mainPackets.add(arg);
        } else {
            this.secondaryPackets.add(arg);
        }
    }

    private void switchController() {
        // Alert!
        // TODO: update check timestamp with timeout class
        // TODO: clear packet queues as appropriate
        // TODO: migrate left-over packets
        this.willIgnoreComparison = true;
        mediatorNotifier.post(ImmutableControllerFailureArgs.builder().build());
    }

    private void onMainExpired(SocketDataEventArg mainPacket) {
        logger.warning("Main packet expired:" + mainPacket);
    }

    private void onSecondaryExpired(SocketDataEventArg secondaryPacket) {
        logger.warning("Secondary packet expired:" + secondaryPacket);
    }
}
