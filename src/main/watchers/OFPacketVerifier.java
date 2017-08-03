package watchers;

import com.google.common.eventbus.EventBus;
import of_packets.OFPacket;
import of_packets.OFStreamParseResult;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.*;
import utils.logging.ConsoleColors;
import utils.logging.NetworkLogLevels;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OFPacketVerifier implements SocketEventObserver {
    private final Logger logger;
    private final OFPacketDiffer differ;
    private final int windowSize;
    private final EventBus mediatorNotifier;

    public OFPacketVerifier(int windSize, SocketEventObserver mediator) {
        this.logger = Logger.getLogger(OFPacketVerifier.class.getName());
        windowSize = windSize;
        this.differ = new OFPacketDiffer(windSize);

        mediatorNotifier = new EventBus(OFPacketVerifier.class.getName());
        mediatorNotifier.register(mediator);
    }

    @Override
    public void dispatchEvent(@NotNull SocketEventArguments arg) {

        if (arg.getReplyType() != EventType.SendData) {
            return;
        }

        SenderType sender = arg.getSenderType();
        Optional<List<OFPacket>> parseResult = this.parseDataStream((SocketDataEventArg) arg);

        if (!parseResult.isPresent()) {
            return;
        }

        int mismatchedPacketCount = this.countMismatchedPackets(sender, parseResult.get());

        if (mismatchedPacketCount >= (this.windowSize / 2)) {
            // Alert!
            logger.warning("Changing controller!!!");
            mediatorNotifier.post(new ControllerFailureArgs());
        }
    }


    private int countMismatchedPackets(SenderType sender, List<OFPacket> packets) {

        for (OFPacket p : packets) {

            this.logger.log(NetworkLogLevels.getLevel(sender),
                    "xid:" + p.getHeader().getXId() + "  " + p.getPakcetType());

            if (sender == SenderType.ControllerRegion) {
                this.differ.addToPrimaryWindow(p);
            } else if (sender == SenderType.ReplicaRegion) {
                this.differ.addToSecondaryWindow(p);
            }
        }

        // FIXME needs some modification
        int unmatched = this.differ.countUnmatchedPackets();
        this.logger.log(NetworkLogLevels.DIFFER,
                ConsoleColors.WHITE_BOLD_BRIGHT + "Unmatched:" + unmatched);

        return unmatched;
    }

    private Optional<List<OFPacket>> parseDataStream(SocketDataEventArg arg) {
        OFStreamParseResult parsedPackets = OFStreamParser.
                parseStream(arg.getExtraData().toByteArray());

        if (!parsedPackets.hasPackets()) {
            return Optional.empty();
        }

        List<OFPacket> filtered = this.filterEcho(parsedPackets.getPackets());
        int packetCount = filtered.size();


        if (packetCount == 0) {
            return Optional.empty();    // FIXME Skip echos
        }

        this.logger.log(NetworkLogLevels.getLevel(arg), ">> " + packetCount + " packets");

        return Optional.of(filtered);
    }

    private List<OFPacket> filterEcho(List<OFPacket> packets) {
        return packets
                .stream()
                .filter(p -> !p.getPakcetType().startsWith("Echo"))
                .collect(Collectors.toList());

    }
}
