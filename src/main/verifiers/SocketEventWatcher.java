package verifiers;

import com.google.common.eventbus.Subscribe;
import utils.events.SocketEventArguments;

public interface SocketEventWatcher {
    @Subscribe
    void processEvent(SocketEventArguments arg);
}
