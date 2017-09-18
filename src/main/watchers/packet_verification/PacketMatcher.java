package watchers.packet_verification;

import of_packets.OFPacket;
import utils.StampedPacket;

import java.util.Arrays;
import java.util.Iterator;

class PacketMatcher {
    public boolean match(final StampedPacket first, StampedPacket second) {
        OFPacket firstPacket = first.getPacket();
        OFPacket secondPacket = second.getPacket();

        return firstPacket.getMessageCode() == secondPacket.getMessageCode() &&
                Arrays.equals(firstPacket.getData(), secondPacket.getData());
    }
}
