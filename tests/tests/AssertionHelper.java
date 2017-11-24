package tests;

import org.junit.jupiter.api.Assertions;
import tests.utils.ConnectionId;
import tests.utils.events.SocketDataEventArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AssertionHelper {

    /**
     * An packet with the specified ID is ready to be output
     *
     * @param packet draws packets from synced queue if possible
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    private static boolean getAndCheck(SocketDataEventArg packet, Checker checker) {

        List<Predicate<SocketDataEventArg>> checks = checker.getChecks();
        for (int i = 0; i < checks.size(); i++) {
            Predicate<SocketDataEventArg> check = checks.get(i);

            Assertions.assertTrue(check.test(packet), String.format("Failed to assert %d %s", i, packet));
        }

        return true;
    }

    /**
     * An packet with the specified ID is ready to be output
     *
     * @param id     Id of connection
     * @param packet packet from synced queue
     * @param xid    xid to match
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    public static boolean hasValidIdMessageTypeXid(int id, SocketDataEventArg packet,
                                                   int xid, Byte messageCode) {
        final ConnectionId connectionId = ConnectionId.CreateForTesting(id);
        final Checker checker = new Checker();


        checker.check(p -> p.getPacket().getXid() == xid);
        checker.check(p -> p.getPacket().getMessageCode() == messageCode);
        checker.check(p -> p.getId().equals(connectionId));

        try {
            getAndCheck(packet, checker);
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
     * @param packet draws packets from synced queue if possible
     * @return true if the next ready packet to output matches the ID, false otherwise
     */
    public static boolean hasValidIdMessageType(int id, SocketDataEventArg packet, byte messageCode) {
        final ConnectionId connectionId = ConnectionId.CreateForTesting(id);
        final Checker checker = new Checker();


        checker.check(p -> p.getPacket().getMessageCode() == messageCode);
        checker.check(p -> p.getId().equals(connectionId));

        try {
            getAndCheck(packet, checker);
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
