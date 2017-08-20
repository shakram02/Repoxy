package watchers;

import com.google.common.eventbus.EventBus;
import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.ControllerFailureArgs;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;
import utils.logging.ConsoleColors;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class OFPacketSynchronizer implements SocketEventObserver {
    private final Logger logger;
    private final OFPacketDiffer differ;
    private final int windowSize;
    private final EventBus mediatorNotifier;

    public OFPacketSynchronizer(int windSize, SocketEventObserver mediator, int timestampThreshold) {
        this.logger = Logger.getLogger(OFPacketSynchronizer.class.getName());

        windowSize = windSize;
        this.differ = new OFPacketDiffer(windSize, timestampThreshold);

        mediatorNotifier = new EventBus(OFPacketSynchronizer.class.getName());
        mediatorNotifier.register(mediator);
    }

    @Override
    public void dispatchEvent(@NotNull final SocketEventArguments arg) {

        if (!(arg instanceof SocketDataEventArg)) {
            return;
        }

        SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
        SenderType sender = dataEventArg.getSenderType();
        List<OFPacket> packets = dataEventArg.getPackets();

        this.logger.info("\n" + this.stringifyPackets(sender, packets));

        if (sender == SenderType.SwitchesRegion) {
            // Switch packets need not be compared
            return;
        }

        int mismatchedPacketCount =
                this.countMismatchedPackets(sender, packets, dataEventArg.getTimeStamp());


        if (mismatchedPacketCount >= (this.windowSize / 2)) {
            // Alert!
            logger.warning("Changing controller!!!");
            differ.setLastValidTime(arg.getTimeStamp());
            mediatorNotifier.post(new ControllerFailureArgs());
        }
    }


    private int countMismatchedPackets(SenderType sender, List<OFPacket> packets, long timestamp) {

        // Add each packet to its corresponding window
        packets.forEach(p -> {
            if (sender == SenderType.ControllerRegion) {
                this.differ.addToPrimaryWindow(p, timestamp);
            } else if (sender == SenderType.ReplicaRegion) {
                this.differ.addToSecondaryWindow(p, timestamp);
            }
        });

        return this.differ.countUnmatchedPackets();
    }

    private String stringifyPackets(SenderType sender, List<OFPacket> packets) {
        StringBuilder infoBuilder = new StringBuilder();
        String color = "";
        switch (sender) {
            case ReplicaRegion:
                color = ConsoleColors.CYAN_BRIGHT;
                break;
            case ControllerRegion:
                color = ConsoleColors.GREEN_BRIGHT;
                break;
            case SwitchesRegion:
                color = ConsoleColors.BLUE;
                break;
        }
        infoBuilder.append(color);
        infoBuilder.append("From:");
        infoBuilder.append(sender);
        infoBuilder.append("\n");

        packets.forEach(p -> {
            infoBuilder.append("\t");
            infoBuilder.append(p.getHeader().getXId());
            infoBuilder.append(" ");
            infoBuilder.append(p.getPacketType());
            infoBuilder.append("\n\t\t");
            infoBuilder.append(Arrays.toString(OFStreamParser.serializePacket(p).array()));
            infoBuilder.append("\n");
        });
        infoBuilder.append(ConsoleColors.RESET);

        return infoBuilder.toString();
    }
}
