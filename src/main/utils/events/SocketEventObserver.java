package tests.utils.events;

import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

public interface SocketEventObserver {
    @Subscribe
    void dispatchEvent(@NotNull SocketEventArguments eventArgs);
}
