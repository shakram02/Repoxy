import mediators.ProxyMediator;
import utils.ControllerConfig;
import utils.LocalhostIpSupplier;
import watchers.ClientCounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static utils.CommonMain.setupLogging;
import static utils.CommonMain.startProxy;

public class Main {
    private static String LOCALHOST;
    private static String CONT_4 = "127.0.0.1"; //"192.168.1.104";
    private static String CONT_5 = "127.0.0.1"; //"192.168.1.105";
    public static int OF_PORT = 6833;
    public static int CONTROLLER_PORT = 6834;
    public static int REPLICATED_CONTROLLER_PORT = 6835;

    public static void main(String[] args) throws IOException {
        setupLogging();
        boolean oneMachineRun = false;

        //noinspection ConstantConditions
        if (oneMachineRun) {
            LOCALHOST = "127.0.0.1";
            CONT_4 = "127.0.0.1";
            CONT_5 = "127.0.0.1";
        } else {
            // Exclude lo* interfaces (loopback) exclude loopback interfaces when not running on single machine
            LOCALHOST = LocalhostIpSupplier.getLocalHostLANAddress();
            CONT_4 = "192.168.1.104";
            CONT_5 = "192.168.1.105";
        }

        System.out.println(String.format("Local IP: [%s] Ports: [%d] [%d] [%d]",
                LOCALHOST, OF_PORT, CONTROLLER_PORT, REPLICATED_CONTROLLER_PORT));


        ArrayList<ControllerConfig> configs = new ArrayList<>();
        configs.add(new ControllerConfig(CONT_4, CONTROLLER_PORT));
        configs.add(new ControllerConfig(CONT_5, REPLICATED_CONTROLLER_PORT));

        startProxy(LOCALHOST, OF_PORT, configs);
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
