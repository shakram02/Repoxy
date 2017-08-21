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

    public boolean isHeaderOnly() {
        return this.getData().length == 0;
    }

    public Byte getMessageCode() {
        return this.getHeader().getMessageCode();
    }

    @Override
    public String toString() {
        // TODO extend this
        return this.getHeader().toString();
    }
}
