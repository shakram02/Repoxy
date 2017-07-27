package mediators;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import network_io.ConnectionAcceptorIOHandler;
import network_io.interfaces.SocketIOer;
import of_packets.OFPacket;
import of_packets.OFStreamParseResult;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import proxylet.Proxylet;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The mediator registers for events from the controller side and switches side
 */
public class BaseMediator extends Proxylet {
    private final ConnectionAcceptorIOHandler switchSockets;

    private final EventBus controllerNotifier;
    private final ArrayList<SocketIOer> controllerRegions; // Replace with connection creator
    private int connectedCount;

    public BaseMediator(ConnectionAcceptorIOHandler switchSockets) {
        super(SenderType.Mediator);
        controllerNotifier = new EventBus(BaseMediator.class.getName());
        this.controllerRegions = new ArrayList<>();
        this.switchSockets = switchSockets;
        this.switchSockets.registerForEvents(this);
    }


    /**
     * Adds a controller to the list of registered controller
     * to receive socket events
     *
     * @param region Properly initialized {@link SocketIOer}
     */
    public void registerController(@NotNull SocketIOer region) {
        this.controllerRegions.add(region);
        // Controller registers for socket events
        region.registerForEvents(this);

        // Mediator notifies controller with events
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
                // Log non data events
                if (arg.getReplyType() != EventType.SendData) {
                    this.logger.info(arg.toString());
                }

                this.switchSockets.addToCommandQueue(redirected);
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
            OFStreamParseResult result = OFStreamParser.
                    parseStream(((SocketDataEventArg) arg).getExtraData().toByteArray());

            if (!result.hasPackets() && result.hasRemaining()) {
                this.logger.info("Invalid OF PACKET");
                return;
            }


            if (result.hasPackets()) {
                ImmutableList<OFPacket> packets = result.getPackets();
                this.logger.info(String.format("#%d OF PACKETS", packets.size()));
            }

            if (result.hasRemaining()) {
                throw new RuntimeException(String.format("Remaining: %d bytes",
                        result.getRemaining().length));
            }

        }

        if (senderType == SenderType.SwitchesRegion) {
            if (eventType == EventType.Connection) {
                this.connectedCount++;
            } else if (eventType == EventType.Disconnection) {
                this.connectedCount--;
            }
        } else if (senderType == SenderType.ReplicaRegion && eventType == EventType.Disconnection) {
            this.logger.warning("Replicated controller disconnected, this will be ignored");
        }
    }

    public boolean hasClients() {
        return this.connectedCount > 0;
    }

    private void onReplicaEvent(@NotNull SocketEventArguments arg) {
//        this.logger.finest(String.format("Event from replica:%s", arg));
    }

    public void setActiveController(@NotNull String ip, int port) {
        SocketAddressInfoEventArg arg = new SocketAddressInfoEventArg(ip, port);
        this.controllerNotifier.post(arg);
    }

    @Override
    public void close() throws IOException {
        this.switchSockets.close();
        for (SocketIOer region : this.controllerRegions) {
            region.close();
        }
    }

    public void cycle() throws IOException {
        try {
            // Do socket io cycle
            this.switchSockets.cycle();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (SocketIOer region : this.controllerRegions) {
            region.cycle();
        }
    }

}
