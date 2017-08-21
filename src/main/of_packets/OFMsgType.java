package of_packets;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

@SuppressWarnings("ALL")
public class OFMsgType {
    private static final LinkedHashSet<Byte> QUERY_SET;
    private static final LinkedHashSet<Byte> REPLY_SET;
    private static final BiMap<Byte, Byte> MESSAGE_PAIRS;

    public static boolean isQuery(Byte msgCode) {
        return QUERY_SET.contains(msgCode);
    }

    public static boolean isReply(Byte msgCode) {
        return REPLY_SET.contains(msgCode);
    }

    /**
     * Calculates the opposite of a given Request-Reply packet
     *
     * @param msgCode code of the request/reply
     * @return Code of the opposite message
     */
    @NotNull
    public static Byte getOppositeMessage(Byte msgCode) {
        if (!QUERY_SET.contains(msgCode) && !REPLY_SET.contains(msgCode)) {
            throw new IllegalStateException(String.format("Not a request, nor a reply [%s]", msgCode));
        }

        // Perform forward and backward search of message codes
        Byte forwardSearchResult = MESSAGE_PAIRS.get(msgCode);

        if (forwardSearchResult == null) {
            return MESSAGE_PAIRS.inverse().get(msgCode);
        }

        return forwardSearchResult;
    }

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

    // TODO this should be loaded from protocol def. somehow
    // TODO use reflication to add to set? idk.
    static {
        int pairCount = 6;

        QUERY_SET = new LinkedHashSet<>(pairCount);

        QUERY_SET.add(OFMsgType.OFPT_BARRIER_REQUEST);
        QUERY_SET.add(OFMsgType.OFPT_ECHO_REQUEST);
        QUERY_SET.add(OFMsgType.OFPT_FEATURES_REQUEST);
        QUERY_SET.add(OFMsgType.OFPT_GET_CONFIG_REQUEST);
        QUERY_SET.add(OFMsgType.OFPT_QUEUE_GET_CONFIG_REQUEST);
        QUERY_SET.add(OFMsgType.OFPT_STATS_REQUEST);


        REPLY_SET = new LinkedHashSet<>(pairCount);

        REPLY_SET.add(OFMsgType.OFPT_BARRIER_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_ECHO_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_FEATURES_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_GET_CONFIG_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_QUEUE_GET_CONFIG_REPLY);
        REPLY_SET.add(OFMsgType.OFPT_STATS_REQUEST);

        MESSAGE_PAIRS = HashBiMap.create();
        convertSetsToBiMap(QUERY_SET, REPLY_SET);
    }

    private static void convertSetsToBiMap(LinkedHashSet requestSet, LinkedHashSet replySet) {
        // Note that LinkedHashSet returns element in insertion order

        Byte[] replySetArray = new Byte[replySet.size()];
        REPLY_SET.toArray(replySetArray);

        Byte[] requestSetArray = new Byte[requestSet.size()];
        QUERY_SET.toArray(requestSetArray);

        for (int i = 0; i < replySetArray.length; i++) {
            MESSAGE_PAIRS.put(requestSetArray[i], replySetArray[i]);
        }
        int x = 3;
    }
}
