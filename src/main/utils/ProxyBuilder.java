package utils;

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

    public void startServer(String ip, int port) throws IOException {
        switches.createServer(
                ImmutableSocketAddressInfoEventArg
                        .builder()
                        .ip(ip)
                        .port(port)
                        .build());

    }

    public ProxyMediator getMediator() {
        return ProxyBuilder.ProxyMediator;
    }
}
