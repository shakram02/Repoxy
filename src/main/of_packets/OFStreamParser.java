package of_packets;

import com.google.common.io.ByteArrayDataOutput;
import utils.io.PartitionReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class OFStreamParser {

    private OFStreamParser() {
    }

    public static OFStreamParseResult parseStream(ByteArrayDataOutput stream) {
        ArrayList<OFPacket> parsedPackets = new ArrayList<>();

        ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());


        try (PartitionReader reader = new PartitionReader(OFPacketHeader.HEADER_LEN, s)) {
            while (reader.hasPartition()) {
                Optional<OFPacket> maybePacket = OFStreamParser.ReadOnePacket(reader);

                if (!maybePacket.isPresent()) {
                    return new OFStreamParseResult(stream.toByteArray());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


//        return new OFStreamParseResult(parsedPackets, )
        return null;
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
