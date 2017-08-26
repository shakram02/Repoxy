import mediators.ProxyMediator;
import utils.LocalhostIpSupplier;
import utils.ProxyBuilder;
import utils.logging.ColoredConsoleHandler;
import watchers.ClientCounter;
import watchers.OFDelayChecker;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static String LOCALHOST;
    private static String CONT_4 = "127.0.0.1"; //"192.168.1.104";
    private static String CONT_5 = "127.0.0.1"; //"192.168.1.105";
    public static int OF_PORT = 6833;
    public static int CONTROLLER_PORT = 6834;
    public static int REPLICATED_CONTROLLER_PORT = 6835;

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static final int WIND_SIZE = 30;
    private static final int TIMEOUT_MILLIS = 6000;

    public static void main(String[] args) throws IOException {
        setupLogging();
        boolean oneMachineRun = false;

        //noinspection ConstantConditions
        if (oneMachineRun) {
            LOCALHOST = "127.0.0.1";
            CONT_4 = "127.0.0.1";
            CONT_5 = "127.0.0.1";
        } else {
            LOCALHOST = LocalhostIpSupplier.getLocalHostLANAddress("w").getHostAddress();
            CONT_4 = "192.168.1.104";
            CONT_5 = "192.168.1.105";
        }

        System.out.println(String.format("Local IP: [%s] Ports: [%d] [%d] [%d]",
                LOCALHOST, OF_PORT, CONTROLLER_PORT, REPLICATED_CONTROLLER_PORT));

        ProxyBuilder builder = ProxyBuilder.createInstance()
                .addController(CONT_4, CONTROLLER_PORT)
                .addController(CONT_5, REPLICATED_CONTROLLER_PORT);


        builder.startServer(LOCALHOST, OF_PORT);
        final ProxyMediator mediator = builder.getMediator();
        logger.log(Level.INFO, "Listening to " + OF_PORT);

        ClientCounter counter = new ClientCounter();
        OFDelayChecker packetVerifier = new OFDelayChecker(WIND_SIZE, mediator, TIMEOUT_MILLIS);


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
