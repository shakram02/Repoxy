package middleware.packet_verification;

import utils.Dumper;
import utils.events.SocketDataEventArg;

import java.util.Date;

class TimeoutChecker {
    private static final String FILE_NAME = new Date().toString() + "- DELAYS.txt";
    private final long thresholdNanoSeconds;
    private Dumper<String> dumper;
    private final long base = System.nanoTime();

    public TimeoutChecker(final long thresholdNanoSeconds) {
        this.thresholdNanoSeconds = thresholdNanoSeconds;
        this.dumper = new Dumper<>(String::getBytes);
    }

    public boolean hasTimedOut(final SocketDataEventArg packet, final SocketDataEventArg secondary) {
        long delay = Math.abs(packet.getTimestamp() - secondary.getTimestamp());
        String info = (System.nanoTime() - base) + " " + delay + "\n";

        // TODO prints the delays
//        System.out.print(info);
        this.dumper.dump(info, FILE_NAME);

        return delay > thresholdNanoSeconds;
    }

    public boolean hasTimedOut(final SocketDataEventArg packet) {
        long delay = Math.abs(packet.getTimestamp() - System.nanoTime());

        String info = System.nanoTime() + " " + delay + "\n";
        System.out.print(info);

        return delay > thresholdNanoSeconds;
    }
}
