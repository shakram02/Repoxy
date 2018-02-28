package network_io;


import org.junit.*;
import org.junit.runners.MethodSorters;
import utils.ConnectionId;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NetworkServerTest {
    // TODO: consider using the same IDs for each given test case

    private NetworkServer server;

    @Before
    public void createConnection() throws IOException {
        server = new NetworkServer("localhost", 35000);
        server.start();
    }

    @After
    public void teardownServer() throws IOException {
        server.stop();
    }

    @Test
    public void a_testReceiveClient() throws Exception {
        byte[] bytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};  // Hello packet

        Socket clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress("localhost", 35000));
        clientSocket.getOutputStream().write(bytes);

        Thread.sleep(200); // Delay for socket operations to complete
        ConnectionId id = ConnectionId.CreateForTesting(0);
        Assert.assertTrue(server.keyMap.inverse().containsKey(id));
        clientSocket.close();
    }

    @Test
    public void b_testSendToClient() throws IOException, InterruptedException {
        Socket clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress("localhost", 35000));
        ConnectionId id = ConnectionId.CreateForTesting(1);
        Thread.sleep(200); // Wait for socket operation to complete

        NetworkClient client = server.keyMap.inverse().get(id);
        byte[] msg = "SEEEE".getBytes();
        client.writeRaw(msg);

        Thread.sleep(200); // Wait for socket operation to complete
        byte[] rcvBuffer = new byte[msg.length];
        int readCount = clientSocket.getInputStream().read(rcvBuffer);
        Assert.assertEquals(readCount, msg.length);
        Assert.assertArrayEquals(rcvBuffer, msg);
        clientSocket.close();
    }
}