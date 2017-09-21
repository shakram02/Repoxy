package watchers.packet_verification;

import of_packets.OFPacket;
import utils.PacketBuffer;
import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.Arrays;

class PacketMatcher {
    private final PacketBuffer mainControllerPackets;
    private final PacketBuffer secondaryControllerPackets;

    PacketMatcher() {
        mainControllerPackets = new PacketBuffer();
        secondaryControllerPackets = new PacketBuffer();
    }

    public void addPacket(final SocketDataEventArg packet) {
        if (packet.getSenderType() == SenderType.ControllerRegion) {
            this.mainControllerPackets.addPacket(packet);
        } else {
            this.secondaryControllerPackets.addPacket(packet);
        }
    }

    public boolean match(final SocketDataEventArg first, SocketDataEventArg second) {
        OFPacket firstPacket = first.getPacket();
        OFPacket secondPacket = second.getPacket();

        return firstPacket.getMessageCode() == secondPacket.getMessageCode() &&
                Arrays.equals(firstPacket.getData(), secondPacket.getData());
    }
}
