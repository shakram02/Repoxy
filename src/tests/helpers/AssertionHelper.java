package helpers;

import network_io.io_synchronizer.Synchronizer;
import org.junit.Assert;
import utils.ConnectionId;
import utils.events.SocketDataEventArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AssertionHelper {

    /**
     * An packet with the specified ID is ready to be output
     *
     * @param synchronizer packet synchronizer
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    private static boolean getAndCheck(Synchronizer synchronizer, Checker checker) {

        Optional<SocketDataEventArg> syncResult = synchronizer.getSynced();
        Assert.assertTrue(syncResult.isPresent());

        SocketDataEventArg eventArg = syncResult.get();

        List<Predicate<SocketDataEventArg>> checks = checker.getChecks();
        for (int i = 0; i < checks.size(); i++) {
            Predicate<SocketDataEventArg> check = checks.get(i);

            Assert.assertTrue(String.format("Failed to assert %d %s", i, eventArg)
                    , check.test(eventArg));
        }

        return true;
    }

    /**
     * An packet with the specified ID is ready to be output
     *
     * @param id           Id of connection
     * @param synchronizer packet synchronizer
     * @param xid          xid to match
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    public static boolean hasValidIdMessageTypeXid(int id, Synchronizer synchronizer, int xid, Byte messageCode) {
        final ConnectionId connectionId = ConnectionId.CreateForTesting(id);
        final Checker checker = new Checker();


        checker.addCheck(p -> p.getPacket().getXid() == xid);
        checker.addCheck(p -> p.getPacket().getMessageCode() == messageCode);
        checker.addCheck(p -> p.getId().equals(connectionId));

        try {
            getAndCheck(synchronizer, checker);
        } catch (AssertionError e) {
            System.err.println(String.format("Validating: messageCode: %d, XId: %d, ConnId: %d",
                    messageCode, xid, id));
            throw e;
        }
        return true;
    }

    /**
     * An packet with the specified ID is ready to be output
     *
     * @param id           Id of connection
     * @param synchronizer packet synchronizer
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    public static boolean hasValidIdMessageType(int id, Synchronizer synchronizer, byte messageCode) {
        final ConnectionId connectionId = ConnectionId.CreateForTesting(id);
        final Checker checker = new Checker();


        checker.addCheck(p -> p.getPacket().getMessageCode() == messageCode);
        checker.addCheck(p -> p.getId().equals(connectionId));

        try {
            getAndCheck(synchronizer, checker);
        } catch (AssertionError e) {
            System.err.println(String.format("Validating: messageCode: %d, ConnId: %d",
                    messageCode, id));
            throw e;
        }
        return true;
    }

    /**
     * Nothing is ready to output
     *
     * @param synchronizer packet synchronizer
     * @return true if nothing is ready to be output, false otherwise
     */
    public static boolean absence(Synchronizer synchronizer) {
        Optional<SocketDataEventArg> barrierReply = synchronizer.getSynced();
        try {
            Assert.assertFalse(barrierReply.isPresent());
        } catch (AssertionError e) {
            return false;
        }

        return true;
    }

    public static class Checker {
        private ArrayList<Predicate<SocketDataEventArg>> checks = new ArrayList<>();

        public void addCheck(Predicate<SocketDataEventArg> check) {
            checks.add(check);
        }

        public ArrayList<Predicate<SocketDataEventArg>> getChecks() {
            return checks;
        }
    }
}
