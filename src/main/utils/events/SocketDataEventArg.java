package utils.events;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.immutables.value.Value;

@Value.Immutable
public abstract class SocketDataEventArg implements SocketEventArguments {

    public abstract OFPacket getPacket();

    @Override
    public EventType getReplyType() {
        return EventType.SendData;
    }

    public byte[] toByteArray() {
        return OFStreamParser.serializePacket(getPacket()).array();
    }

    @Override
    public String toString() {
        // Get the header of the first packet
        return super.toString() + "Packets: [ " + getPacket().toString() + "]";
    }
}
