package utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SocketDataEventArg extends ConnectionIdEventArg {

    private List<Byte> extraData;

    public SocketDataEventArg(@NotNull SenderType senderType, @NotNull ConnectionId id,
                              @NotNull List<Byte> extraData) {
        super(senderType, EventType.SendData, id);
        this.extraData = extraData;
    }

    public List<Byte> getExtraData() {
        return extraData;
    }

    @Override
    public SocketEventArguments createRedirectedCopy(SenderType newSender) {
        SocketDataEventArg redirected = (SocketDataEventArg) super.createRedirectedCopy(newSender);
        redirected.extraData = this.extraData;
        return redirected;
    }
}
