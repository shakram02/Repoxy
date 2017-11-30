package tests;

import middleware.MiddlewareManager;
import middleware.blocking.PacketMatcher;
import org.junit.Assert;
import org.junit.Test;
import utils.ConnectionId;
import utils.SenderType;

public class MiddlewareManagerTests {
    @Test
    public void testSingleMiddleware() {
        // Take care that small threshold causes timeouts even if the program is being run
        // normally

        MiddlewareManager manager = new MiddlewareManager();
        manager.addMiddleware(new PacketMatcher());

        manager.addPacket(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        manager.addPacket(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        manager.cycle();

        Assert.assertTrue(manager.hasOutput());
        Assert.assertTrue(manager.getOutput().getId().equals(ConnectionId.CreateForTesting(1)));

        Assert.assertFalse(manager.hasOutput());
    }

    @Test
    public void testSingleMiddleware_NoOutput_DifferentIDs() {
        MiddlewareManager manager = new MiddlewareManager();
        manager.addMiddleware(new PacketMatcher());

        manager.addPacket(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        // Different connection IDs
        manager.addPacket(TestPacketArgMaker.
                createFromPacket(2, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        manager.cycle();

        // No output will be present because the packets won't be matched due to different IDs
        // and won't timeout since no input is there
        Assert.assertFalse(manager.hasOutput());
    }

    @Test(expected = IllegalStateException.class)
    public void testSingleMiddleware_Error_SameIDs() {
        MiddlewareManager manager = new MiddlewareManager();
        manager.addMiddleware(new PacketMatcher());

        manager.addPacket(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Timeout, exception thrown
        manager.addPacket(TestPacketArgMaker.
                createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        manager.cycle();
    }
}
