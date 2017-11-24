package utils.events;

import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

/**
 * An interface representing a non-blocking middleware
 */
public interface SocketEventObserver {
    @Subscribe
    void dispatchEvent(@NotNull SocketEventArguments eventArgs);
}
