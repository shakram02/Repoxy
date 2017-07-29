package verifiers;

import of_packets.OFPacket;
import of_packets.OFStreamParseResult;
import of_packets.OFStreamParser;
import utils.events.EventType;
import utils.SenderType;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OFPacketVerifier implements SocketEventWatcher {
    Logger logger;

    public OFPacketVerifier() {
        this.logger = Logger.getLogger(OFPacketVerifier.class.getName());
    }

    @Override
    public void processEvent(SocketEventArguments arg) {
        if (arg.getReplyType() != EventType.SendData ||
                arg.getSenderType() == SenderType.ReplicaRegion) {
            return;
        }

        String sender = arg.getSenderType().toString();

        OFStreamParseResult result = OFStreamParser.
                parseStream(((SocketDataEventArg) arg).getExtraData().toByteArray());

        if (!result.hasPackets() && result.hasRemaining()) {
            System.out.println("Invalid OF PACKET");
            return;
        }

        if (result.hasPackets()) {
            List<OFPacket> filtered =
                    result.getPackets()
                            .stream()
                            .filter(p -> !p.getPakcetType().startsWith("Echo"))
                            .collect(Collectors.toList());

            int packetCount = filtered.size();

            if (packetCount == 0) {
                return;
            }

            System.out.println(String.format("\n#%d of-packets", packetCount));
            for (OFPacket p : filtered) {
                System.out.println(String.format("[%s] \t xid [%d] %s ", sender,
                        p.getHeader().getXId(), p.getPakcetType()));
            }
        }
    }
}
