package tests;

import tests.of_packets.OFMsgType;
import org.junit.Assert;
import org.junit.Test;
import tests.utils.SenderType;

public class SynchronizationFacadeTest {
    @Test
    public void simpleOnePacket() {
        FacadeTestHelper tester = new FacadeTestHelper();
        int requestXid = 54;

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));
        tester.facade.execute();

        Assert.assertTrue(tester.checkPacket(1, SenderType.ReplicaRegion, requestXid,
                OFMsgType.OFPT_BARRIER_REQUEST));

        tester.addUnSynchronized(TestPacketArgMaker
                .createFromPacket(1,
                        TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(tester.checkPacket(1, SenderType.SwitchesRegion,
                requestXid, OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertTrue(tester.absence());   // Assert that nothing is left
    }

    @Test
    public void simpleOnePacketReversedDifferentXids() {
        FacadeTestHelper tester = new FacadeTestHelper();

        int requestXid = 11;

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.SwitchesRegion));

        Assert.assertTrue(tester.absence());

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid11, SenderType.ReplicaRegion));

        // Request and reply come out with the xid of the request
        Assert.assertTrue(tester.checkPacket(1, SenderType.ReplicaRegion,
                requestXid, OFMsgType.OFPT_FEATURES_REQUEST));

        Assert.assertTrue(tester.checkPacket(1, SenderType.SwitchesRegion,
                requestXid, OFMsgType.OFPT_FEATURES_REPLY));

        Assert.assertTrue(tester.absence());   // Assert that nothing is left
    }

    @Test
    public void simpleOnePacketReversedSameXids() {
        FacadeTestHelper tester = new FacadeTestHelper();
        int requestXid = 10;

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.SwitchesRegion));

        Assert.assertTrue(tester.absence());

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid10, SenderType.ReplicaRegion));

        // Request and reply come out with the xid of the request
        Assert.assertTrue(tester.checkPacket(1, SenderType.ReplicaRegion,
                requestXid, OFMsgType.OFPT_FEATURES_REQUEST));

        Assert.assertTrue(tester.checkPacket(1, SenderType.SwitchesRegion,
                requestXid, OFMsgType.OFPT_FEATURES_REPLY));
        Assert.assertTrue(tester.absence());   // Assert that nothing is left
    }

    @Test
    public void twoPacketsSameXidsDifferentConnections() {
        FacadeTestHelper tester = new FacadeTestHelper();

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.SwitchesRegion));

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid10, SenderType.ReplicaRegion));

        Assert.assertTrue(tester.checkPacket(2, SenderType.ReplicaRegion, 54,
                OFMsgType.OFPT_BARRIER_REQUEST));

        Assert.assertTrue(tester.checkPacket(2, SenderType.SwitchesRegion, 54,
                OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertTrue(tester.checkPacket(1, SenderType.ReplicaRegion, 10,
                OFMsgType.OFPT_FEATURES_REQUEST));

        Assert.assertTrue(tester.checkPacket(1, SenderType.SwitchesRegion, 10,
                OFMsgType.OFPT_FEATURES_REPLY));
    }

    @Test
    public void twoPacketsDifferentXidsDifferentConnections() {
        FacadeTestHelper tester = new FacadeTestHelper();
        int barrierRequestXid = 53;
        int barrierReplyXid = 11;

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.SwitchesRegion));

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid53, SenderType.ReplicaRegion));

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        tester.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid11, SenderType.ReplicaRegion));

        Assert.assertTrue(tester.checkPacket(2, SenderType.ReplicaRegion, barrierRequestXid,
                OFMsgType.OFPT_BARRIER_REQUEST));

        Assert.assertTrue(tester.checkPacket(2, SenderType.SwitchesRegion, barrierRequestXid,
                OFMsgType.OFPT_BARRIER_REPLY));

        Assert.assertTrue(tester.checkPacket(1, SenderType.ReplicaRegion, barrierReplyXid,
                OFMsgType.OFPT_FEATURES_REQUEST));

        Assert.assertTrue(tester.checkPacket(1, SenderType.SwitchesRegion, barrierReplyXid,
                OFMsgType.OFPT_FEATURES_REPLY));
    }
}