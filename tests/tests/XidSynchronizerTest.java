package tests;

import network_io.io_synchronizer.XidSynchronizer;
import org.junit.Assert;
import org.junit.Test;
import of_packets.OFPacket;
import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.Optional;

public class XidSynchronizerTest {
    @Test
    public void syncOneXid() {
        XidSynchronizer synchronizer = new XidSynchronizer();
        int requestXid = 11;
        SocketDataEventArg dataEventArgs = TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid11, SenderType.ReplicaRegion);

        synchronizer.saveCopyIfQuery(dataEventArgs);

        Optional<SocketDataEventArg> xidSyncResult = synchronizer.syncIfReply(TestPacketArgMaker
                .createFromPacket(1,
                        TestPackets.FeaturesReplyXid10, SenderType.ReplicaRegion));

        Assert.assertTrue(xidSyncResult.isPresent());

        SocketDataEventArg syncedArg = xidSyncResult.get();

        Assert.assertTrue(syncedArg.getPacket().getXid() == requestXid);
    }

    @Test
    public void syncOneXidFalseConnectionID() {
        final XidSynchronizer synchronizer = new XidSynchronizer();
        int connectionId = 1;
        int falseConnectionId = 2;

        SocketDataEventArg dataEventArgs = TestPacketArgMaker.createFromPacket(connectionId,
                TestPackets.FeaturesRequestXid11, SenderType.ReplicaRegion);

        synchronizer.saveCopyIfQuery(dataEventArgs);
        SocketDataEventArg arg = TestPacketArgMaker.createFromPacket(falseConnectionId,
                TestPackets.FeaturesReplyXid10, SenderType.ReplicaRegion);

        Assert.assertFalse(synchronizer.syncIfReply(arg).isPresent());
    }

    @Test
    public void syncSwitchQuery() {
        final XidSynchronizer synchronizer = new XidSynchronizer();

        synchronizer.saveCopyIfQuery(TestPacketArgMaker.createFromPacket(1,
                TestPackets.FeaturesRequestXid11, SenderType.SwitchesRegion));

        Optional<SocketDataEventArg> syncResult = synchronizer.syncIfReply(
                TestPacketArgMaker.createFromPacket(1,
                        TestPackets.FeaturesReplyXid10, SenderType.ReplicaRegion));

        Assert.assertTrue(syncResult.isPresent());
        OFPacket packet = syncResult.get().getPacket();

        Assert.assertTrue(packet.getXid() == 11);
    }
}