package utils.xid_sync;

import of_packets.XidSynchronizer;
import utils.events.ImmutableSocketDataEventArg;
import of_packets.OFStreamParser;
import org.junit.jupiter.api.Test;
import utils.ConnectionId;
import utils.SenderType;
import utils.events.SocketDataEventArg;
import utils.packet_store.PacketStore;

class XidSynchronizerTest {
    @Test
    void syncXid() {
        XidSynchronizer synchronizer = new XidSynchronizer(new PacketStore());

        /*
            From:ReplicaRegion
	            27 Features Request
		            [1, 5, 0, 8, 0, 0, 0, 27]
	            28 Features Request
       		        [1, 5, 0, 8, 0, 0, 0, 28]

       		Packet:type:Features Request xId:27 len:8 Insert:27
            Packet:type:Features Request xId:28 len:8 Insert:28
         */

        byte[] featuresRequest = new byte[]{1, 5, 0, 8, 0, 0, 0, 27, 1, 5, 0, 8, 0, 0, 0, 28};

        ConnectionId conId = ConnectionId.CreateForTesting(1);
        SocketDataEventArg replicaPackets = ImmutableSocketDataEventArg.builder()
                .senderType(SenderType.ReplicaRegion)
                .id(conId)
                .packets(OFStreamParser.parseStream(featuresRequest))
                .build();


        synchronizer.syncListXids(conId, replicaPackets.getPackets());

        /*
            From:ControllerRegion
	            14 Features Request
		            [1, 5, 0, 8, 0, 0, 0, 14]

            From:ControllerRegion
                15 Features Request
		            [1, 5, 0, 8, 0, 0, 0, 15]
         */
//        SocketDataEventArg controllerPackets = new SocketDataEventArg(SenderType.ControllerRegion,
//                ConnectionId.CreateForTesting(1), createFromArray(featuresRequest));
//        synchronizer.syncListXids(controllerPackets);

        /*
            From:SwitchesRegion
	14 Features Reply
		[1, 6, 0, -32, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, -2, 0, 0, 0, 0, 0, 0, -57, 0, 0, 15, -1, 0, 3, 14, 15, -22, 111, 62, -101, 115, 49, 45, 101, 116, 104, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -34, -2, -68, -76, 52, -38, 115, 49, 45, 101, 116, 104, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 62, -102, 46, -78, 123, -20, 115, 49, 45, 101, 116, 104, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -2, 126, -2, 20, -111, 13, 71, 115, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
         */

        byte[] featuresReplyFirst = new byte[]{1, 6, 0, -32, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, -2, 0, 0, 0, 0, 0, 0, -57, 0, 0, 15, -1, 0, 3, 14, 15, -22, 111, 62, -101, 115, 49, 45, 101, 116, 104, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -34, -2, -68, -76, 52, -38, 115, 49, 45, 101, 116, 104, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 62, -102, 46, -78, 123, -20, 115, 49, 45, 101, 116, 104, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -2, 126, -2, 20, -111, 13, 71, 115, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        SocketDataEventArg replyFirst = ImmutableSocketDataEventArg.builder()
                .senderType(SenderType.SwitchesRegion)
                .id(conId)
                .packets(OFStreamParser.parseStream(featuresReplyFirst))
                .build();

        synchronizer.syncListXids(conId, replyFirst.getPackets());

        byte[] featuresReplySecond = new byte[]{1, 6, 0, -32, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 1, 0, -2, 0, 0, 0, 0, 0, 0, -57, 0, 0, 15, -1, 0, 3, 78, 103, 78, -80, 57, -76, 115, 50, 45, 101, 116, 104, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -110, -55, -53, 94, -62, -51, 115, 50, 45, 101, 116, 104, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, -90, -23, 104, -6, 121, 122, 115, 50, 45, 101, 116, 104, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -2, -82, -14, -76, 80, -48, 73, 115, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        SocketDataEventArg replySecond =
                ImmutableSocketDataEventArg.builder()
                        .senderType(SenderType.SwitchesRegion)
                        .id(conId)
                        .packets(OFStreamParser.parseStream(featuresReplySecond))
                        .build();


        synchronizer.syncListXids(conId, replySecond.getPackets());
    }
}