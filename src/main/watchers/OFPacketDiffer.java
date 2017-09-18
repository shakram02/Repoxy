package watchers;

import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.ImmutableStampedPacket;
import utils.LimitedSizeQueue;
import utils.StampedPacket;
import utils.logging.ConsoleColors;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

public class OFPacketDiffer {
    private final LimitedSizeQueue<StampedPacket> mainPackets;
    private final LimitedSizeQueue<StampedPacket> secondaryPackets;
    private final int timeoutMills;
    private long lastValidTime;
    private static final Logger logger = Logger.getLogger(OFPacketDiffer.class.getName());
    private boolean ignoreComparison;
    /*
     * TODO
     * ----
     *
     * What if the main controller event weren't dispatched first and the replica controller
     * event went off first and the packets of the replica came first? at this case
     * the main packet queue won't contain those packets though they might arrive in
     * a later time.
     *
     * Will the echo packets be added?
     *
     */

    public OFPacketDiffer(int windSize, int timeoutMills) {
        this.mainPackets = new LimitedSizeQueue<>(windSize);
        this.secondaryPackets = new LimitedSizeQueue<>(windSize);
        this.timeoutMills = timeoutMills;
    }

    public synchronized void addToPrimaryWindow(@NotNull OFPacket packet, long timestamp) {
        this.mainPackets.add(
                ImmutableStampedPacket.builder()
                        .packet(packet)
                        .timestamp(timestamp)
                        .build());
    }

    public synchronized void addToSecondaryWindow(@NotNull OFPacket packet, long timestamp) {
        this.secondaryPackets.add(ImmutableStampedPacket.builder()
                .packet(packet)
                .timestamp(timestamp)
                .build());
    }

    public synchronized int countUnmatchedPackets() {
        int unmatchedCount = 0;

        if (this.ignoreComparison) {
            logger.info("Comparison ignored");
            return 0;
        }

        // Calculate the number of packets in both windows, remove matched packets and return the number of mismatched
        // packets
        for (Iterator<StampedPacket> itPrimary = this.mainPackets.descendingIterator();
             itPrimary.hasNext(); ) {
            StampedPacket primaryPacket = itPrimary.next();

            if (verifyPacket(primaryPacket)) {
                // Remove the matched packet
                logger.info("Remove");
                itPrimary.remove();
            } else {
                unmatchedCount++;
            }
        }

        return unmatchedCount;
    }

    private boolean verifyPacket(StampedPacket primaryPacket) {
        OFPacket primary = primaryPacket.getPacket();
        Iterator<StampedPacket> itSecondary = this.secondaryPackets.descendingIterator();


        while (itSecondary.hasNext()) {
            StampedPacket secondaryPacket = itSecondary.next();
            OFPacket secondary = secondaryPacket.getPacket();

            if (primary.getMessageCode() != secondary.getMessageCode()) {
                continue;
            }

            // Remove the matched packet
            itSecondary.remove();

            long timeDifference = primaryPacket.getTimestamp() - secondaryPacket.getTimestamp();
            boolean timedOut = Math.abs(timeDifference) > this.timeoutMills;

            if (secondaryPacket.getTimestamp() <= this.lastValidTime ||
                    primaryPacket.getTimestamp() <= this.lastValidTime) {
                // Packets that were left out after controller switching due to timeout
                // should be compared without the time out constrain to avoid packet accumulation
                // in the packet queue
                timedOut = false;
            }

            logger.info("Time difference: " + timeDifference + " " +
                    (timedOut ? ConsoleColors.RED_BOLD_BRIGHT + "TIMEOUT" : "") +
                    ConsoleColors.RESET +
                    (timeDifference < 0 ?
                            " Secondary late" :
                            (timeDifference == 0 ?
                                    "" : " Primary late")));
            // Packet content and timeout are OK. Now check time out and return it
            //noinspection RedundantIfStatement
            if (timedOut) {
                return false;   // Invalid packet, timed out
            } else {
                return true;    // Valid packet, didn't time out
            }
        }

        return false;
    }

    public void clearPacketQueues() {
        this.secondaryPackets.clear();
        this.mainPackets.clear();
    }

    public void setLastValidTime(long lastValidTime) {
        // TODO: Remove this field,
        // it's just present because now only 2 controllers are used,
        // if one is removed, then nothing is available to compare to.
        this.ignoreComparison = true;
        this.lastValidTime = lastValidTime;
    }
}