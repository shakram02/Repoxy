package network_io;

import com.google.common.eventbus.EventBus;
import mediators.BaseMediator;
import network_io.interfaces.SocketIOer;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.SocketAddressInfoEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ControllerManager implements SocketEventObserver {
    private final EventBus controllerNotifier;
    private final EventBus mediatorNotifier;
    private final ArrayList<SocketIOer> controllerHandlers; // Replace with connection creator
    private final BaseMediator mediator;
    private final ConcurrentLinkedQueue<SocketEventArguments> eventArgQueue;

    public ControllerManager(BaseMediator mediator) {
        this.mediator = mediator;
        this.controllerNotifier = new EventBus(ControllerIOHandler.class.getName());
        this.controllerHandlers = new ArrayList<>();
        this.eventArgQueue = new ConcurrentLinkedQueue<>();

        // Event bus is used to notify the replica events concurrently
        // after dispatching the main controller's event
        this.mediatorNotifier = new EventBus();
        this.mediatorNotifier.register(mediator);
    }

    /**
     * Adds a controller to the list of registered controller
     * to receive socket events
     *
     * @param region Properly initialized {@link SocketIOer}
     */
    public void registerController(@NotNull SocketIOer region) {
        this.controllerHandlers.add(region);
        // Controller registers for socket events
        region.registerForEvents(this);

        if (region == ControllerIOHandler.getActiveControllerHandler()) {
            // The main controller doesn't register for mediator's events
            // as they'll be manually dispatched
            return;
        }

        // Mediator notifies controller with events
        this.controllerNotifier.register(region);

    }

    public void notifyControllers(SocketEventArguments arguments) {
        // TODO notify the main controller first, then the others
        ControllerIOHandler.getActiveControllerHandler().processEvent(arguments);
        this.controllerNotifier.post(arguments);
    }

    public void setActiveController(String ip, int port) {
        // In order to change the main controller, we first register the old main controller
        // to listen for events, then we change the active controller
        // the we un-register the new active controller from events as they'll
        // be manually dispatched by this class.
        this.controllerNotifier.register(ControllerIOHandler.getActiveControllerHandler());
        this.controllerNotifier.post(new SocketAddressInfoEventArg(ip, port));
        this.controllerNotifier.unregister(ControllerIOHandler.getActiveControllerHandler());

    }

    public void closeControllers() throws IOException {
        SocketIOer mainController = ControllerIOHandler.getActiveControllerHandler();
        mainController.close();

        for (SocketIOer controller : this.controllerHandlers) {
            if (controller == mainController) {
                continue;
            }

            controller.close();
        }
    }

    public void cycleControllers() throws IOException {
        SocketIOer mainController = ControllerIOHandler.getActiveControllerHandler();
        mainController.cycle();


        for (SocketIOer controller : this.controllerHandlers) {
            if (controller == mainController) {
                continue;
            }

            controller.cycle();
        }
    }

    @Override
    public void dispatchEvent(@NotNull SocketEventArguments eventArgs) {

        // Catch all the events and count them. dispatch the main controller's
        // event once it arrives, then dispatch all the others.
        // The main controller should notify first as its event is dispatched first
        // hence all the secondary controllers will notify afterwards.
        // TODO I'm not sure if this mechanism is true (that of all controllers will dispatch correctly)
        if (eventArgs.getSenderType() == SenderType.ControllerRegion) {
            this.mediator.dispatchEvent(eventArgs);

            for (SocketEventArguments arg : this.eventArgQueue) {
                this.mediatorNotifier.post(arg);
            }

            this.eventArgQueue.clear();

        } else {
            this.eventArgQueue.add(eventArgs);
        }
    }
}
