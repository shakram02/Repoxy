package utils.events;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.immutables.value.Value;

import java.nio.ByteBuffer;

@Value.Immutable
public abstract class SocketDataEventArg extends SocketEventArguments {

    public abstract OFPacket getPacket();

    @Override
    public EventType getReplyType() {
        return EventType.SendData;
    }

    public ByteBuffer toByteBuffer() {
        return OFStreamParser.serializePacket(getPacket());
    }

    @Override
    public String toString() {
        // Get the header of the first packet
        return super.toString() + "Packets: [ " + getPacket().toString() + "]";
    }
}
