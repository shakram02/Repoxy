package verifiers;

import utils.events.EventType;
import utils.SenderType;
import utils.events.SocketEventArguments;

public class ClientCounter implements SocketEventWatcher {
    private int connectedCount;

    @Override
    public void processEvent(SocketEventArguments arg) {
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
