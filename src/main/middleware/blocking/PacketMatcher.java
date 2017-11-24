package middleware.packet_verification;

import of_packets.OFPacket;
import utils.ConnectionId;
import utils.MatchBuffer;
import utils.events.SocketDataEventArg;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

class PacketMatcher {
    private final BiConsumer<SocketDataEventArg, SocketDataEventArg> onMatch;
    private final HashMap<ConnectionId, MatchBuffer<SocketDataEventArg>> matchBufferMap;

    PacketMatcher(BiConsumer<SocketDataEventArg, SocketDataEventArg> onMatch) {
        this.onMatch = onMatch;
        this.matchBufferMap = new HashMap<>();
    }

    public void addPacket(final SocketDataEventArg packet) {
        ConnectionId id = packet.getId();
        MatchBuffer<SocketDataEventArg> buffer = getBufferOrPutIfAbsent(id, this::doMatch);

        Optional<SocketDataEventArg> match = buffer.addIfMatchNotFound(packet);
        // If a match exists, inform the upper class
        match.ifPresent(socketDataEventArg -> this.onMatch.accept(packet, socketDataEventArg));
    }

    public List<Iterator<SocketDataEventArg>> unmatchedIterators() {
        return this.matchBufferMap
                .values()
                .stream()
                .map(buffer -> buffer.getCollection().iterator())
                .collect(Collectors.toList());
    }

    public int countUnmatched() {
        return this.matchBufferMap
                .values()
                .stream()
                .mapToInt(MatchBuffer::getSize)
                .sum();
    }

    private boolean doMatch(SocketDataEventArg first, SocketDataEventArg second) {
        // Match connection ids first
        if (!first.getId().equals(second.getId())) {
            return false;
        }

        OFPacket firstPacket = first.getPacket();
        OFPacket secondPacket = second.getPacket();

        return firstPacket.getMessageCode() == secondPacket.getMessageCode() &&
                Arrays.equals(firstPacket.getData(), secondPacket.getData());
    }

    private MatchBuffer<SocketDataEventArg> getBufferOrPutIfAbsent(
            ConnectionId id,
            BiPredicate<SocketDataEventArg, SocketDataEventArg> matchingFunction) {

        MatchBuffer<SocketDataEventArg> buffer;
        if (this.matchBufferMap.containsKey(id)) {
            buffer = this.matchBufferMap.get(id);
        } else {
            buffer = new MatchBuffer<>(matchingFunction);
            this.matchBufferMap.put(id, buffer);
        }

        return buffer;
    }
}
