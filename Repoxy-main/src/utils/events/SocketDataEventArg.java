package utils.events;

import openflow.OFMsgType;
import openflow.OFPacket;
import openflow.OFStreamParser;
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

    public boolean isCounterpartOf(SocketDataEventArg other) {
        boolean sameId = this.getId().equals(other.getId());
        byte selfMessageCode = this.getPacket().getMessageCode();
        byte otherMessageCode = other.getPacket().getMessageCode();
        boolean counterPart = OFMsgType.getOppositeMessage(selfMessageCode) == otherMessageCode;
        return counterPart && sameId;
    }
}
