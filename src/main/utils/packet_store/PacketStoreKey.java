package utils.packet_store;

import utils.ConnectionId;

import java.util.Objects;

public final class PacketStoreKey {
    private ConnectionId id;
    private Byte messageCode;

    private PacketStoreKey() {
    }

    private PacketStoreKey(ConnectionId id, Byte messageCode) {
        this.id = id;
        this.messageCode = messageCode;
    }

    public static PacketStoreKey create(ConnectionId id, Byte messageCode) {
        return new PacketStoreKey(id, messageCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.messageCode);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PacketStoreKey)) {
            return false;
        }
        PacketStoreKey other = (PacketStoreKey) o;
        return this.id.equals(other.id) && Objects.equals(this.messageCode, other.messageCode);
    }
}
