package of_packets;

import org.jetbrains.annotations.NotNull;

public class OFPacket {
    OFPacketHeader header;
    byte[] data;

    public OFPacket(@NotNull OFPacketHeader header, @NotNull byte[] data) {

        if (header.isInvalid()) {
            throw new IllegalArgumentException("Invalid OF Header");
        }

        this.header = header;
        this.data = data;   // Can be any OF message
    }

    public boolean isHeaderOnly() {
        return this.data.length == 0;
    }

    public String getPakcetType() {
        return this.header.getMessageType();
    }

    public OFPacketHeader getHeader() {
        return header;
    }

    public byte[] getData() {
        return data;
    }

}
