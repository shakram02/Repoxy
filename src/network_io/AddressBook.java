package network_io;

import base_classes.ConnectionId;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;

/**
 * Hold triplet values of ConnectionId,SocketAddress,SelectableChannel
 * that are unique to each connected element
 */
public class AddressBook {
    private BiMap<ConnectionId, SocketAddress> socketAddressBiMap;
    private BiMap<ConnectionId, SelectableChannel> channelBiMap;

    public AddressBook() {
        this.socketAddressBiMap = HashBiMap.create();
        this.channelBiMap = HashBiMap.create();
    }

    @NotNull
    public ConnectionId getId(SocketAddress addr) {
        return this.socketAddressBiMap.inverse().get(addr);
    }

    @NotNull
    public ConnectionId getId(SelectableChannel channel) {
        return this.channelBiMap.inverse().get(channel);
    }

    @NotNull
    public SocketAddress getAddress(ConnectionId id) {
        return this.socketAddressBiMap.get(id);
    }

    @NotNull
    public SelectableChannel getSocket(ConnectionId id) {
        return this.channelBiMap.get(id);
    }

    public synchronized void insert(ConnectionId id, SelectableChannel channel,
                                    SocketAddress address) {
        this.channelBiMap.put(id, channel);
        this.socketAddressBiMap.put(id, address);
    }

    public synchronized void remove(ConnectionId id) {
        this.channelBiMap.remove(id);
        this.socketAddressBiMap.remove(id);
    }
}
