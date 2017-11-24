package network_io.io_synchronizer;

import middleware.ProxyMiddleware;
import utils.events.SocketDataEventArg;

import java.util.Optional;

/**
 * Synchronizes Xid of the input packets, the output queue doesn't care
 * about the sender, it's the responsibility of the caller to decide whom
 * to send the output packets to
 */
public class SynchronizationFacade extends ProxyMiddleware {
    private XidSynchronizer xidSynchronizer;
    private ClonedControllerPacketSynchronizer delaySynchronizer;

    public SynchronizationFacade() {
        this.xidSynchronizer = new XidSynchronizer();
        this.delaySynchronizer = new ClonedControllerPacketSynchronizer();
    }

    private void addUnSynchronized(SocketDataEventArg arg) {
        this.delaySynchronizer.addUnSynchronized(arg);
    }

    private Optional<SocketDataEventArg> getSynced() {
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

    private void updateOutputQueue() {
        Optional<SocketDataEventArg> synced = this.getSynced();

        while (synced.isPresent()) {

            SocketDataEventArg dataEventArg = synced.get();
            this.output.add(dataEventArg);

            synced = this.getSynced();
        }
    }

    @Override
    public void execute() {
        while (!this.input.isEmpty()) {
            SocketDataEventArg packet = this.input.poll();
            this.addUnSynchronized(packet);
            this.updateOutputQueue();
        }
    }

}
