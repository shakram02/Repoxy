package helpers;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import utils.ConnectionId;
import utils.SenderType;
import utils.events.ImmutableSocketDataEventArg;
import utils.events.SocketDataEventArg;

public class TestPacketArgMaker {

    private static SocketDataEventArg createArg(int connectionId, OFPacket packet, SenderType sender) {
        return ImmutableSocketDataEventArg.builder()
                .senderType(sender)
                .id(ConnectionId.CreateForTesting(connectionId))
                .packet(packet)
                .build();
    }

    public static SocketDataEventArg createFromPacket(int connectionId, byte[] bytes, SenderType sender) {
        return createArg(connectionId, OFStreamParser.parseStream(bytes).get(0), sender);
    }
}
