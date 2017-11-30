package middleware.blocking;

import middleware.ProxyMiddleware;
import of_packets.OFPacket;
import utils.MonotonicClock;
import utils.events.SocketDataEventArg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Matches packets from {@link utils.SenderType#ControllerRegion} with packets
 * from {@link utils.SenderType#ReplicaRegion}.
 * <p>
 * If the packets match, it's added to the output buffer, otherwise it's added
 * to the error buffer.
 * <p>
 * If a packet times out, it's not known until an input comes in
 * <p>
 * Note that matched replicated packets are dropped
 */
public class PacketMatcher extends ProxyMiddleware {
    private static int DEFAULT_TIMEOUT_MILLIS = 20;
    private LinkedTransferQueue<SocketDataEventArg> waitingPackets;
    private final int millisThreshold;

    public PacketMatcher() {
        this(DEFAULT_TIMEOUT_MILLIS);
    }

    public PacketMatcher(int millisThreshold) {
        this.millisThreshold = millisThreshold;
        waitingPackets = new LinkedTransferQueue<>();
    }

    @Override
    public void execute() {
        while (!this.input.isEmpty()) {

            SocketDataEventArg packet = this.input.poll();
            Optional<SocketDataEventArg> match = hasQueuedMatch(packet);

            if (match.isPresent()) {
                this.output.add(packet);
            } else {
                // Add the packet to waiting Queue
                this.waitingPackets.add(packet);
            }
        }

        sweepUnmatched();
    }

    @Override
    public ProxyMiddleware clone() {
        return new PacketMatcher(this.millisThreshold);
    }

    /**
     * Checks if old packets exist in the intermediate queue
     * If the last matched packet has a timestamp greater than
     * a packet in the queue, then the queue packet is expired
     */
    private void sweepUnmatched() {
        if (this.waitingPackets.isEmpty()) {
            return;
        }

        // We only need to check the first packet, since it's
        // the oldest one in the queue
        SocketDataEventArg packet = this.waitingPackets.peek();

        long delta = MonotonicClock.getTimeMillis() - packet.getTimestamp();

        // Check if the packet is old enough
        if (delta < millisThreshold) {
            return;
        }

        // TODO: we should log a warning/error
        this.error.add(this.waitingPackets.poll());
    }

    public int countUnmatched() {
        return this.error.size();
    }

    /**
     * Scans the queue to find a matching packet
     *
     * @param incomingPacket Newly received packet
     * @return if a match exists, it's wrapped in an {@link Optional} otherwise an {@link Optional#EMPTY} is returned
     */
    private Optional<SocketDataEventArg> hasQueuedMatch(SocketDataEventArg incomingPacket) {
        Iterator<SocketDataEventArg> iter = waitingPackets.iterator();

        while (iter.hasNext()) {
            SocketDataEventArg packet = iter.next();

            if (areMatch(incomingPacket, packet)) {
                iter.remove();
                return Optional.of(packet);
            }
        }

        return Optional.empty();
    }

    private boolean areMatch(SocketDataEventArg first, SocketDataEventArg second) {
        OFPacket firstPacket = first.getPacket();
        OFPacket secondPacket = second.getPacket();

        return firstPacket.getMessageCode() == secondPacket.getMessageCode() &&
                Arrays.equals(firstPacket.getData(), secondPacket.getData());
    }
}
