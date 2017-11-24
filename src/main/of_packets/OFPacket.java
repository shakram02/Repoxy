package of_packets;

import org.immutables.value.Value;

@Value.Immutable
public abstract class OFPacket {
    public abstract OFPacketHeader getHeader();

    public int getXid() {
        return this.getHeader().getXid();
    }

    public abstract byte[] getData();

    public abstract OFPacket withHeader(OFPacketHeader header);

    public OFPacket withXid(int xid) {
        OFPacketHeader freshHeader = this.getHeader().withXid(xid);
        return this.withHeader(freshHeader);
    }

    public byte getMessageCode() {
        return this.getHeader().getMessageCode();
    }

    @Override
    public String toString() {
        // TODO extend this
        return this.getHeader().toString();
    }
}
