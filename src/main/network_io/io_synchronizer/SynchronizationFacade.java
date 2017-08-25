package network_io.io_synchronizer;

import utils.events.SocketDataEventArg;

import java.util.Optional;

/**
 * Provides an interface for synchronization mechanism and hides implementation details
 */
public class SynchronizationFacade implements Synchronizer {
    private XidSynchronizer xidSynchronizer;
    private ClonedControllerPacketSynchronizer delaySynchronizer;

    public SynchronizationFacade() {
        this.xidSynchronizer = new XidSynchronizer();
        this.delaySynchronizer = new ClonedControllerPacketSynchronizer();
    }

    public void addUnSynchronized(SocketDataEventArg arg) {
        this.delaySynchronizer.addUnSynchronized(arg);
    }

    public Optional<SocketDataEventArg> getSynced() {
        // Get delay synchronized packet
        Optional<SocketDataEventArg> delaySynced = this.delaySynchronizer.getSynced();

        if (!delaySynced.isPresent()) {
            return Optional.empty();
        }

        SocketDataEventArg timeSyncedEventArg = delaySynced.get();

        // Either store or modify will occur. But this class assumes synchronized
        // packets. That's why it need to be here
        this.xidSynchronizer.saveCopyIfQuery(timeSyncedEventArg);

        // Modify Xid if needed
        Optional<SocketDataEventArg> fullySynced = this.xidSynchronizer.syncIfReply(timeSyncedEventArg);
        return Optional.of(fullySynced.orElse(timeSyncedEventArg));

    }
}
