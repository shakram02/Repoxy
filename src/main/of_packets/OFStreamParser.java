package of_packets;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import utils.io.PartitionReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class OFStreamParser {

    private OFStreamParser() {
    }

    @NotNull
    public static OFStreamParseResult parseStream(byte[] bytes) {
        ImmutableList.Builder<OFPacket> parsedPackets = new ImmutableList.Builder<>();

        ByteArrayInputStream s = new ByteArrayInputStream(bytes);
        byte[] remaining = {};

        try (PartitionReader reader = new PartitionReader(OFPacketHeader.HEADER_LEN, s)) {
            while (reader.hasPartition()) {
                Optional<OFPacket> maybePacket = OFStreamParser.ReadOnePacket(reader);

                if (!maybePacket.isPresent()) {
                    return new OFStreamParseResult(bytes);
                }
                parsedPackets.add(maybePacket.get());
            }

            if (reader.hasAny()) {
                remaining = reader.getNextPartition();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new OFStreamParseResult(parsedPackets.build(), remaining);

    }

    public static ByteBuffer serializePacket(OFPacket packet) {
        OFPacketHeader header = packet.getHeader();
        ByteBuffer buffer = ByteBuffer.allocate(header.getLen());

        buffer.put(header.getVersion());
        buffer.put(header.getMessageCode());
        buffer.putShort((short) header.getLen());
        buffer.putInt(header.getXId());

        if (packet.getData().length != 0) {
            buffer.put(packet.getData());
        }


        return buffer;
    }

    /**
     * Gets the first available OF packet.
     *
     * @param reader stream containing at least the length of one header
     * @return Parsed OFPacket
     * @throws IOException stream is closed
     */
    private static Optional<OFPacket> ReadOnePacket(PartitionReader reader) throws IOException {

        byte[] result = reader.getNextPartition();
        Optional<OFPacketHeader> header = OFPacketHeader.ParseHeader(result);

        if (!header.isPresent() || header.get().isInvalid()) {
            return Optional.empty();
        }

        int ofPacketLength = header.get().getLen() - OFPacketHeader.HEADER_LEN;

        if (ofPacketLength == 0) {
            // Packet was only a header ex. OF_HELLO
            return Optional.of(new OFPacket(header.get(), new byte[0]));
        }

        byte[] body = reader.getBulk(ofPacketLength);

        return Optional.of(new OFPacket(header.get(), body));
    }
}
