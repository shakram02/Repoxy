package watchers.packet_verification;

import utils.Dumper;
import utils.events.SocketDataEventArg;

import java.util.Date;

class TimeoutChecker {
    private static final String FILE_NAME = new Date().toString() + "- DELAYS.txt";
    private final long threshold;
    private Dumper<String> dumper;

    public TimeoutChecker(final long threshold) {
        this.threshold = threshold;
        this.dumper = new Dumper<>(String::getBytes);
    }

    public boolean hasTimedOut(final SocketDataEventArg packet, final SocketDataEventArg secondary) {
        long delay = Math.abs(packet.getTimestamp() - secondary.getTimestamp());
        String info = "Delay:" + delay;

        System.out.println(info);
        this.dumper.dump(info, FILE_NAME);

        return delay > threshold;
    }
}
