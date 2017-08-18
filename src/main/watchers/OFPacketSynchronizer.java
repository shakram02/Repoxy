package watchers;

import com.google.common.eventbus.EventBus;
import of_packets.OFPacket;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.*;
import utils.logging.ConsoleColors;
import utils.logging.NetworkLogLevels;

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
    public void dispatchEvent(@NotNull SocketEventArguments arg) {

        if (arg.getReplyType() != EventType.SendData) {
            return;
        }

        SenderType sender = arg.getSenderType();
        List<OFPacket> packets = ((SocketDataEventArg) arg).getPackets();
        this.logger.info(arg.toString());

        if (arg.getSenderType() == SenderType.SwitchesRegion) {
            return;
        }

        int mismatchedPacketCount = this.countMismatchedPackets(sender, packets);


        if (mismatchedPacketCount >= (this.windowSize / 2)) {
            // Alert!
            logger.warning("Changing controller!!!");
            differ.setLastValidTime(arg.getTimeStamp());
            mediatorNotifier.post(new ControllerFailureArgs());
        }
    }


    private int countMismatchedPackets(SenderType sender, List<OFPacket> packets, long timestamp) {

        for (OFPacket p : packets) {

            this.logger.log(NetworkLogLevels.getLevel(sender),
                    "xid:" + p.getHeader().getXId() + "  " + p.getPakcetType());

            if (sender == SenderType.ControllerRegion) {
                this.differ.addToPrimaryWindow(p, timestamp);
            } else if (sender == SenderType.ReplicaRegion) {
                this.differ.addToSecondaryWindow(p, timestamp);
            }
        }

        // FIXME needs some modification
        int unmatched = this.differ.countUnmatchedPackets();
        this.logger.log(NetworkLogLevels.DIFFER,
                ConsoleColors.RED_BOLD + "Unmatched:" + unmatched + ConsoleColors.RESET);

        return unmatched;
    }
}
