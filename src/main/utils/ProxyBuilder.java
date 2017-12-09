package utils;

import middleware.MiddlewareManager;
import middleware.ProxyMiddleware;
import utils.events.ImmutableSocketAddressInfoEventArg;
import mediators.ProxyMediator;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ControllerIOHandler;

import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.util.ArrayList;

public class ProxyBuilder {
    private static ProxyMediator ProxyMediator;
    private static final ArrayList<ControllerIOHandler> CONTROLLERS_REGIONS = new ArrayList<>();
    private static ConnectionAcceptorIOHandler switches;

    private static ProxyBuilder instance;
    private static MiddlewareManager middlewareManager;

    public static ProxyBuilder createInstance() throws RuntimeErrorException {
        if (ProxyBuilder.instance != null) {
            throw new RuntimeErrorException(new Error("Instance is already created"));
        }
        ProxyBuilder.switches = new ConnectionAcceptorIOHandler();
        ProxyBuilder.middlewareManager = new MiddlewareManager();
        ProxyBuilder.ProxyMediator = new ProxyMediator(middlewareManager, switches);
        ProxyBuilder.instance = new ProxyBuilder();
        return instance;
    }

    public ProxyBuilder addController(ControllerConfig config) {
        ControllerIOHandler connectorElement = new ControllerIOHandler(config.getIp(), config.getPort());
        ProxyBuilder.ProxyMediator.registerController(connectorElement);
        CONTROLLERS_REGIONS.add(connectorElement);

        return instance;
    }

    public void startServer(String ip, int port) throws IOException {
        switches.createServer(
                ImmutableSocketAddressInfoEventArg
                        .builder()
                        .ip(ip)
                        .port(port)
                        .build());

    }

    public void stopServer() {
        switches.shutdownServer();
        // Invalidate the builder to create a new one in the next run
        ProxyBuilder.instance = null;
    }

    public void addMiddleware(ProxyMiddleware middleware) {
        middlewareManager.addMiddleware(middleware);
    }

    public ProxyMediator getMediator() {
        return ProxyBuilder.ProxyMediator;
    }
}
