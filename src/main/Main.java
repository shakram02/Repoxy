import regions.ControllersRegion;
import regions.SwitchesRegion;
import utils.*;

import java.io.IOException;

/**
 * Packet flow:
 * **Input**
 * - Sockets
 * - Regions
 * - Mediators
 * - Regions
 * - Sockets
 * **Output**
 */
public class Main {
    public static final String localhost = "127.0.0.1";
    public static final int of_port = 6633;

    public static void main(String[] args) throws IOException {
//        SwitchesRegion server = new SwitchesRegion();
//        server.startListening(localhost, of_port);

        ControllersRegion client = new ControllersRegion();
        client.connect(localhost, of_port);

        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
//                server.close();
                client.close();
                break;
            }
//            server.cycle();
            client.cycle();
        }
    }
}
