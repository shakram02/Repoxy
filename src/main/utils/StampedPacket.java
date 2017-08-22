package utils;

import of_packets.OFPacket;
import org.immutables.value.Value;

@Value.Immutable
public abstract class StampedPacket {
    public abstract OFPacket getPacket();

    public abstract long getTimestamp();
}
