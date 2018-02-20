package utils;

import mediators.ProxyMediator;
import middleware.blocking.PacketMatcher;
import utils.logging.ColoredConsoleHandler;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class CommonMain {
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

        // TODO Dumping is disabled
//        ClientCounter counter = new ClientCounter();
//        PacketDumper dumper = new PacketDumper(new Date().toString());
//        mediator.registerWatcher(counter);
//        mediator.registerWatcher(packetVerifier);
//        mediator.registerWatcher(dumper);

        builder.addMiddleware(new PacketMatcher());

        while (!Thread.interrupted()) {
            mediator.cycle();
        }
    }

    public static void stopProxy() throws IOException {
        builder.getMediator().close();
        builder.stopServer();
    }
}
