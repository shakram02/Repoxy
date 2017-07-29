package watchers;

import of_packets.OFPacket;
import of_packets.OFStreamParseResult;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.LineBasedStringBuilder;
import utils.SenderType;
import utils.events.EventType;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OFPacketVerifier implements SocketEventObserver {
    private final Logger logger;
    private final OFPacketDiffer differ;

    public OFPacketVerifier(int windSize) {
        this.logger = Logger.getLogger(OFPacketVerifier.class.getName());
        this.differ = new OFPacketDiffer(windSize);
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

        this.processPackets(sender, parseResult.get());
    }


    private void processPackets(SenderType sender, List<OFPacket> packets) {
        LineBasedStringBuilder sb = new LineBasedStringBuilder();

        for (OFPacket p : packets) {

            sb.appendTabbedLine(String.format("xid [%d] %s",
                    p.getHeader().getXId(), p.getPakcetType()));

            if (sender == SenderType.ControllerRegion) {
                this.differ.addToWindow(p);
            } else if (sender == SenderType.ReplicaRegion) {

                // FIXME needs some modification
                if (this.differ.checkInWindow(p)) {
                    sb.appendLine("##Packet in window");
                } else {
                    sb.appendLine("Packet wasn't matched");
                }
            }
        }

        this.logger.info(sb.toString());
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

        this.logger.info(String.format("\n#%d of-packets from [ %s ]",
                packetCount, arg.getSenderType()));

        return Optional.of(filtered);
    }

    private List<OFPacket> filterEcho(List<OFPacket> packets) {
        return packets
                .stream()
                .filter(p -> !p.getPakcetType().startsWith("Echo"))
                .collect(Collectors.toList());

    }
}
