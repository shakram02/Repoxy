package utils;

import mediators.ProxyMediator;
import middleware.blocking.PacketMatcher;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.logging.ColoredConsoleHandler;
import middleware.nonblocking.ClientCounter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class CommonMain {
    public static final int WIND_SIZE = 30;
    public static final int TIMEOUT_MILLIS = 500;
    private static ProxyBuilder builder = ProxyBuilder.createInstance();

    public static void setupLogging() {
        Logger globalLogger = Logger.getLogger("");

        // Remove the default console handler
        for (Handler h : globalLogger.getHandlers()) {
            globalLogger.removeHandler(h);
        }

        // Add custom handler
        globalLogger.addHandler(new ColoredConsoleHandler());
    }

    public static void startProxy(String localIp, int localPort, List<ControllerConfig> configList)
            throws IOException {
        // TODO, why not start the server after adding controllers?
        builder.startServer(localIp, localPort);

        for (ControllerConfig controllerConfig : configList) {
            builder.addController(controllerConfig);
        }

        ProxyMediator mediator = builder.getMediator();

        ClientCounter counter = new ClientCounter();
//        OFDelayChecker packetVerifier = new OFDelayChecker(mediator, TIMEOUT_MILLIS);

        // TODO Dumping is disabled
        //        PacketDumper dumper = new PacketDumper(new Date().toString());
        builder.addMiddleware(new PacketMatcher());
        mediator.registerWatcher(counter);

//        mediator.registerWatcher(packetVerifier);
        //        mediator.registerWatcher(dumper);

        while (!Thread.interrupted()) {
            mediator.cycle();
        }
    }

    public static void stopProxy() throws IOException {
        builder.getMediator().close();
        builder.stopServer();
    }
}
