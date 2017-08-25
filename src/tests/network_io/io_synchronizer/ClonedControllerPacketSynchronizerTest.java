package network_io.io_synchronizer;

import helpers.AssertionHelper;
import helpers.TestPacketArgMaker;
import helpers.TestPackets;
import of_packets.OFMsgType;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import utils.SenderType;

import static helpers.AssertionHelper.absence;
import static of_packets.OFMsgType.OFPT_BARRIER_REPLY;
import static of_packets.OFMsgType.OFPT_BARRIER_REQUEST;

class CloneIoBufferSynchronizerTest {
    @Test
    void testSyncSimple() {
        CloneIoBufferSynchronizer synchronizer = new CloneIoBufferSynchronizer();

        // Add the query
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REQUEST));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer,
                OFMsgType.OFPT_BARRIER_REPLY));


        // Nothing left
        Assert.assertTrue(absence(synchronizer));
    }

    @Test
    void testSyncReversedSimple() {
        CloneIoBufferSynchronizer synchronizer = new CloneIoBufferSynchronizer();

        // Add the reply
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        // Asserting false because replies shouldn't be released until their request was added
        Assert.assertTrue(absence(synchronizer));

        // Add the query
        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REPLY));

        // Nothing left
        Assert.assertTrue(absence(synchronizer));
    }

    @Test
    void testSyncTwoRequestOneReply() {
        CloneIoBufferSynchronizer synchronizer = new CloneIoBufferSynchronizer();

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

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REPLY));

        // Packet with ConnectionId 1 won't be released until its reply arrive, so now the output
        // queue to controller is empty
        Assert.assertTrue(absence(synchronizer));
    }

    @Test
    void testSwitchRequest() {
        CloneIoBufferSynchronizer synchronizer = new CloneIoBufferSynchronizer();

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.SwitchesRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REPLY));
    }

    @Test
    void testSwitchRequestReversed() {
        CloneIoBufferSynchronizer synchronizer = new CloneIoBufferSynchronizer();


        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.ControllerRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REPLY));
    }

    @Test
    void testDifferentConnectionIDs() {
        CloneIoBufferSynchronizer synchronizer = new CloneIoBufferSynchronizer();

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        // Request comes out
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REPLY));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(1, synchronizer, OFPT_BARRIER_REPLY));
        Assert.assertTrue(absence(synchronizer));
    }

    @Test
    void testSyncTwoRequestTwoReplies() {
        CloneIoBufferSynchronizer synchronizer = new CloneIoBufferSynchronizer();

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        // Now the replies are only remaining, they won't go out
        Assert.assertTrue(absence(synchronizer));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        // Request and reply come out
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REPLY));

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        // Request and reply come out
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REQUEST));
        Assert.assertTrue(AssertionHelper.hasValidIdMessageType(2, synchronizer, OFPT_BARRIER_REPLY));

        Assert.assertTrue(absence(synchronizer));
    }

    @Test
    void testSyncReplyWithNoQuery() {
        CloneIoBufferSynchronizer synchronizer = new CloneIoBufferSynchronizer();

        synchronizer.addUnSynchronized(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(absence(synchronizer));
    }
}