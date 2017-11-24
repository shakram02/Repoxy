package middleware.packet_verification;

import com.google.common.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.ImmutableControllerFailureArgs;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class OFDelayChecker implements SocketEventObserver {
    private static final int SCAN_INTERVAL = 5;
    private final Logger logger;
    private final EventBus mediatorNotifier;
    private final PacketMatcher packetMatcher;
//    private final int windowSize;

    private TimeoutChecker timeoutChecker;
    private final Runnable scanTask = this::scanForExpiry;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
    private ScheduledFuture<?> lastTask;

    public OFDelayChecker(int windowSize, SocketEventObserver mediator, int timestampThresholdMillis) {
        this.logger = Logger.getLogger(OFDelayChecker.class.getName());
        // A window mechanism won't work as the timeout of the packets is very small and
        // switches automatically disconnect, so basically we want to scan the expiry as
        // fast as possible.
        // We scan a packet timeouts every interval to remove the expired packets
        //        this.windowSize = windowSize;

        this.mediatorNotifier = new EventBus(OFDelayChecker.class.getName());
        this.mediatorNotifier.register(mediator);
        this.timeoutChecker = new TimeoutChecker(timestampThresholdMillis * 100);

        this.packetMatcher = new PacketMatcher(this::onPacketMatch);
    }

    @Override
    public void dispatchEvent(@NotNull final SocketEventArguments arg) {

        if (!(arg instanceof SocketDataEventArg) || arg.getSenderType() == SenderType.SwitchesRegion) {
            return;
        }

        this.scanForUnmatched();
        this.schedulePacketScan();

        SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
        this.packetMatcher.addPacket(dataEventArg);

        // TODO: check for unmatched packets
        this.logger.info("Unmatched:" + this.packetMatcher.countUnmatched());
    }

    /**
     * This method is run whenever a new packet comes in, it handles the case when a controller
     * might be attacked and is deviating from the other controller
     */
    private void scanForUnmatched() {
//        int unmatchedPackets = this.packetMatcher.countUnmatched();
        // TODO: packets should be count as unmatched after their time has expired! lol
//        if (unmatchedPackets >= (this.windowSize / 2)) {
//            logger.warning("Maximum number of unmatched packets exceeded:" + unmatchedPackets);
//            switchController();
//        }
    }

    /**
     * This method is run periodically to check for the packets that are about to timeout
     * It handles the case when the network isn't enough active to detect timed out packets
     * <p>
     * Packet delay is so sensitive because the switches will disconnect if a packet times out
     * thus we should switch the controller once a single packet is about to timeout
     */
    private void scanForExpiry() {
        List<Iterator<SocketDataEventArg>> iterators = this.packetMatcher.unmatchedIterators();
        // TODO: needs testing
        // Find the expired packets in all iterators for all connection IDs
        for (Iterator<SocketDataEventArg> iterator : iterators) {
            while (iterator.hasNext()) {
                SocketDataEventArg arg = iterator.next();
                if (!this.timeoutChecker.hasTimedOut(arg)) {
                    continue;
                }

                iterator.remove();
                this.logger.warning(String.format("Switching controller; A packet expired:\n%s", arg.toString()));
            }
        }
    }

    private void onPacketMatch(SocketDataEventArg main, SocketDataEventArg secondary) {
        if (!this.timeoutChecker.hasTimedOut(main, secondary)) {
            return;
        }

        System.out.println("Timeout");
        // TODO: switch the controller here after threshold
    }

    private void switchController() {
        // Alert!
        // TODO: update check timestamp with timeout class
        // TODO: clear packet queues as appropriate
        // TODO: migrate left-over packets
        // this.willIgnoreComparison = true;
        mediatorNotifier.post(ImmutableControllerFailureArgs.builder().build());
    }

    private void schedulePacketScan() {
        // Don't overlap tasks
        if (lastTask != null && !lastTask.isDone()) {
            return;
        }

        lastTask = scheduler.schedule(scanTask, SCAN_INTERVAL, TimeUnit.MILLISECONDS);
    }

    class DaemonThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(@NotNull Runnable runnable) {
            Thread t = new Thread();
            t.setDaemon(true);  // JVM can kill this thread when application exists
            return t;
        }
    }
}
