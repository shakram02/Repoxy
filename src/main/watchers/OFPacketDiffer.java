package watchers;

import of_packets.OFPacket;
import of_packets.OFPacketHeader;
import org.jetbrains.annotations.NotNull;
import utils.LimitedSizeQueue;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OFPacketDiffer {
    private final LimitedSizeQueue<OFPacket> mainPackets;
    private final LimitedSizeQueue<OFPacket> secondaryPackets;
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

    public OFPacketDiffer(int windSize) {
        this.mainPackets = new LimitedSizeQueue<>(windSize);
        this.secondaryPackets = new LimitedSizeQueue<>(windSize);
    }

    public synchronized void addToPrimaryWindow(@NotNull OFPacket packet) {
        this.mainPackets.add(packet);
    }

    public synchronized void addToSecondaryWindow(@NotNull OFPacket packet) {
        this.secondaryPackets.add(packet);
    }

    public synchronized int countUnmatchedPackets() {
        int unmatchedCount = 0;

        // Calculate the number of packets in both windows, remove matched packets and return the number of mismatched
        // packets
        for (Iterator<OFPacket> itPrimary = this.mainPackets.descendingIterator();
             itPrimary.hasNext(); ) {
            OFPacket primaryPacket = itPrimary.next();

            if (!findPacket(primaryPacket)) {
                unmatchedCount++;
            } else {
                // Remove the matched packet
                itPrimary.remove();
            }
        }

        return unmatchedCount;
    }

    private boolean findPacket(OFPacket packet) {
        for (Iterator<OFPacket> itSecondary = this.secondaryPackets.descendingIterator();
             itSecondary.hasNext(); ) {
            OFPacket secondaryPacket = itSecondary.next();

            if (this.diffPackets(packet, secondaryPacket)) {
                // Remove the matched packet
                itSecondary.remove();
                return true;
            }
        }
        return false;
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
