package network_io;

import base_classes.ConnectionId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.rmi.server.ExportException;

import static org.junit.Assert.*;

/**
 * AddressBook usability tests
 */
public class AddressBookTest {
    private AddressBook addressBook;
    private static final String localhost = "127.0.0.1";
    private SocketChannel sockets[];
    private final int socketCount = 5;

    @Before
    public void setUp() throws Exception {

        this.sockets = new SocketChannel[socketCount];
        addressBook = new AddressBook();
        for (int i = 0; i < socketCount; i++) {
            SocketChannel ch = SocketChannel.open();
            addressBook.insert(new ConnectionId(),
                    ch, new InetSocketAddress(localhost, 40000 + i));
            this.sockets[i] = ch;
        }
    }


    @Test
    public void testAddressBookRetrieval() throws Exception {
        for (int i = 0; i < this.socketCount; i++) {
            ConnectionId id = this.addressBook.getId(new InetSocketAddress(localhost,
                    40000 + i));
            SocketAddress address = this.addressBook.getAddress(id);

            assertNotNull(id);
            assertEquals(new InetSocketAddress(localhost, 40000 + i), address);
        }
    }

    @Test
    public void testGetBySockets() throws Exception {
        for (int i = 0; i < this.socketCount; i++) {
            ConnectionId id = this.addressBook.getId(this.sockets[i]);
            SelectableChannel ch = this.addressBook.getSocket(id);
            assertEquals(this.sockets[i], ch);
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