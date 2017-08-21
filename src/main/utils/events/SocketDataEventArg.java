package utils.events;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.SenderType;

import java.io.DataOutputStream;

public class SocketDataEventArg extends ConnectionIdEventArg {

    private ImmutableList<OFPacket> packets;

    public SocketDataEventArg(@NotNull SenderType senderType, @NotNull ConnectionId id,
                              @NotNull ByteArrayDataOutput packet) {
        super(senderType, EventType.SendData, id);
        this.packets = OFStreamParser.parseStream(packet.toByteArray()).getPackets();
    }

    private SocketDataEventArg(@NotNull SenderType senderType, @NotNull ConnectionId id,
                               @NotNull ImmutableList<OFPacket> packets) {
        super(senderType, EventType.SendData, id);
        this.packets = packets;
    }

    public ImmutableList<OFPacket> getPackets() {
        return packets;
    }

    public byte[] toByteArray() {
        byte[] accumulator = {};

        for (OFPacket p : packets) {
            accumulator = Bytes.concat(accumulator, OFStreamParser.serializePacket(p).array());
        }

        return accumulator;
    }

    @Override
    public SocketEventArguments createRedirectedCopy(SenderType newSender) {
        SocketDataEventArg redirected = (SocketDataEventArg) super.createRedirectedCopy(newSender);
        redirected.packets = this.packets;
        return redirected;
    }

    @Override
    public String toString() {
        // Get the header of the first packet
        StringBuilder desc = new StringBuilder();

        for (OFPacket p : this.packets) {
            desc.append(p.getHeader().getXId());
            desc.append(" - ");
            desc.append(p.getPacketType());
            desc.append(" ");
        }
        return super.toString() + "Packets: [ " + desc.toString() + "]";
    }

    public static SocketDataEventArg createWithPackets(SocketDataEventArg old,
                                                       ImmutableList<OFPacket> packets) {
        return new SocketDataEventArg(old.senderType, old.getId(), packets);
    }

}
