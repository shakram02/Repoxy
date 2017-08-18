package watchers;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.ControllerFailureArgs;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;
import utils.logging.ConsoleColors;

import java.util.List;
import java.util.logging.Logger;

public class OFPacketSynchronizer implements SocketEventObserver {
    private final Logger logger;
    private final OFPacketDiffer differ;
    private final int windowSize;
    private final EventBus mediatorNotifier;

    public OFPacketSynchronizer(int windSize, SocketEventObserver mediator) {
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

        ImmutableList<OFPacket> filteredPackets = this.filterEchoPackets(dataEventArg);

        if (sender == SenderType.SwitchesRegion || filteredPackets.isEmpty()) {
            // Switches packets need not be compared
            return;
        }

        int mismatchedPacketCount = this.countMismatchedPackets(sender, filteredPackets);
        this.logger.info(ConsoleColors.WHITE_BOLD_BRIGHT + "Unmatched:"
                + mismatchedPacketCount + ConsoleColors.RESET +
                "\n" + this.stringifyPackets(filteredPackets));



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

    private ImmutableList<OFPacket> filterEchoPackets(SocketDataEventArg dataEventArg) {
        return dataEventArg.getPackets().stream()
                .filter(p -> !p.getPacketType().startsWith("Echo"))
                .collect(ImmutableList.toImmutableList());

    }


    private String stringifyPackets(List<OFPacket> packets) {
        StringBuilder infoBuilder = new StringBuilder();
        packets.forEach(p -> {
            infoBuilder.append(p.toString());
            infoBuilder.append(" ");
        });

        return infoBuilder.toString();
    }
}
