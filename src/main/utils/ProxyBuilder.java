package utils;

import mediators.BaseMediator;
import network_io.ConnectionAcceptorIOHandler;
import network_io.ConnectionCreatorIOHandler;
import regions.ControllersRegion;
import regions.SwitchesRegion;

import java.io.IOException;
import java.util.ArrayList;

public class ProxyBuilder {
    private static final BaseMediator mediator = new BaseMediator();
    private static final ArrayList<ControllersRegion> controllerRegions = new ArrayList<>();
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
        switches.setMediator(mediator);
        mediator.setSwitchesRegion(switches);
        return instance;
    }

    public ProxyBuilder BuildController(String ip, int port) {
        ConnectionCreatorIOHandler connectorElement = new ConnectionCreatorIOHandler();
        ControllersRegion controllerRegion = new ControllersRegion(connectorElement, ip, port);
        connectorElement.setUpperLayer(controllerRegion);
        controllerRegion.setMediator(mediator);

        mediator.registerController(controllerRegion);
        controllerRegions.add(controllerRegion);

        return instance;
    }

    public ProxyBuilder startServer(String ip, int port) throws IOException {
        switches.startListening(ip, port);
        return instance;
    }

    public BaseMediator getMediator() {
        return mediator;
    }

    public int getControllerCount() {
        return controllerRegions.size();
    }
}
