package utils.packet_store;

import org.junit.jupiter.api.Test;
import utils.ConnectionId;
import utils.packet_store.PacketStoreKey;

import static org.junit.jupiter.api.Assertions.*;

class PacketStoreKeyTest {
    @Test
    void testHashCode() {
        PacketStoreKey key = PacketStoreKey.create(ConnectionId.CreateForTesting(1), (byte) 1);

        assertTrue(key.hashCode() > 0);
    }

    @Test
    void testEquals() {
        PacketStoreKey a = PacketStoreKey.create(ConnectionId.CreateForTesting(1), (byte) 1);
        PacketStoreKey b = PacketStoreKey.create(ConnectionId.CreateForTesting(1), (byte) 1);
        assertTrue(a.equals(b));

        PacketStoreKey c = PacketStoreKey.create(ConnectionId.CreateForTesting(1), (byte) 2);
        PacketStoreKey d = PacketStoreKey.create(ConnectionId.CreateForTesting(2), (byte) 1);

        assertFalse(c.equals(d));
        assertFalse(d.equals(c));

        assertFalse(c.equals(a));
        assertFalse(a.equals(c));
    }

}