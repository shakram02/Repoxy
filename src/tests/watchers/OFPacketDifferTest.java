package watchers;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class OFPacketDifferTest {
    @Test
    void checkSimilar() {
        OFPacketDiffer differ = new OFPacketDiffer(3);
        byte[] helloBytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};
        OFPacket hello = OFStreamParser.parseStream(helloBytes).getPackets().get(0);

        byte[] barrierReplyBytes = new byte[]{1, 19, 0, 8, 0, 0, 0, 1};
        OFPacket barrierReply = OFStreamParser.parseStream(barrierReplyBytes).getPackets().get(0);

        differ.addToPrimaryWindow(hello);
        differ.addToPrimaryWindow(barrierReply);

        differ.addToSecondaryWindow(hello);
        differ.addToSecondaryWindow(barrierReply);

        Assert.assertTrue(differ.countUnmatchedPackets() == 0);
        Assert.assertTrue(differ.countUnmatchedPackets() == 0);
    }

    @Test
    void checkNotFound() {
        OFPacketDiffer differ = new OFPacketDiffer(3);
        byte[] helloBytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};
        OFPacket hello = OFStreamParser.parseStream(helloBytes).getPackets().get(0);
        differ.addToPrimaryWindow(hello);

        byte[] barrierReplyBytes = new byte[]{1, 19, 0, 8, 0, 0, 0, 1};
        OFPacket barrierReply = OFStreamParser.parseStream(barrierReplyBytes).getPackets().get(0);
        differ.addToSecondaryWindow(barrierReply);

        Assert.assertTrue(differ.countUnmatchedPackets() == 1);
    }
}