package utils.events;

import com.google.common.io.ByteArrayDataOutput;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.SenderType;

public class SocketDataEventArg extends ConnectionIdEventArg {

    private ByteArrayDataOutput extraData;

    public SocketDataEventArg(@NotNull SenderType senderType, @NotNull ConnectionId id,
                              @NotNull ByteArrayDataOutput extraData) {
        super(senderType, EventType.SendData, id);
        this.extraData = extraData;
    }

    public ByteArrayDataOutput getExtraData() {
        return extraData;
    }

    @Override
    public SocketEventArguments createRedirectedCopy(SenderType newSender) {
        SocketDataEventArg redirected = (SocketDataEventArg) super.createRedirectedCopy(newSender);
        redirected.extraData = this.extraData;
        return redirected;
    }

    @Override
    public String toString() {
        // Get the header of the first packet
        return super.toString() + "[" + OFStreamParser.parseStream(this.extraData.toByteArray())
                .getPackets().get(0).getHeader().getMessageType() + "]";
    }
}
