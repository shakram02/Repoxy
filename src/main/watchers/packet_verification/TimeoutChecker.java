package watchers.packet_verification;

import utils.Dumper;
import utils.StampedPacket;

import java.util.Date;

class TimeoutChecker {
    private final long threshold;
    private static final int ECHO_INTERVAL_CEILING = 5500;
    private static final int ECHO_INTERVAL_FLOOR = 4500;
    private long lastMainPacketTimestamp;
    private static final String FILE_NAME = new Date().toString() + "- DELAYS.txt";
    private Dumper<String> dumper;

    public TimeoutChecker(final long threshold) {
        this.threshold = threshold;
        this.lastMainPacketTimestamp = System.currentTimeMillis();
        this.dumper = new Dumper<>(String::getBytes);
    }

    public boolean isMainPacketTimedOut(final long mainPacketTimestamp) {
        long delay = Math.abs(lastMainPacketTimestamp - mainPacketTimestamp);
        lastMainPacketTimestamp = mainPacketTimestamp;

        return !isEchoDelay(delay) && delay > threshold;
    }

    public boolean hasTimedOut(final StampedPacket packet, final StampedPacket secondary) {
        long delay = Math.abs(packet.getTimestamp() - secondary.getTimestamp());
        String info = "Delay:" + delay;

        System.out.println(info);
        this.dumper.dump(info, FILE_NAME);

        String info = "Delay:" + delay;

        System.out.println(info);
        this.dumper.dump(info, FILE_NAME);

        return !isWildTimeout(delay) && delay > threshold;
    }

    private boolean isEchoDelay(long delay) {
        // The echo packet is sent every ~5 seconds, if this is the current
        // delay. ignore it, as the network delay will almost never generate a
        // 5 seconds delay
        System.out.println("Delay:" + delay);
        return delay >= ECHO_INTERVAL_FLOOR && delay <= ECHO_INTERVAL_CEILING;
    }
}
