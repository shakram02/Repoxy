import mediators.BaseMediator;
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
    public static final String LOCALHOST = "127.0.0.1";
    public static final int OF_PORT = 6833;
    public static final int CONTROLLER_PORT = 6834;
    public static final int REPLICATED_CONTROLLER_PORT = 6835;

    public static void main(String[] args) throws IOException {
        ProxyBuilder builder = ProxyBuilder.createInstance()
                .BuildController(LOCALHOST, CONTROLLER_PORT)
                .BuildController(LOCALHOST, REPLICATED_CONTROLLER_PORT)
                .BuildSwitchesRegion();

        builder.startServer(LOCALHOST, OF_PORT);
        final BaseMediator mediator = builder.getMediator();


        TimerTask t = new TimerTask() {
            int alt = 0;

            @Override
            public void run() {
                if (!mediator.hasClients()) {
                    return; // Cancel the task if nobody is connected
                }

                if (alt % 2 == 0) {
                    mediator.setActiveController(LOCALHOST, REPLICATED_CONTROLLER_PORT);
                } else {
                    mediator.setActiveController(LOCALHOST, CONTROLLER_PORT);
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
