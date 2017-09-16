package watchers.packet_verification;

import of_packets.OFPacket;
import utils.StampedPacket;

import java.util.Arrays;
import java.util.Iterator;

class TimeoutChecker {
    private final long threshold;

    public TimeoutChecker(final long threshold) {
        this.threshold = threshold;
    }

    public boolean isTimedOut(final StampedPacket packet) {
        long now = System.currentTimeMillis();
        return (packet.getTimestamp() - now) > threshold;
    }
}
