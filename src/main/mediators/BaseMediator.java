package mediators;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import of_packets.PacketHeader;
import org.jetbrains.annotations.NotNull;
import proxylet.Proxylet;
import regions.ControllersRegion;
import regions.SwitchesRegion;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

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

    /**
     * Adds a controller to the list of registered controller
     * to receive socket events
     *
     * @param region Properly initialized {@link ControllersRegion}
     */
    public void registerController(@NotNull ControllersRegion region) {
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
     * @param arg socket event data containing the Sender and Event types
     */
    @Subscribe
    public synchronized void dispatchEvent(@NotNull SocketEventArguments arg) {
        SenderType senderType = arg.getSenderType();

        SocketEventArguments redirected = arg.createRedirectedCopy(SenderType.Mediator);

        if (senderType == SenderType.SwitchesRegion) {
            this.controllerNotifier.post(redirected);
        } else {
            if (senderType == SenderType.ControllerRegion) {
                this.switchesRegion.dispatchEvent(redirected);
            } else if (senderType == SenderType.ReplicaRegion) {
                this.onReplicaEvent(arg);
            }
        }
        this.postDispatch(arg);
    }

    /**
     * Performs user-defined checks after the event has been processed
     *
     * @param arg Event argument containing Type and Sender of the event
     */
    protected void postDispatch(@NotNull SocketEventArguments arg) {
        // TODO Add to packet diff
        SenderType senderType = arg.getSenderType();
        EventType eventType = arg.getReplyType();

        if (eventType == EventType.SendData) {
            PacketHeader header = PacketHeader.ParsePacket(((SocketDataEventArg) arg).getExtraData());

            if (header.isInvalid()) {
                logger.log(Level.INFO,"Invalid OF PACKET");
                return;
            }
            System.out.println(String.format("OF PACKET [%s] - LEN:[%d]",
                    header.getMessage_type(), header.getLen()));
        }

        if (senderType == SenderType.SwitchesRegion) {
            if (eventType == EventType.Connection) {
                this.connectedCount++;
            } else if (eventType == EventType.Disconnection) {
                this.connectedCount--;
            }
        } else if (senderType == SenderType.ReplicaRegion && eventType == EventType.Disconnection) {
            this.logger.log(Level.INFO, "Replicated controller disconnected, this will be ignored");
        }
    }

    public boolean hasClients() {
        return this.connectedCount > 0;
    }

    private void onReplicaEvent(@NotNull SocketEventArguments arg) {
//        System.out.println(String.format("Event from replica:%s", arg));
    }

    public void setActiveController(@NotNull String ip, int port) {
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
