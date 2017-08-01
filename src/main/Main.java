import mediators.BaseMediator;
import utils.ProxyBuilder;
import utils.logging.ColoredConsoleFormatter;
import utils.logging.ColoredConsoleHandler;
import utils.logging.ConsoleColors;
import watchers.ClientCounter;
import watchers.OFPacketVerifier;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

/**
 * Packet flow:
 * **Input**
 * - Sockets
 * - Regions
 * - Mediators
 * - Regions
 * - Sockets
 * **Output**
 * <p>
 * The behaviour of the program is non-determined with respect to a disconnected
 * replicated controller, no actions will be taken to resolve that.
 */
public class Main {
    public static final String LOCALHOST = "127.0.0.1";
    public static int OF_PORT = 6833;
    public static int CONTROLLER_PORT = 6834;
    public static int REPLICATED_CONTROLLER_PORT = 6835;
    public static final int WIND_SIZE = 100;

    public static void main(String[] args) throws IOException {
        setupLogging();
        Logger.getLogger(Main.class.getName()).info("INDOO");

//        int max = 65000;
//        int min = 45000;
//        int randomPort = ((int) (Math.random() * (max + 1 - min))) + min;
        int randomPort = 6833;
        OF_PORT = randomPort;
        CONTROLLER_PORT = randomPort + 1;
        REPLICATED_CONTROLLER_PORT = randomPort + 2;
        System.out.println(String.format("Ports: [%d] [%d] [%d]",
                OF_PORT, CONTROLLER_PORT, REPLICATED_CONTROLLER_PORT));

        ProxyBuilder builder = ProxyBuilder.createInstance()
                .addController(LOCALHOST, CONTROLLER_PORT)
                .addController(LOCALHOST, REPLICATED_CONTROLLER_PORT);

        builder.startServer(LOCALHOST, OF_PORT);
        final BaseMediator mediator = builder.getMediator();
        System.out.println(String.format("%s Listening to [%d] %s", ConsoleColors.BLUE, OF_PORT, ConsoleColors.RESET));

        ClientCounter counter = new ClientCounter();
        OFPacketVerifier packetVerifier = new OFPacketVerifier(WIND_SIZE);

        mediator.registerWatcher(counter);
        mediator.registerWatcher(packetVerifier);

//        createAndRunSwitcher(mediator,counter);

        while (true) {
            mediator.cycle();
        }

    }

    private static void setupLogging() {
        Logger globalLogger = Logger.getLogger("");

        // Remove the default console handler
        for (Handler h : globalLogger.getHandlers()) {
            globalLogger.removeHandler(h);
        }

        // Add custom handler
        globalLogger.addHandler(new ColoredConsoleHandler());
    }

    private static void createAndRunSwitcher(final BaseMediator mediator, final ClientCounter counter) {
        TimerTask t = new TimerTask() {
            int alt = 0;

            @Override
            public void run() {
                if (!counter.hasClients()) {
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

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(t, 2000, 10000);
//        timer.cancel();


    }
}
