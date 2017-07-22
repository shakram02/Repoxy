package utils;

import mediators.BaseMediator;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ConnectionCreatorIOHandler;
import regions.ControllersRegion;
import regions.SwitchesRegion;

import java.io.IOException;
import java.util.ArrayList;

public class ProxyBuilder {
    private static final BaseMediator BASE_MEDIATOR = new BaseMediator();
    private static final ArrayList<ControllersRegion> CONTROLLERS_REGIONS = new ArrayList<>();
    private static SwitchesRegion switches;

    private static ProxyBuilder instance;

    public static ProxyBuilder createInstance() {
        if (ProxyBuilder.instance != null) {
            throw new IllegalStateException("Instance is already created");
        }
        ProxyBuilder.instance = new ProxyBuilder();
        return instance;
    }

    public ProxyBuilder BuildSwitchesRegion() {
        ConnectionAcceptorIOHandler acceptorIOHandler = new ConnectionAcceptorIOHandler();
        switches = new SwitchesRegion(acceptorIOHandler);
        acceptorIOHandler.setConnectionAcceptor(switches);
        switches.setMediator(BASE_MEDIATOR);
        BASE_MEDIATOR.setSwitchesRegion(switches);
        return instance;
    }

    public ProxyBuilder BuildController(String ip, int port) {
        ConnectionCreatorIOHandler connectorElement = new ConnectionCreatorIOHandler();
        ControllersRegion controllerRegion = new ControllersRegion(connectorElement, ip, port);
        connectorElement.setUpperLayer(controllerRegion);
        controllerRegion.setMediator(BASE_MEDIATOR);

        BASE_MEDIATOR.registerController(controllerRegion);
        CONTROLLERS_REGIONS.add(controllerRegion);

        return instance;
    }

    public ProxyBuilder startServer(String ip, int port) throws IOException {
        switches.startListening(ip, port);
        return instance;
    }

    public BaseMediator getMediator() {
        return BASE_MEDIATOR;
    }

    public int getControllerCount() {
        return CONTROLLERS_REGIONS.size();
    }
}
