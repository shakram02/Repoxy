package network_io.io_synchronizer;

import middleware.ProxyMiddleware;
import utils.ConnectionId;
import utils.events.SocketDataEventArg;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Provides an interface for synchronization mechanism and hides implementation details
 */
public class SynchronizationFacade extends ProxyMiddleware {
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

    private void updateOutputQueue() {
        Optional<SocketDataEventArg> synced = this.getSynced();

        while (synced.isPresent()) {

            SocketDataEventArg dataEventArg = synced.get();
            this.output.add(dataEventArg);

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
        this.updateOutputQueue();
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
