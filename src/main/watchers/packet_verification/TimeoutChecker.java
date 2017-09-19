package watchers.packet_verification;

import utils.Dumper;
import utils.StampedPacket;

class TimeoutChecker {
    private final long threshold;
    private boolean isNew;
    private static final int ARBITRARY_UNKNOWN_DELAY = 4000;    // TODO: some random 5sec delay happens once, idk
    private long lastMainPacketTimestamp;
    private final String FILE_NAME = "DELAYS.txt";
    private Dumper<String> dumper;

    public TimeoutChecker(final long threshold) {
        this.threshold = threshold;
        this.lastMainPacketTimestamp = System.currentTimeMillis();
        this.dumper = new Dumper<>(String::getBytes);
    }

    public boolean isMainPacketTimedOut(final long mainPacketTimestamp) {
        boolean timedOut = Math.abs(lastMainPacketTimestamp - mainPacketTimestamp) > threshold;
        lastMainPacketTimestamp = mainPacketTimestamp;

        return timedOut;
    }

    public boolean hasTimedOut(final StampedPacket packet, final StampedPacket secondary) {

        long delay = Math.abs(packet.getTimestamp() - secondary.getTimestamp());
        String info = "Delay:" + delay;

        System.out.println(info);
        this.dumper.dump(info, FILE_NAME);

        if (isNew && (delay > ARBITRARY_UNKNOWN_DELAY)) {
            isNew = false;
            return false;
        }

        return delay > threshold;
    }
}
