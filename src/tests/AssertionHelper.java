package helpers;

import org.junit.jupiter.api.Assertions;
import utils.ConnectionId;
import utils.events.SocketDataEventArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AssertionHelper {

    /**
     * An packet with the specified ID is ready to be output
     *
     * @param getter draws packets from synced queue if possible
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    private static boolean getAndCheck(Supplier<Optional<SocketDataEventArg>> getter, Checker checker) {

        Optional<SocketDataEventArg> syncResult = getter.get();
        Assertions.assertTrue(syncResult.isPresent());

        SocketDataEventArg eventArg = syncResult.get();

        List<Predicate<SocketDataEventArg>> checks = checker.getChecks();
        for (int i = 0; i < checks.size(); i++) {
            Predicate<SocketDataEventArg> check = checks.get(i);

            Assertions.assertTrue(check.test(eventArg), String.format("Failed to assert %d %s", i, eventArg));
        }

        return true;
    }

    /**
     * An packet with the specified ID is ready to be output
     *
     * @param id     Id of connection
     * @param getter draws packets from synced queue if possible
     * @param xid    xid to match
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    public static boolean hasValidIdMessageTypeXid(int id, Supplier<Optional<SocketDataEventArg>> getter,
                                                   int xid, Byte messageCode) {
        final ConnectionId connectionId = ConnectionId.CreateForTesting(id);
        final Checker checker = new Checker();


        checker.check(p -> p.getPacket().getXid() == xid);
        checker.check(p -> p.getPacket().getMessageCode() == messageCode);
        checker.check(p -> p.getId().equals(connectionId));

        try {
            getAndCheck(getter, checker);
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
     * @param id     Id of connection
     * @param getter draws packets from synced queue if possible
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    public static boolean hasValidIdMessageType(int id, Supplier<Optional<SocketDataEventArg>> getter, byte messageCode) {
        final ConnectionId connectionId = ConnectionId.CreateForTesting(id);
        final Checker checker = new Checker();


        checker.check(p -> p.getPacket().getMessageCode() == messageCode);
        checker.check(p -> p.getId().equals(connectionId));

        try {
            getAndCheck(getter, checker);
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
     * @param getter draws packets from synced queue if possible
     * @return true if nothing is ready to be output, false otherwise
     */
    public static boolean absence(Supplier<Optional<SocketDataEventArg>> getter) {
        Optional<SocketDataEventArg> barrierReply = getter.get();
        try {
            Assertions.assertFalse(barrierReply.isPresent());
        } catch (AssertionError e) {
            return false;
        }

        return true;
    }

    public static class Checker {
        private ArrayList<Predicate<SocketDataEventArg>> checks = new ArrayList<>();

        public void check(Predicate<SocketDataEventArg> check) {
            checks.add(check);
        }

        public ArrayList<Predicate<SocketDataEventArg>> getChecks() {
            return checks;
        }
    }
}
