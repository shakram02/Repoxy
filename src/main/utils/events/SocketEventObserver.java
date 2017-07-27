package utils.events;

import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import utils.SocketEventArguments;

public interface SocketEventObserver {
    @Subscribe
    void dispatchEvent(@NotNull SocketEventArguments eventArgs);
}
