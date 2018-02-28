package network_io;


import org.junit.Assert;
import org.junit.Test;
import utils.ConnectionId;

import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkServerTest {

    @Test
    public void testReceiveClient() throws Exception {
        NetworkServer server = new NetworkServer("localhost", 35000);
        server.start();

        Socket clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress("localhost", 35000));
        byte[] bytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};  // Hello packet
        clientSocket.getOutputStream().write(bytes);

        Thread.sleep(200); // Delay for socket operations to complete
        Assert.assertFalse(server.keyMap.containsKey(ConnectionId.CreateForTesting(1)));
        clientSocket.close();
    }

    @Test
    public void testSendToClient() {

    }
}