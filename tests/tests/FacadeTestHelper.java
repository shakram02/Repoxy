package tests;

import network_io.io_synchronizer.SynchronizationFacade;
import utils.SenderType;
import utils.events.SocketDataEventArg;

import java.util.LinkedList;

import static tests.AssertionHelper.hasValidIdMessageTypeXid;

public class FacadeTestHelper {
    SynchronizationFacade facade;
    LinkedList<SocketDataEventArg> toController;
    LinkedList<SocketDataEventArg> toSwitches;

    public FacadeTestHelper() {
        this.facade = new SynchronizationFacade();
        toController = new LinkedList<>();
        toSwitches = new LinkedList<>();
    }

    public void addUnSynchronized(SocketDataEventArg arg) {
        this.facade.addInput(arg);
        this.facade.execute();
    }

    public boolean absence() {
        return this.facade.isEmpty();
    }

    private SocketDataEventArg getToController() {
        drawPacket();
        return this.toController.poll();
    }

    private SocketDataEventArg getToSwitches() {
        drawPacket();
        return this.toSwitches.poll();
    }


    private void drawPacket() {
        if (this.facade.isEmpty()) {
            return;
        }

        SocketDataEventArg packet = this.facade.getOutput();

        if (packet.getSenderType() == SenderType.SwitchesRegion) {
            this.toController.add(packet);
        } else {
            this.toSwitches.add(packet);
        }
    }

    public boolean checkPacket(int connId, SenderType sender, int xid, byte messageCode) {

        if (sender == SenderType.SwitchesRegion) {
            return hasValidIdMessageTypeXid(connId, this.getToController(), xid, messageCode);
        }

        return hasValidIdMessageTypeXid(connId, this.getToSwitches(), xid, messageCode);
    }
}
