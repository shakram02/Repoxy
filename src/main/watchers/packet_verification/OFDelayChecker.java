package watchers.packet_verification;

import com.google.common.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.ImmutableControllerFailureArgs;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class OFDelayChecker implements SocketEventObserver {
    private static final int SCAN_INTERVAL = 10;
    private final Logger logger;
    private final EventBus mediatorNotifier;
    private final ConcurrentLinkedQueue<SocketDataEventArg> secondaryPackets;
    private final ConcurrentLinkedQueue<SocketDataEventArg> mainPackets;
    private final PacketMatcher packetMatcher;

    private TimeoutChecker timeoutChecker;
    private boolean willIgnoreComparison = false;

    public OFDelayChecker(int windowSize, SocketEventObserver mediator, int timestampThreshold) {
        this.logger = Logger.getLogger(OFDelayChecker.class.getName());

        mainPackets = new ConcurrentLinkedQueue<>();
        secondaryPackets = new ConcurrentLinkedQueue<>();

        this.mediatorNotifier = new EventBus(OFDelayChecker.class.getName());
        this.mediatorNotifier.register(mediator);
        this.timeoutChecker = new TimeoutChecker(timestampThreshold);

        this.packetMatcher = new PacketMatcher();

        Timer expirationAlarm = new Timer(true);
        expirationAlarm.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                OFDelayChecker.this.scanPackets();
            }
        }, 0, SCAN_INTERVAL);
    }

    @Override
    public void dispatchEvent(@NotNull final SocketEventArguments arg) {

        if (!(arg instanceof SocketDataEventArg) || arg.getSenderType() == SenderType.SwitchesRegion) {
            return;
        }

        SocketDataEventArg dataEventArg = (SocketDataEventArg) arg;
        this.addToAppropriateQueue(dataEventArg);
    }

    private void scanPackets() {
//        int unmatchedPackets = 0;

        for (Iterator<SocketDataEventArg> mainIterator = mainPackets.iterator(); mainIterator.hasNext(); ) {
            SocketDataEventArg mainPacket = mainIterator.next();
            boolean matched = false;

            for (Iterator<SocketDataEventArg> secondaryIterator = secondaryPackets.iterator();
                 secondaryIterator.hasNext(); ) {
                SocketDataEventArg secondaryPacket = secondaryIterator.next();

                if (!this.packetMatcher.match(mainPacket, secondaryPacket)) {
//                    unmatchedPackets++;
                    continue;
                }

                matched = true;
                if (timeoutChecker.hasTimedOut(mainPacket, secondaryPacket)) {
                    System.out.println("Timeout");
                    this.onExpiry(mainPacket, secondaryPacket);
                } else {
                    System.out.println("No timeout");
                }
                secondaryIterator.remove();
            }

            if (matched) {
                mainIterator.remove();
            }
        }

//        if (unmatchedPackets >= (this.windowSize / 2)) {
//            logger.warning("Maximum number of unmatched packets exceeded:" + unmatchedPackets);
//            switchController();
//        }
    }

    private void addToAppropriateQueue(SocketDataEventArg arg) {
        if (arg.getSenderType() == SenderType.ControllerRegion) {
            this.mainPackets.add(arg);
        } else {
            this.secondaryPackets.add(arg);
        }
    }

    private void switchController() {
        // Alert!
        // TODO: update check timestamp with timeout class
        // TODO: clear packet queues as appropriate
        // TODO: migrate left-over packets
        this.willIgnoreComparison = true;
        mediatorNotifier.post(ImmutableControllerFailureArgs.builder().build());
    }

    private void onExpiry(SocketDataEventArg mainPacket, SocketDataEventArg secondaryPacket) {
        if (this.willIgnoreComparison) {
            logger.info("Comparison ignored");
            return;
        }

        logger.warning("Delay expired:" +
                Math.abs(mainPacket.getTimestamp() - secondaryPacket.getTimestamp()));

        this.switchController();
    }
}
