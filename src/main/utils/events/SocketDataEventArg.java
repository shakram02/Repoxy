package utils.events;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;
import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.immutables.value.Value;

@Value.Immutable
public abstract class SocketDataEventArg implements SocketEventArguments {

    public abstract ImmutableList<OFPacket> getPackets();

    @Override
    public EventType getReplyType() {
        return EventType.SendData;
    }

    public byte[] toByteArray() {
        byte[] accumulator = {};

        for (OFPacket p : this.getPackets()) {
            accumulator = Bytes.concat(accumulator, OFStreamParser.serializePacket(p).array());
        }

        return accumulator;
    }

    @Override
    public String toString() {
        // Get the header of the first packet
        StringBuilder desc = new StringBuilder();

        for (OFPacket p : this.getPackets()) {
            desc.append(p.getHeader());

        }
        return super.toString() + "Packets: [ " + desc.toString() + "]";
    }
}
