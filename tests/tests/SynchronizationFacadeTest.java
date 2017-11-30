package tests;

import middleware.blocking.io_synchronizer.SynchronizationFacade;
import of_packets.OFMsgType;
import org.junit.Assert;
import org.junit.Test;
import utils.SenderType;

public class SynchronizationFacadeTest {
    @Test
    public void simpleOnePacket() {
        SynchronizationFacade f = new SynchronizationFacade();
        int requestXid = 54;
        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        f.execute();

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(), requestXid,
                OFMsgType.OFPT_BARRIER_REQUEST));

        f.addInput(TestPacketArgMaker
                .createFromPacket(1, TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        f.execute();
        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(),
                requestXid, OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertFalse(f.hasOutput());
    }

    @Test
    public void simpleOnePacketReversedDifferentXids() {
        SynchronizationFacade f = new SynchronizationFacade();
        int requestXid = 11;

        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.SwitchesRegion));

        // Don't forget to execute before assertion
        f.execute();
        Assert.assertFalse(f.hasOutput());

        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid11, SenderType.ReplicaRegion));

        // Request and reply come out with the xid of the request
        f.execute();
        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(),
                requestXid, OFMsgType.OFPT_FEATURES_REQUEST));

        f.execute();
        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(),
                requestXid, OFMsgType.OFPT_FEATURES_REPLY));

        f.execute();
        Assert.assertFalse(f.hasOutput());   // Assert that nothing is left
    }

    @Test
    public void simpleOnePacketReversedSameXids() {
        SynchronizationFacade f = new SynchronizationFacade();
        int requestXid = 10;

        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.SwitchesRegion));

        f.execute();
        Assert.assertFalse(f.hasOutput());

        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid10, SenderType.ReplicaRegion));

        // Request and reply come out with the xid of the request
        f.execute();
        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(),
                requestXid, OFMsgType.OFPT_FEATURES_REQUEST));

        f.execute();
        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(),
                requestXid, OFMsgType.OFPT_FEATURES_REPLY));

        f.execute();
        Assert.assertFalse(f.hasOutput());   // Assert that nothing is left
    }

    @Test
    public void twoPacketsSameXidsDifferentConnections() {
        SynchronizationFacade f = new SynchronizationFacade();

        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.SwitchesRegion));

        f.addInput(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        f.addInput(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid10, SenderType.ReplicaRegion));

        f.execute();
        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(2, f.getOutput(), 54,
                OFMsgType.OFPT_BARRIER_REQUEST));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(2, f.getOutput(), 54,
                OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(), 10,
                OFMsgType.OFPT_FEATURES_REQUEST));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(), 10,
                OFMsgType.OFPT_FEATURES_REPLY));
    }

    @Test
    public void twoPacketsDifferentXidsDifferentConnections() {
        SynchronizationFacade f = new SynchronizationFacade();
        int barrierRequestXid = 53;
        int barrierReplyXid = 11;

        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.SwitchesRegion));

        f.addInput(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid53, SenderType.ReplicaRegion));

        f.addInput(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        f.addInput(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid11, SenderType.ReplicaRegion));

        f.execute();

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(2, f.getOutput(), barrierRequestXid,
                OFMsgType.OFPT_BARRIER_REQUEST));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(2, f.getOutput(), barrierRequestXid,
                OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(), barrierReplyXid,
                OFMsgType.OFPT_FEATURES_REQUEST));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, f.getOutput(), barrierReplyXid,
                OFMsgType.OFPT_FEATURES_REPLY));
    }
}