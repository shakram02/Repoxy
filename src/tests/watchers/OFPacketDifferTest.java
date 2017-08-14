package watchers;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class OFPacketDifferTest {
    @Test
    void checkSimilar() {
        OFPacketDiffer differ = new OFPacketDiffer(3, 100);
        byte[] helloBytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};
        OFPacket hello = OFStreamParser.parseStream(helloBytes).getPackets().get(0);

        byte[] barrierReplyBytes = new byte[]{1, 19, 0, 8, 0, 0, 0, 1};
        OFPacket barrierReply = OFStreamParser.parseStream(barrierReplyBytes).getPackets().get(0);

        // This packet should timeout
        differ.addToPrimaryWindow(hello, 100);
        differ.addToSecondaryWindow(hello, 201);

        Assert.assertTrue(differ.countUnmatchedPackets() == 1);

        differ.clearPacketQueues();

        differ.addToPrimaryWindow(barrierReply, 100);
        differ.addToSecondaryWindow(barrierReply, 101);

        Assert.assertTrue(differ.countUnmatchedPackets() == 0);
    }

    @Test
    void checkNotFound() {
        OFPacketDiffer differ = new OFPacketDiffer(3, 101);

        byte[] helloBytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};
        OFPacket hello = OFStreamParser.parseStream(helloBytes).getPackets().get(0);
        differ.addToPrimaryWindow(hello, 10);

        byte[] barrierReplyBytes = new byte[]{1, 19, 0, 8, 0, 0, 0, 1};
        OFPacket barrierReply = OFStreamParser.parseStream(barrierReplyBytes).getPackets().get(0);
        differ.addToSecondaryWindow(barrierReply, 11);

        Assert.assertTrue(differ.countUnmatchedPackets() == 1);
    }
}