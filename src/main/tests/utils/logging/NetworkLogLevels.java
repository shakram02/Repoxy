package utils.logging;

import utils.SenderType;
import utils.events.SocketEventArguments;

import java.util.logging.Level;

public class NetworkLogLevels extends Level {
    public static final NetworkLogLevels CONTROLLER = new NetworkLogLevels("CONTROLLER", 801);
    public static final NetworkLogLevels SWITCH = new NetworkLogLevels("SWITCH", 802);
    public static final NetworkLogLevels REPLICA = new NetworkLogLevels("REPLICA", 803);
    public static final NetworkLogLevels DIFFER = new NetworkLogLevels("DIFFER", 804);

    protected NetworkLogLevels(String name, int value) {
        super(name, value);
    }

    public static Level getLevel(SenderType sender) {
        if (sender == SenderType.ControllerRegion) {
            return NetworkLogLevels.CONTROLLER;
        }
        if (sender == SenderType.ReplicaRegion) {
            return NetworkLogLevels.REPLICA;
        }
        if (sender == SenderType.SwitchesRegion) {
            return NetworkLogLevels.SWITCH;
        }
        return Level.INFO;
    }

    public static Level getLevel(SocketEventArguments arg) {
        SenderType sender = arg.getSenderType();
        return getLevel(sender);
    }
}
