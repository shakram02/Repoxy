package utils.logging;

import java.util.logging.Level;

public class NetworkLogLevels extends Level {
    public static final Level CONTROLLER = new NetworkLogLevels("CONTROLLER", 801);
    public static final Level SWITCH = new NetworkLogLevels("SWITCH", 802);
    public static final Level REPLICA = new NetworkLogLevels("REPLICA", 803);

    protected NetworkLogLevels(String name, int value) {
        super(name, value);
    }
}
