package network_io.interfaces;

import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public interface SocketIOer extends Closeable {

    void cycle() throws IOException;

    @NotNull
    String getConnectionInfo(@NotNull ConnectionId id);

    /**
     * Put an item in the command queue for processing
     *
     * @param arg Command for socket IO (CloseConnection/SendData)
     */
    void addToCommandQueue(@NotNull SocketEventArguments arg);

    /**
     * Process a socket event "Now" without adding it to the event queue
     *
     * @param arg Urgent socket event data
     */
    void processEvent(@NotNull SocketEventArguments arg) throws IOException;

    /**
     * Retrieves the next event from the IO Event queue
     *
     * @return An optional the contains the IO event, or empty
     */
    @NotNull
    Optional<SocketEventArguments> getFromEventQueue();

    void registerForEvents(SocketEventObserver observer);
}
