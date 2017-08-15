import mediators.ProxyMediator;
import utils.ProxyBuilder;
import utils.logging.ColoredConsoleHandler;
import watchers.ClientCounter;
import watchers.OFPacketVerifier;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

/*
  The Guest OSs connect to

  vboxnet0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
  inet 192.168.56.1  netmask 255.255.255.0  broadcast 192.168.56.255
 */
public class Main {
    public static final String CLONE_SEC_CONT = "192.168.56.105";
    public static final String CLONE_MAIN_CONT = "192.168.56.104";
    public static final String LOCALHOST = "192.168.56.1";

    public static int OF_PORT = 6833;
    public static int CONTROLLER_PORT = 6834;
    public static int REPLICATED_CONTROLLER_PORT = 6835;

    public static final int WIND_SIZE = 20;
    public static final int TIMESTAMP_THRESHOLD_MS = 200;
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        setupLogging();

        System.out.println(String.format("Ports: [%d] [%d] [%d]",
                OF_PORT, CONTROLLER_PORT, REPLICATED_CONTROLLER_PORT));

        ProxyBuilder builder = ProxyBuilder.createInstance()
                .addController(CLONE_MAIN_CONT, CONTROLLER_PORT)
                .addController(CLONE_SEC_CONT, REPLICATED_CONTROLLER_PORT);

        builder.startServer(LOCALHOST, OF_PORT);
        final ProxyMediator mediator = builder.getMediator();
        logger.log(Level.INFO, "Listening to " + OF_PORT);

        ClientCounter counter = new ClientCounter();
        OFPacketVerifier packetVerifier = new OFPacketVerifier(WIND_SIZE, mediator, TIMESTAMP_THRESHOLD_MS);

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

    private static void createAndRunSwitcher(final ProxyMediator mediator, final ClientCounter counter) {
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

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(t, 2000, 10000);
        timer.cancel();
    }
}
