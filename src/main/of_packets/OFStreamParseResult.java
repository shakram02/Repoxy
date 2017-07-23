package of_packets;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OFStreamParseResult {
    List<OFPacket> packets;
    byte[] remaining;

    public OFStreamParseResult(@NotNull List<OFPacket> packets, @NotNull byte[] remaining) {
        this.packets = packets;
        this.remaining = remaining;
    }

    /**
     * Result of parsing byte stream containing no OF packets
     *
     * @param streamBytes bytes in the stream
     */
    public OFStreamParseResult(@NotNull byte[] streamBytes) {
        this.packets = new ArrayList<>();
        this.remaining = streamBytes;
    }

    public boolean hasPackets() {
        return !this.packets.isEmpty();
    }

    public List<OFPacket> getPackets() {
        return packets;
    }

    public boolean hasRemaining() {
        return this.remaining.length > 0;
    }

    public byte[] getRemaining() {
        return remaining;
    }

}
