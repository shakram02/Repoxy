package tests;

import middleware.MiddlewareManager;
import middleware.blocking.PacketMatcher;
import of_packets.OFMsgType;
import org.junit.Assert;
import org.junit.Test;
import utils.SenderType;

public class MiddlewareManagerTests {
    @Test
    public void testSingleMiddleware() {
        // Take care that small threshold causes timeouts even if the program is being run
        // normally

        MiddlewareManager manager = new MiddlewareManager();
        manager.addMiddleware(new PacketMatcher());
        int xid = 54; // Xid the same as in the test packet

        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1,
                manager.getOutput(), xid, OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertFalse(manager.hasOutput());
    }

    @Test
    public void testSingleMiddleware_TwoIDs_Ordered_Output() {
        // Take care that small threshold causes timeouts even if the program is being run
        // normally

        MiddlewareManager manager = new MiddlewareManager();
        manager.addMiddleware(new PacketMatcher());
        int xid = 54;

        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(2, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(2, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, manager.getOutput(),
                xid, OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(2, manager.getOutput(),
                xid, OFMsgType.OFPT_BARRIER_REPLY));
    }


    @Test
    public void testSingleMiddleware_NoOutput_DifferentIDs() {
        MiddlewareManager manager = new MiddlewareManager();
        manager.addMiddleware(new PacketMatcher());

        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        // Different connection IDs
        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(2, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        // No output will be present because the packets won't be matched due to different IDs
        // and won't timeout since no input is there
        Assert.assertFalse(manager.hasOutput());
    }

    @Test(expected = IllegalStateException.class)
    public void testSingleMiddleware_Error_SameIDs() {
        MiddlewareManager manager = new MiddlewareManager();
        manager.addMiddleware(new PacketMatcher());

        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Timeout, exception thrown
        manager.addToPipeline(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

    }
}
