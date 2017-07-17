package network_io;

import base_classes.ConnectionId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import static org.junit.Assert.*;

/**
 * AddressBook usability tests
 */
public class AddressBookTest {
    private AddressBook addressBook;
    private static final String localhost = "127.0.0.1";

    @Before
    public void setUp() throws Exception {
        addressBook = new AddressBook();
        for (int i = 0; i < 5; i++) {
            SocketChannel ch = SocketChannel.open();
            ch.bind(new InetSocketAddress(localhost, 60000 + i));
            addressBook.insert(new ConnectionId(),
                    ch, new InetSocketAddress(localhost, 40000 + i));
        }
    }


    @Test
    public void testAddressBookRetrieval() throws Exception {
        for (int i = 0; i < 5; i++) {
            ConnectionId id = this.addressBook.getId(new InetSocketAddress(localhost,
                    40000 + i));
            SocketAddress addr = this.addressBook.getAddress(id);
            SocketChannel channel = (SocketChannel) this.addressBook.getSocket(id);

            assertNotNull(id);
            assertEquals(new InetSocketAddress(localhost, 40000 + i), addr);
            assertEquals(new InetSocketAddress(localhost, 60000 + i),
                    channel.getLocalAddress());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void remove() throws Exception {
        InetSocketAddress address = new InetSocketAddress(localhost, 40000);
        ConnectionId id = this.addressBook.getId(address);
        this.addressBook.remove(id);
        this.addressBook.getAddress(id);
    }

}