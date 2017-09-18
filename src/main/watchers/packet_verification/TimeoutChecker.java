package watchers.packet_verification;

import utils.StampedPacket;

class TimeoutChecker {
    private final long threshold;
    private boolean isNew;
    private static final int ARBITRARY_UNKNOWN_DELAY = 4000;    // TODO: some random 5sec delay happens once, idk

    public TimeoutChecker(final long threshold) {
        this.threshold = threshold;
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
