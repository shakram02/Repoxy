package watchers;

import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.LimitedSizeQueue;
import utils.StampedPacket;

import java.util.Iterator;
import java.util.logging.Logger;

public class OFPacketDiffer {
    private final LimitedSizeQueue<StampedPacket> mainPackets;
    private final LimitedSizeQueue<StampedPacket> secondaryPackets;
    private final int timeoutMills;
    private static final Logger logger = Logger.getLogger(OFPacketDiffer.class.getName());
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

            boolean timedOut = Math.abs(primaryPacket.timestamp - secondaryPacket.timestamp) > this.timeoutMills;

            if (this.diffPackets(primaryPacket.packet, secondaryPacket.packet) && !timedOut) {
                // Remove the matched packet
                itSecondary.remove();
                return true;
            }
        }
        return false;
    }

    public void clearPacketQueues() {
        this.secondaryPackets.clear();
        this.mainPackets.clear();
    }

    private boolean diffPackets(OFPacket first, OFPacket second) {
        return matchHeaders(first, second) && matchData(first, second);
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
}
