package network_io.io_synchronizer;

import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Provides an interface for synchronization mechanism and hides implementation details
 */
public class SynchronizationFacade {
    private XidSynchronizer xidSynchronizer;
    private ClonedControllerPacketSynchronizer delaySynchronizer;

    private Consumer<SocketDataEventArg> sendToController;
    private Consumer<SocketDataEventArg> sendToSwitches;

    public SynchronizationFacade(Consumer<SocketDataEventArg> sendToSwitches,
                                 Consumer<SocketDataEventArg> sendToController) {
        this.xidSynchronizer = new XidSynchronizer();
        this.delaySynchronizer = new ClonedControllerPacketSynchronizer();
        this.sendToSwitches = sendToSwitches;
        this.sendToController = sendToController;
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

    private void updateIoQueues() {
        Optional<SocketDataEventArg> synced = this.getSynced();

        while (synced.isPresent()) {
            SocketDataEventArg dataEventArg = synced.get();
            if (dataEventArg.getSenderType() == SenderType.SwitchesRegion) {
                this.sendToController.accept(dataEventArg);
            } else {
                // We disregard the type of the controller as it might change over time
                this.sendToSwitches.accept(dataEventArg);
            }

            synced = this.getSynced();
        }
    }

    public void manageInput(SocketDataEventArg incoming) {
        this.manageIo(incoming);
    }

    public void manageOutput(SocketDataEventArg incoming) {
        this.manageIo(incoming);
    }

    private void manageIo(SocketDataEventArg incoming) {
        this.addUnSynchronized(incoming);
        this.updateIoQueues();
    }
}
