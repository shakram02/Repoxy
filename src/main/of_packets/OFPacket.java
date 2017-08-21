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

    public String getPacketType() {
        return this.header.getMessageType();
    }

    public OFPacketHeader getHeader() {
        return header;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        // TODO extend this
        return this.header.toString();
    }

    private OFPacket() {
    }

    public static OFPacket createNewWithXid(OFPacket packet, int xid) {
        OFPacket clone = new OFPacket();
        clone.header = OFPacketHeader.createNewWithXid(packet.header, xid);
        clone.data = packet.data.clone();

        return clone;
    }
}
