package watchers.packet_verification;

import of_packets.OFPacket;
import utils.StampedPacket;

class TimeoutChecker {
    private final long threshold;
    private boolean isNew;
    private static final int ARBITRARY_UNKNOWN_DELAY = 4000;    // TODO: some random 5sec delay happens once, idk
    private long lastMainPacketTimestamp;

    public TimeoutChecker(final long threshold) {
        this.threshold = threshold;
        this.lastMainPacketTimestamp = System.currentTimeMillis();
    }

    public boolean isMainPacketTimedOut(final long mainPacketTimestamp) {
        boolean timedOut = Math.abs(lastMainPacketTimestamp - mainPacketTimestamp) > threshold;
        lastMainPacketTimestamp = mainPacketTimestamp;

        return timedOut;
    }

    public boolean hasTimedOut(final StampedPacket packet, final StampedPacket secondary) {

        long delay = Math.abs(packet.getTimestamp() - secondary.getTimestamp());
        System.out.println("Delay:" + delay);

        if (isNew && (delay > ARBITRARY_UNKNOWN_DELAY)) {
            isNew = false;
            return false;
        }

        return delay > threshold;
    }
}
