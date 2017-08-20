package utils.xid_sync;

// Keys: ConnectionId, RequestType, Timestamp/LinkedList

import of_packets.OFPacketHeader;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Manages the storage and retrieval of Request packets
 * ex. (Barrier, Config, Echo, ...etc)
 * <p>
 * FIXME merge with QueueMap after the QueueMap is edited
 */
public class RequestBuffer {
    private static final String SPACE = " ";
    private final HashMap<String, XidUnit> xidMap;

    public RequestBuffer() {
        xidMap = new HashMap<>();
    }

    /**
     * Adds a request-packet header to the map
     *
     * @param id     Id of the issuing connection
     * @param header header of the request-packet
     */
    public void addRequest(@NotNull final ConnectionId id, @NotNull final OFPacketHeader header) {
        String key = createKey(id, header);
        int xid = header.getXId();

        if (xidMap.containsKey(key)) {
            // A unit already exists, append to it
            xidMap.get(key).addXid(xid);
        } else {
            XidUnit unit = new XidUnit();
            unit.addXid(xid);
            xidMap.put(key, unit);
        }
    }

    /**
     * Returns a request for a given connection in storage order
     *
     * @param id     Id of the issuing connection
     * @param header header of the reply-packet
     * @return x-id of the request of this reply-packet
     */
    @NotNull
    public Integer getRequest(@NotNull final ConnectionId id, @NotNull final OFPacketHeader header) {
        String key = createKey(id, header);

        if (xidMap.containsKey(key)) {
            XidUnit unit = xidMap.get(key);
            int xid = unit.getXid();

            // Remove entries when they're empty
            if (unit.isEmpty()) {
                xidMap.remove(key);
            }

            return xid;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Clears all data in request buffer.
     * <p>
     * FIXME
     * This should be used when switching controllers?
     */
    public void resetEntries() {
        xidMap.clear();
    }

    // FIXME find a better method to strip the request name and create the key fgs
    private static String createKey(ConnectionId id, OFPacketHeader header) {
        String requestName = stripRequestName(header.getMessageType());
        return id + requestName;
    }

    /**
     * Takes out the request name from packet type
     * ex. Barrier Request -> Barrier
     *
     * @param requestType Full name of the type of packet request
     * @return request name
     */
    private static String stripRequestName(String requestType) {
        return requestType.split(SPACE)[0].trim();
    }

    /**
     * Stores multiple x-ids in order for a given hash map entry
     */
    private class XidUnit {
        LinkedList<Integer> xIds;

        protected XidUnit() {
            this.xIds = new LinkedList<>();
        }

        public void addXid(int xid) {
            this.xIds.add(xid);
        }

        public int getXid() {
            return this.xIds.removeFirst();
        }

        public boolean isEmpty() {
            return this.xIds.size() == 0;
        }
    }
}
