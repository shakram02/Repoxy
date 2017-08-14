package utils;

import of_packets.OFPacket;

public class StampedPacket {
    public final OFPacket packet;
    public final long timestamp;

    public StampedPacket(OFPacket packet, long timestamp) {
        this.packet = packet;
        this.timestamp = timestamp;
    }
}
