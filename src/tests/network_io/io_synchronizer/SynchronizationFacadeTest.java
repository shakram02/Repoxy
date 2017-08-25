package network_io.io_synchronizer;

import helpers.AssertionHelper;
import helpers.TestPacketArgMaker;
import helpers.TestPackets;
import of_packets.OFMsgType;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import utils.SenderType;

import static helpers.AssertionHelper.absence;
import static helpers.AssertionHelper.hasValidIdMessageTypeXid;

class SynchronizationFacadeTest {
    @Test
    void simpleOnePacket() {
        SynchronizationFacade synchronizer = new SynchronizationFacade();
        int requestXid = 54;

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageTypeXid(1, synchronizer,
                requestXid, OFMsgType.OFPT_BARRIER_REQUEST));

        synchronizer.addUnSynchronized(TestPacketArgMaker
                .createFromPacket(1,
                        TestPackets.BarrierReplyXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(hasValidIdMessageTypeXid(1, synchronizer,
                requestXid, OFMsgType.OFPT_BARRIER_REPLY));
        Assert.assertTrue(absence(synchronizer));   // Assert that nothing is left
    }

    @Test
    void simpleOnePacketReversedDifferentXIDs() {
        SynchronizationFacade synchronizer = new SynchronizationFacade();
        int requestXid = 11;

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.ReplicaRegion));

        Assert.assertTrue(absence(synchronizer));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid11, SenderType.ReplicaRegion));

        // Request and reply come out with the xid of the request
        Assert.assertTrue(hasValidIdMessageTypeXid(1, synchronizer, requestXid, OFMsgType.OFPT_FEATURES_REQUEST));
        Assert.assertTrue(hasValidIdMessageTypeXid(1, synchronizer, requestXid, OFMsgType.OFPT_FEATURES_REPLY));
        Assert.assertTrue(absence(synchronizer));   // Assert that nothing is left
    }

    @Test
    void simpleOnePacketReversedSameXIDs() {
        SynchronizationFacade synchronizer = new SynchronizationFacade();
        int requestXid = 10;
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesReplyXid10, SenderType.ReplicaRegion));

        Assert.assertTrue(absence(synchronizer));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid10, SenderType.ReplicaRegion));

        // Request and reply come out with the xid of the request
        Assert.assertTrue(hasValidIdMessageTypeXid(1, synchronizer, requestXid, OFMsgType.OFPT_FEATURES_REQUEST));
        Assert.assertTrue(hasValidIdMessageTypeXid(1, synchronizer, requestXid, OFMsgType.OFPT_FEATURES_REPLY));
        Assert.assertTrue(absence(synchronizer));   // Assert that nothing is left
    }
}