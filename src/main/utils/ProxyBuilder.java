package utils;

import mediators.BaseMediator;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ControllerIOHandler;

import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.util.ArrayList;

public class ProxyBuilder {
    private static BaseMediator BaseMediator;
    private static final ArrayList<ControllerIOHandler> CONTROLLERS_REGIONS = new ArrayList<>();
    private static ConnectionAcceptorIOHandler switches;

    private static ProxyBuilder instance;

    public static ProxyBuilder createInstance() throws RuntimeErrorException {
        if (ProxyBuilder.instance != null) {
            throw new RuntimeErrorException(new Error("Instance is already created"));
        }
        ProxyBuilder.switches = new ConnectionAcceptorIOHandler();

        ProxyBuilder.BaseMediator = new BaseMediator(switches);
        ProxyBuilder.instance = new ProxyBuilder();
        return instance;
    }

    public ProxyBuilder BuildController(String ip, int port) {
        ControllerIOHandler connectorElement = new ControllerIOHandler(SenderType.ReplicaRegion, ip, port);
        ProxyBuilder.BaseMediator.registerController(connectorElement);
        CONTROLLERS_REGIONS.add(connectorElement);

        return instance;
    }

    public ProxyBuilder startServer(String ip, int port) throws IOException {
        switches.createServer(new SocketAddressInfoEventArg(ip, port));
        return instance;
    }

    public BaseMediator getMediator() {
        return ProxyBuilder.BaseMediator;
    }

    public int getControllerCount() {
        return CONTROLLERS_REGIONS.size();
    }
}
