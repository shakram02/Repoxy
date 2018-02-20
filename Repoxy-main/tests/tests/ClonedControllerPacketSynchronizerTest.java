package tests;

import middleware.blocking.io_synchronizer.ClonedControllerPacketSynchronizer;
import openflow.OFMsgType;
import org.junit.Assert;
import org.junit.Test;
import utils.SenderType;

public class ClonedControllerPacketSynchronizerTest {
    @Test
    public void testSyncSimple() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        // Add the query
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(),
                OFMsgType.OFPT_BARRIER_REPLY));


        // Nothing left
        Assert.assertTrue(AssertionHelper.absence(synchronizer::getSynced));
    }

    @Test
    public void testSyncReversedSimple() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        // Add the reply
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        // Asserting false because replies shouldn't be released until their request was added
        Assert.assertTrue(AssertionHelper.absence(synchronizer::getSynced));

        // Add the query
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REPLY));

        // Nothing left
        Assert.assertTrue(AssertionHelper.absence(synchronizer::getSynced));
    }

    @Test
    public void testSyncTwoRequestOneReply() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        // Add the query
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        // Add the query
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        // Add the query
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REPLY));

        // Packet with ConnectionId 1 won't be released until its reply arrive, so now the toSwitches
        // queue to controller is empty
        Assert.assertTrue(AssertionHelper.absence(synchronizer::getSynced));
    }

    @Test
    public void testSwitchRequest() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.SwitchesRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REPLY));
    }

    @Test
    public void testSwitchRequestReversed() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();


        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REPLY));
    }

    @Test
    public void testDifferentConnectionIDs() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        // Request comes out
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REPLY));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REPLY));
        Assert.assertTrue(AssertionHelper.absence(synchronizer::getSynced));
    }

    @Test
    public void testSyncTwoRequestTwoReplies() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        // Now the replies are only remaining, they won't go out
        Assert.assertTrue(AssertionHelper.absence(synchronizer::getSynced));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        // Request and reply come out
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REPLY));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        // Request and reply come out
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer.getSynced().get(), OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertTrue(AssertionHelper.absence(synchronizer::getSynced));
    }

    @Test
    public void testSyncReplyWithNoQuery() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(AssertionHelper.absence(synchronizer::getSynced));
    }
}