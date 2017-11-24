package tests;

import tests.of_packets.OFPacket;
import tests.of_packets.OFStreamParser;
import tests.utils.ConnectionId;
import tests.utils.SenderType;
import tests.utils.events.ImmutableSocketDataEventArg;
import tests.utils.events.SocketDataEventArg;

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
