package watchers.packet_verification;

import of_packets.OFPacket;
import utils.ConnectionId;
import utils.PacketBuffer;
import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;

class PacketMatcher {
    private final PacketBuffer mainControllerPackets;
    private final PacketBuffer secondaryControllerPackets;
    private final BiConsumer<SocketDataEventArg, SocketDataEventArg> onMatch;

    PacketMatcher(BiConsumer<SocketDataEventArg, SocketDataEventArg> onMatch) {
        this.onMatch = onMatch;
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

    public int countUnmatched() {
        int unmatched = 0;

        for (ConnectionId id : this.mainControllerPackets.keySet()) {
            unmatched += this.matchIdPackets(id);
        }

        return unmatched;
    }

    private int matchIdPackets(ConnectionId id) {
        Iterator<SocketDataEventArg> firstIterator = this.mainControllerPackets.packetIterator(id);
        int unmatched = 0;

        while (firstIterator.hasNext()) {
            SocketDataEventArg mainPacket = firstIterator.next();
            Iterator<SocketDataEventArg> secondIterator = this.secondaryControllerPackets.packetIterator(id);

            while (secondIterator.hasNext()) {
                SocketDataEventArg secondaryPacket = secondIterator.next();

                if (doMatch(mainPacket, secondaryPacket)) {
                    this.onMatch.accept(mainPacket, secondaryPacket);
                    firstIterator.remove();
                    secondIterator.remove();
                } else {
                    unmatched++;    // Increment if packets don't match
                }
            }
        }

        return unmatched;
    }

    private boolean doMatch(SocketDataEventArg first, SocketDataEventArg second) {
        OFPacket firstPacket = first.getPacket();
        OFPacket secondPacket = second.getPacket();

        return firstPacket.getMessageCode() == secondPacket.getMessageCode() &&
                Arrays.equals(firstPacket.getData(), secondPacket.getData());
    }
}
