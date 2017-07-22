package mediators;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import proxylet.Proxylet;
import regions.ControllersRegion;
import regions.SwitchesRegion;
import utils.ControllerChangeEventArg;
import utils.EventType;
import utils.SenderType;
import utils.SocketEventArg;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ahmed on 7/18/17.
 */
public class BaseMediator extends Proxylet {
    private SwitchesRegion switchesRegion;
    private final EventBus controllerNotifier;
    private final ArrayList<ControllersRegion> controllerRegions;
    private int connectedCount;

    public BaseMediator() {
        super(SenderType.Mediator);
        controllerNotifier = new EventBus(BaseMediator.class.getName());
        this.controllerRegions = new ArrayList<>();
    }

    public void setSwitchesRegion(SwitchesRegion switchesRegion) {
        this.switchesRegion = switchesRegion;
    }

    public void registerController(ControllersRegion region) {
        this.controllerRegions.add(region);
        this.controllerNotifier.register(region);
    }

    /**
     * Someone notified the proxylet for an event, dispatch it to the correct
     * method
     * <p>
     * This method also receives event bus notifications when the Mediator
     * is registered to lower layers
     * <p>
     * This method is marked as synchronized as events from controllers
     * and from switches will need to access this method. thus, this
     * method is its synchronization point
     *
     * @param arg socket event data
     */
    @Subscribe
    public synchronized void dispatchEvent(@NotNull SocketEventArg arg) {
        SenderType senderType = arg.getSenderType();
        SocketEventArg redirected = SocketEventArg.Redirect(SenderType.Mediator, arg);

        if (senderType == SenderType.SwitchesRegion) {
            this.controllerNotifier.post(redirected);
        } else {
            if (senderType == SenderType.ControllerRegion) {
                this.switchesRegion.dispatchEvent(redirected);
            } else if (senderType == SenderType.ReplicaRegion) {
                this.onReplicaEvent(redirected);
            }
        }
        this.postDispatch(arg); // FIXME, I NEED CODE!!!
    }


    protected void postDispatch(SocketEventArg arg) {
        // TODO Add to packet diff
        SenderType senderType = arg.getSenderType();
        EventType eventType = arg.getReplyType();

        if (senderType == SenderType.SwitchesRegion) {
            if (eventType == EventType.Connection) {
                this.connectedCount++;
            } else if (eventType == EventType.Disconnection) {
                this.connectedCount--;
            }
        }
    }

    public boolean hasClients() {
        return this.connectedCount > 0;
    }

    private void onReplicaEvent(SocketEventArg arg) {
        System.out.println(String.format("Event from replica:%s", arg));
    }

    public void setActiveController(String ip, int port) {
        ControllerChangeEventArg arg = new ControllerChangeEventArg(ip, port);
        this.controllerNotifier.post(arg);
    }

    @Override
    public void close() throws IOException {
        this.switchesRegion.close();
        for (ControllersRegion region : this.controllerRegions) {
            region.close();
        }
    }

    @Override
    public void cycle() {
        this.switchesRegion.cycle();
        for (ControllersRegion region : this.controllerRegions) {
            region.cycle();
        }
    }

}
