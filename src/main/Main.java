import mediators.BaseMediator;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ConnectionCreatorIOHandler;
import regions.ControllersRegion;
import regions.SwitchesRegion;

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

    public static void main(String[] args) throws IOException {
        BaseMediator mediator = new BaseMediator();

        ConnectionAcceptorIOHandler acceptorIOHandler = new ConnectionAcceptorIOHandler();
        SwitchesRegion server = new SwitchesRegion(acceptorIOHandler);
        acceptorIOHandler.setConnectionAcceptor(server);
        server.setMediator(mediator);

        ConnectionCreatorIOHandler creatorIOHandler = new ConnectionCreatorIOHandler();
        ControllersRegion client = new ControllersRegion(creatorIOHandler, localhost, controller_port);
        creatorIOHandler.setUpperLayer(client);
        client.setMediator(mediator);


        mediator.setSwitchesRegion(server);
        mediator.registerController(client);
        server.startListening(localhost, of_port);


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
