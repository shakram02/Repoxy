package watchers;

import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.LimitedSizeQueue;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OFPacketDiffer {
    private final LimitedSizeQueue<OFPacket> mainPackets;
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
    }

    public void addToWindow(@NotNull OFPacket packet) {
        this.mainPackets.add(packet);
    }

    public boolean checkInWindow(@NotNull OFPacket secondaryPacket) {

        for (Iterator<OFPacket> itPrimary = this.mainPackets.descendingIterator();
             itPrimary.hasNext(); ) {

            OFPacket packet = itPrimary.next();

            if (this.diffPackets(packet, secondaryPacket)) {
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
        boolean typeMatch = first.getPakcetType().equals(second.getPakcetType());
        boolean headerOnly = first.isHeaderOnly() == second.isHeaderOnly();

        return typeMatch && headerOnly;
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
