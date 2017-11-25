package tests;

import middleware.blocking.PacketMatcher;
import org.junit.Assert;
import org.junit.Test;
import utils.SenderType;
import utils.events.SocketDataEventArg;

public class PacketMatcherTest {
    @Test
    public void simpleTest() {
        PacketMatcher matcher = new PacketMatcher(120);


        matcher.addInput(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        matcher.addInput(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        matcher.execute();

        SocketDataEventArg eventArg = matcher.getOutput();
        // Last packet is emitted
        Assert.assertTrue(eventArg.getSenderType() == SenderType.ControllerRegion);
        Assert.assertTrue(matcher.hasOutput());
    }

    @Test
    public void timeoutTest() throws InterruptedException {
        PacketMatcher matcher = new PacketMatcher(10);


        matcher.addInput(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        matcher.addInput(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierRequestXid54, SenderType.ControllerRegion));

        Thread.sleep(10);

        matcher.addInput(TestPacketArgMaker.
                createFromPacket(1, TestPackets.FeaturesRequestXid11, SenderType.ControllerRegion));

        matcher.execute();

        // The first 2 packets will have timed out
        Assert.assertTrue(matcher.hasError());
        // No output exists as no packets match
        Assert.assertFalse(matcher.hasOutput());
    }
}
