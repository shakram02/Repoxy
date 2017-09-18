package watchers.packet_verification;

import utils.StampedPacket;

class TimeoutChecker {
    private final long threshold;

    public TimeoutChecker(final long threshold) {
        this.threshold = threshold;
    }

    public boolean hasTimedOut(final StampedPacket packet, final StampedPacket secondary) {
        long delay = Math.abs(packet.getTimestamp() - secondary.getTimestamp());
        System.out.println("Delay:" + delay);

        return delay > threshold;
    }
}
