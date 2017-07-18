package network_io;

import base_classes.ConnectionId;
import base_classes.TripletSet;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;

/**
 * Stores triplets of ConnectionID, SocketAddress, Channel
 */
public class AddressBook extends TripletSet<ConnectionId, SocketAddress, SelectableChannel> {
    @NotNull
    public ConnectionId getId(SocketAddress address) {
        return this.getKeyByFirst(address);
    }

    @NotNull
    public ConnectionId getId(SelectableChannel channel) {
        return this.getKeyBySecond(channel);
    }

    @NotNull
    public SocketAddress getAddress(ConnectionId id) {
        return this.getFirst(id);
    }

    @NotNull
    public SelectableChannel getSocket(ConnectionId id) {
        return this.getSecond(id);
    }

    public synchronized void insert(ConnectionId id, SelectableChannel channel,
                                    SocketAddress address) {
        super.insert(id, address, channel);
    }

    public synchronized void remove(ConnectionId id) {
        super.remove(id);
    }


}
