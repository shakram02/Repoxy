package watchers.packet_verification;

import com.google.common.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.ImmutableControllerFailureArgs;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OFDelayChecker implements SocketEventObserver {
    private static final int SCAN_INTERVAL = 10;
    private final Logger logger;
    private final EventBus mediatorNotifier;
    private final PacketMatcher packetMatcher;
    private final int windowSize;

    private TimeoutChecker timeoutChecker;


    public OFDelayChecker(int windowSize, SocketEventObserver mediator, int timestampThreshold) {
        this.windowSize = windowSize;
        this.logger = Logger.getLogger(OFDelayChecker.class.getName());

        this.mediatorNotifier = new EventBus(OFDelayChecker.class.getName());
        this.mediatorNotifier.register(mediator);
        this.timeoutChecker = new TimeoutChecker(timestampThreshold);

        this.packetMatcher = new PacketMatcher(this::onPacketsMatch);

        Timer expirationAlarm = new Timer(true);
        expirationAlarm.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                OFDelayChecker.this.scanPackets();
            }
        }, 0, SCAN_INTERVAL);
    }

    private void onPacketsMatch(SocketDataEventArg main, SocketDataEventArg secondary) {
        if (!this.timeoutChecker.hasTimedOut(main, secondary)) {
            return;
        }
        System.out.println("Timeout");
    }

    @Override
    public void dispatchEvent(@NotNull final SocketEventArguments arg) {

        if (!(arg instanceof SocketDataEventArg) || arg.getSenderType() == SenderType.SwitchesRegion) {
            return;
        }

        SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
        this.packetMatcher.addPacket(dataEventArg);
    }

    private void scanPackets() {
        int unmatchedPackets = this.packetMatcher.countUnmatched();

        if (unmatchedPackets >= (this.windowSize / 2)) {
            logger.warning("Maximum number of unmatched packets exceeded:" + unmatchedPackets);
            switchController();
        }
    }

    private void switchController() {
        // Alert!
        // TODO: update check timestamp with timeout class
        // TODO: clear packet queues as appropriate
        // TODO: migrate left-over packets
//        this.willIgnoreComparison = true;
        mediatorNotifier.post(ImmutableControllerFailureArgs.builder().build());
    }
}
