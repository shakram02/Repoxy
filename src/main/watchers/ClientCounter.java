package watchers;

import org.jetbrains.annotations.NotNull;
import utils.events.EventType;
import utils.SenderType;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

public class ClientCounter implements SocketEventObserver {
    private int connectedCount;

    @Override
    public void dispatchEvent(@NotNull SocketEventArguments arg) {
        SenderType senderType = arg.getSenderType();
        EventType eventType = arg.getReplyType();

        if (senderType == SenderType.SwitchesRegion) {
            if (eventType == EventType.Connection) {
                this.connectedCount++;
            } else if (eventType == EventType.Disconnection) {
                this.connectedCount--;
            }
        }
    }

    public boolean hasClients() {
        return this.connectedCount > 0;
    }
}
