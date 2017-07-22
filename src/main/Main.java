import mediators.BaseMediator;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ConnectionCreatorIOHandler;
import regions.ControllersRegion;
import regions.SwitchesRegion;
import utils.ProxyBuilder;

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
    public static final int controller_port = 6634;
    public static final int replicated_controller_port = 6635;

    public static void main(String[] args) throws IOException {
        ProxyBuilder builder = ProxyBuilder.createInstance()
                .BuildController(localhost, controller_port)
                .BuildController(localhost, replicated_controller_port)
                .BuildSwitchesRegion();

        builder.startServer(localhost, of_port);
        BaseMediator mediator = builder.getMediator();

        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                mediator.close();
                break;
            }
            mediator.cycle();
        }
    }
}
