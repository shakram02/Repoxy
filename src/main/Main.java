import mediators.BaseMediator;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ConnectionCreatorIOHandler;
import regions.ControllersRegion;
import regions.SwitchesRegion;
import utils.ProxyBuilder;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
        final BaseMediator mediator = builder.getMediator();


        TimerTask t = new TimerTask() {
            int alt = 0;

            @Override
            public void run() {
                if (!mediator.hasClients()) {
                    return; // Cancel the task if nobody is connected
                }

                if (alt % 2 == 0) {
                    mediator.setActiveController(localhost, replicated_controller_port);
                } else {
                    mediator.setActiveController(localhost, controller_port);
                }
                alt++;
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(t, 2000, 10000);

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
