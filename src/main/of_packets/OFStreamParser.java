package tests.of_packets;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import tests.utils.io.PartitionReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class OFStreamParser {

    private OFStreamParser() {
    }

    @NotNull
    public static ImmutableList<OFPacket> parseStream(byte[] bytes) {
        ImmutableList.Builder<OFPacket> parsedPackets = new ImmutableList.Builder<>();

        ByteArrayInputStream s = new ByteArrayInputStream(bytes);

        try (PartitionReader reader = new PartitionReader(OFPacketHeader.HEADER_LEN, s)) {
            while (reader.hasPartition()) {
                OFPacket maybePacket = OFStreamParser.readOnePacket(reader);
                parsedPackets.add(maybePacket);
            }

            if (reader.hasAny()) {
                throw new IllegalStateException("Parsing failed:" + Arrays.toString(bytes));
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return parsedPackets.build();
    }

    public static ByteBuffer serializePacket(OFPacket packet) {
        OFPacketHeader header = packet.getHeader();
        ByteBuffer buffer = ByteBuffer.allocate(header.getLen());

        buffer.put(header.getVersion());
        buffer.put(header.getMessageCode());
        buffer.putShort((short) header.getLen());
        buffer.putInt(header.getXid());

        if (packet.getData().length != 0) {
            buffer.put(packet.getData());
        }

        buffer.flip();  // Make the buffer ready for consumption

        return buffer;
    }

    /**
     * Gets the first available OF packet.
     *
     * @param reader stream containing at least the length of one header
     * @return Parsed OFPacket
     * @throws IOException stream is closed
     */
    @NotNull
    private static OFPacket readOnePacket(PartitionReader reader) throws IOException {

        byte[] result = reader.getNextPartition();
        OFPacketHeader header = OFPacketHeader.parseHeader(result);

        int ofPacketLength = header.getLen() - OFPacketHeader.HEADER_LEN;

        if (ofPacketLength == 0) {
            // Packet was only a header ex. OF_HELLO
            return tests.of_packets.ImmutableOFPacket.builder()
                    .data()
                    .header(header)
                    .build();
        }

        byte[] body = reader.getBulk(ofPacketLength);
        return tests.of_packets.ImmutableOFPacket.builder()
                .data(body)
                .header(header)
                .build();
    }
}
