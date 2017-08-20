package of_packets;

@SuppressWarnings("ALL")
public class OFMsgType {

    // Symmetric/Immutable messages;
    public static final Byte OFPT_HELLO = 0;
    public static final Byte OFPT_ERROR = 1;
    public static final Byte OFPT_ECHO_REQUEST = 2;
    public static final Byte OFPT_ECHO_REPLY = 3;
    public static final Byte OFPT_VENDOR = 4;

    // Switch configuration messages
    // Controller/Switch messages
    public static final Byte OFPT_FEATURES_REQUEST = 5;
    public static final Byte OFPT_FEATURES_REPLY = 6;
    public static final Byte OFPT_GET_CONFIG_REQUEST = 7;
    public static final Byte OFPT_GET_CONFIG_REPLY = 8;
    public static final Byte OFPT_SET_CONFIG = 9;

    // Async messages
    public static final Byte OFPT_PACKET_IN = 10;
    public static final Byte OFPT_FLOW_REMOVED = 11;
    public static final Byte OFPT_PORT_STATUS = 12;

    // Controller command messages
    // Controller/switch message
    public static final Byte OFPT_PACKET_OUT = 13;
    public static final Byte OFPT_FLOW_MOD = 14;
    public static final Byte OFPT_PORT_MOD = 15;

    // Statistics messages
    // Controller/Switch message
    public static final Byte OFPT_STATS_REQUEST = 16;
    public static final Byte OFPT_STATS_REPLY = 17;

    // Barrier messages
    // Controller/Switch message
    public static final Byte OFPT_BARRIER_REQUEST = 18;
    public static final Byte OFPT_BARRIER_REPLY = 19;

    // Queue Configuration messages
    // Controller/Switch message
    public static final Byte OFPT_QUEUE_GET_CONFIG_REQUEST = 20;
    public static final Byte OFPT_QUEUE_GET_CONFIG_REPLY = 21;
}
