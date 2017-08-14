package utils;

import mediators.ProxyMediator;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ControllerIOHandler;
import utils.events.SocketAddressInfoEventArg;
import utils.events.SocketEventObserver;

import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.util.ArrayList;

public class ProxyBuilder {
    private static ProxyMediator ProxyMediator;
    private static final ArrayList<ControllerIOHandler> CONTROLLERS_REGIONS = new ArrayList<>();
    private static ConnectionAcceptorIOHandler switches;

    private static ProxyBuilder instance;

    public static ProxyBuilder createInstance() throws RuntimeErrorException {
        if (ProxyBuilder.instance != null) {
            throw new RuntimeErrorException(new Error("Instance is already created"));
        }
        ProxyBuilder.switches = new ConnectionAcceptorIOHandler();

        ProxyBuilder.ProxyMediator = new ProxyMediator(switches);
        ProxyBuilder.instance = new ProxyBuilder();
        return instance;
    }

    public ProxyBuilder addController(String ip, int port) {
        ControllerIOHandler connectorElement = new ControllerIOHandler(ip, port);
        ProxyBuilder.ProxyMediator.registerController(connectorElement);
        CONTROLLERS_REGIONS.add(connectorElement);

        return instance;
    }

    public ProxyBuilder startServer(String ip, int port) throws IOException {
        switches.createServer(new SocketAddressInfoEventArg(ip, port));
        return instance;
    }

    public  ProxyBuilder addWatcher(SocketEventObserver watcher) {
        ProxyBuilder.ProxyMediator.registerWatcher(watcher);
        return instance;
    }

    public ProxyMediator getMediator() {
        return ProxyBuilder.ProxyMediator;
    }

    public int getControllerCount() {
        return CONTROLLERS_REGIONS.size();
    }
}
