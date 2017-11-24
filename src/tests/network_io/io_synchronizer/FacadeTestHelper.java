package network_io.io_synchronizer;

import helpers.AssertionHelper;
import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.LinkedList;
import java.util.Optional;

public class FacadeTestHelper {
    LinkedList<SocketDataEventArg> toController;
    LinkedList<SocketDataEventArg> toSwitches;
    SynchronizationFacade facade;

    public FacadeTestHelper() {
        this.toController = new LinkedList<>();
        this.toSwitches = new LinkedList<>();
        this.facade = new SynchronizationFacade();
    }

    public void addUnSynchronized(SocketDataEventArg arg) {
        this.facade.addInput(arg);
    }

    public boolean absence(boolean ofOutput) {
        if (ofOutput) {
            return this.toSwitches.isEmpty();
        }
        return this.toController.isEmpty();
    }

    public boolean absence() {
        return absence(true) && absence(false);
    }

    private Optional<SocketDataEventArg> getToController() {
        return Optional.ofNullable(this.toController.poll());
    }

    private Optional<SocketDataEventArg> getToSwitches() {
        return Optional.ofNullable(this.toSwitches.poll());
    }

    public boolean checkPacket(int connId, SenderType sender, int xid, byte messageCode) {

        if (sender == SenderType.SwitchesRegion) {
            return AssertionHelper.hasValidIdMessageTypeXid(connId, this::getToController, xid, messageCode);
        }

        return AssertionHelper.hasValidIdMessageTypeXid(connId, this::getToSwitches, xid, messageCode);
    }
}
