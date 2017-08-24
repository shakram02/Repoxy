package network_io.io_synchronizer;

import helpers.TestPacketArgMaker;
import helpers.TestPackets;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import utils.ConnectionId;
import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.Optional;

class ClonedControllerPacketSynchronizerTest {
    @Test
    void testSyncSimple() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        // Add the query
        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(absence(synchronizer));

        // Add the reply
        SocketDataEventArg barrierReply54 = TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion);
        synchronizer.insertUnSynced(barrierReply54);

        Assert.assertTrue(presence(1, synchronizer));
    }

    @Test
    void testSyncReversedSimple() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();
        SocketDataEventArg barrierReplyDataArg = TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion);
        // Add the reply
        synchronizer.insertUnSynced(barrierReplyDataArg);

        // Asserting false because replies shouldn't be released until their request was added
        Assert.assertTrue(absence(synchronizer));

        // Add the query
        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(presence(1, synchronizer));
    }

    @Test
    void testSyncTwoRequestOneReply() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        // Add the query
        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        // Add the query
        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(presence(2, synchronizer));

        // Packet with ConnectionId 1 won't be released until its reply arrive, so now the output
        // queue to controller is empty
        Assert.assertTrue(absence(synchronizer));
    }

    @Test
    void testSyncTwoRequestTwoReplies() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        // Add the query
        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        // Nothing is ready to output
        Assert.assertTrue(absence(synchronizer));

        // Add the query
        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierRequestXid54, SenderType.ReplicaRegion));

        Assert.assertTrue(presence(2, synchronizer));


        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(1,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(presence(1, synchronizer));
    }

    @Test
    void testSyncReplyWithNoQuery() {
        ClonedControllerPacketSynchronizer synchronizer = new ClonedControllerPacketSynchronizer();

        synchronizer.insertUnSynced(TestPacketArgMaker.createFromPacket(2,
                TestPackets.BarrierReplyXid54, SenderType.SwitchesRegion));

        Assert.assertTrue(absence(synchronizer));
    }


    /**
     * An packet with the specified ID is ready to be output
     *
     * @param id           Id of connection
     * @param synchronizer packet synchronizer
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    boolean presence(int id, ClonedControllerPacketSynchronizer synchronizer) {
        ConnectionId connectionId = ConnectionId.CreateForTesting(id);
        Optional<SocketDataEventArg> barrierReply = synchronizer.getSynced();

        try {
            Assert.assertTrue(barrierReply.isPresent() &&
                    barrierReply.get().getId().equals(connectionId));
        } catch (AssertionError e) {
            return false;
        }
        return true;
    }

    /**
     * Nothing is ready to output
     *
     * @param synchronizer packet synchronizer
     * @return true if nothing is ready to be output, false otherwise
     */
    boolean absence(ClonedControllerPacketSynchronizer synchronizer) {
        Optional<SocketDataEventArg> barrierReply = synchronizer.getSynced();
        try {
            Assert.assertFalse(barrierReply.isPresent());
        } catch (AssertionError e) {
            return false;
        }

        return true;
    }
}