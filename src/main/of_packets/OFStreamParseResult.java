package of_packets;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

public class OFStreamParseResult {
    ImmutableList<OFPacket> packets;
    byte[] remaining;

    public OFStreamParseResult(@NotNull ImmutableList<OFPacket> packets, @NotNull byte[] remaining) {
        this.packets = packets;
        this.remaining = remaining;
    }

    /**
     * Result of parsing byte stream containing no OF packets
     *
     * @param streamBytes bytes in the stream
     */
    public OFStreamParseResult(@NotNull byte[] streamBytes) {
        this.packets = ImmutableList.of();
        this.remaining = streamBytes;
    }

    public boolean hasPackets() {
        return !this.packets.isEmpty();
    }

    public ImmutableList<OFPacket> getPackets() {
        return packets;
    }

    public boolean hasRemaining() {
        return this.remaining.length > 0;
    }

    public byte[] getRemaining() {
        return remaining;
    }

}
