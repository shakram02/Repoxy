package watchers;

import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.LimitedSizeQueue;
import utils.StampedPacket;
import utils.logging.ConsoleColors;

import java.io.Console;
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
        this.mainPackets.add(new StampedPacket(packet, timestamp));
    }

    public synchronized void addToSecondaryWindow(@NotNull OFPacket packet, long timestamp) {
        this.secondaryPackets.add(new StampedPacket(packet, timestamp));
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

            if (!verifyPacket(primaryPacket)) {
                unmatchedCount++;
            } else {
                // Remove the matched packet
                itPrimary.remove();
            }
        }

        return unmatchedCount;
    }

    private boolean verifyPacket(StampedPacket primaryPacket) {
        for (Iterator<StampedPacket> itSecondary = this.secondaryPackets.descendingIterator();
             itSecondary.hasNext(); ) {
            StampedPacket secondaryPacket = itSecondary.next();

            if (this.diffPackets(primaryPacket.packet, secondaryPacket.packet)) {
                // Remove the matched packet
                itSecondary.remove();

                long timeDifference = primaryPacket.timestamp - secondaryPacket.timestamp;
                boolean timedOut = Math.abs(timeDifference) > this.timeoutMills;

                if (secondaryPacket.timestamp <= this.lastValidTime ||
                        primaryPacket.timestamp <= this.lastValidTime) {
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
                // Packet content match. Now check time out and return it
                return timedOut;
            }
        }
        return false;
    }

    public void clearPacketQueues() {
        this.secondaryPackets.clear();
        this.mainPackets.clear();
    }

    private boolean diffPackets(OFPacket first, OFPacket second) {
//        return matchHeaders(first, second) && matchData(first, second);
        return matchHeaders(first, second);
    }

    private boolean matchHeaders(OFPacket first, OFPacket second) {
        // Same packets have same header and content (maybe header only)
        boolean headerMatch = first.getHeader().isEquivalentTo(second.getHeader());
        boolean headerOnly = first.isHeaderOnly() == second.isHeaderOnly();

        return headerMatch && headerOnly;
    }

    private boolean matchData(OFPacket first, OFPacket second) {
        if (first.getData().length != second.getData().length) {
            return false;
        }

        byte[] firstBytes = first.getData();
        byte[] secondBytes = second.getData();

        for (int i = 0; i < firstBytes.length; i++) {
            if (firstBytes[i] != secondBytes[i]) {
                return false;
            }
        }

        return true;
    }

    public void setLastValidTime(long lastValidTime) {
        // TODO: Remove this field,
        // it's just present because now only 2 controllers are used,
        // if one is removed, then nothing is available to compare to.
        this.ignoreComparison = true;
        this.lastValidTime = lastValidTime;
    }
}
