package watchers.packet_verification;

import of_packets.OFPacket;
import utils.StampedPacket;

import java.util.Arrays;
import java.util.Iterator;

class PacketMatcher {
    /**
     * Stops the iterator at the matched packet
     *
     * @param stampedPacket           packet from main controller
     * @param secondaryPacketIterator Iterator for the packets of the secondary controller
     * @return true if a secondary packet matches the main packet
     */
    public boolean findByIterator(final StampedPacket stampedPacket, Iterator<StampedPacket> secondaryPacketIterator) {
        OFPacket mainPacket = stampedPacket.getPacket();

        while (secondaryPacketIterator.hasNext()) {
            OFPacket secondary = secondaryPacketIterator.next().getPacket();

            if (mainPacket.getMessageCode() != secondary.getMessageCode()) {
                continue;
            }

            if (!Arrays.equals(mainPacket.getData(), secondary.getData())) {
                throw new IllegalStateException("Data from two controllers don't match");
            }
            return true;
        }
        return false;
    }
}
