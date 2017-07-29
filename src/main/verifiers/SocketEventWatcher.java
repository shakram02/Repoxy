package verifiers;

import com.google.common.eventbus.Subscribe;
import utils.SocketEventArguments;

public interface SocketEventWatcher {
    @Subscribe
    void processEvent(SocketEventArguments arg);
}
