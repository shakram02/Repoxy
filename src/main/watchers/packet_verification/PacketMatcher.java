package watchers.packet_verification;

import of_packets.OFPacket;
import utils.events.SocketDataEventArg;

import java.util.Arrays;

class PacketMatcher {
    public boolean match(final SocketDataEventArg first, SocketDataEventArg second) {
        OFPacket firstPacket = first.getPacket();
        OFPacket secondPacket = second.getPacket();

        return firstPacket.getMessageCode() == secondPacket.getMessageCode() &&
                Arrays.equals(firstPacket.getData(), secondPacket.getData());
    }
}
