package of_packets;


import com.google.common.primitives.Bytes;
import of_packets.ImmutableOFPacket;
import of_packets.ImmutableOFPacketHeader;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class OFStreamParserTest {


    @Test
    public void parseStream() throws Exception {
        // Same as python ofgenerator output
        byte[] helloBytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};

        OFStreamParseResult result = OFStreamParser.parseStream(helloBytes);

        assert OFPacketHeader.ParseHeader(helloBytes).isPresent();
        OFPacketHeader header = OFPacketHeader.ParseHeader(helloBytes).get();
        assert header.getMessageType().equals("Hello");

        assert result.hasPackets();
    }

    @Test
    public void parseBarrierReply() throws Exception {
        byte[] bytes = new byte[]{1, 19, 0, 8, 0, 0, 0, 1};
        OFStreamParseResult result = OFStreamParser.parseStream(bytes);

        assert OFPacketHeader.ParseHeader(bytes).isPresent();
        OFPacketHeader header = OFPacketHeader.ParseHeader(bytes).get();
        assert header.getMessageType().equals("Barrier Reply");

        assert result.hasPackets();
    }

    @Test
    public void testSerializationHeaderOnly() throws IOException {
        // All assertions will fail as binary serialization adds JVM attributes to
        // the serialized object

        byte[] bytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};

        OFPacketHeader helloHeader = ImmutableOFPacketHeader.builder()
                .version((byte) 1)
                .messageCode((byte) 0)
                .len(8)
                .xid(1)
                .build();

//        ByteBuffer buffer = OFStreamParser.serializePacket(new OFPacket(helloHeader, new byte[]{}));
        ByteBuffer buffer = OFStreamParser.serializePacket(
                ImmutableOFPacket.builder()
                        .header(helloHeader)
                        .data().build());

        byte[] parsed = buffer.array();


        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], parsed[i]);
        }
    }

    @Test
    public void testSerializationData() {
        byte[] headerBytes = new byte[]{1, 0, 0, 10, 0, 0, 0, 1};
        byte[] dataBytes = new byte[]{125, 54};
        byte[] packetBytes = Bytes.concat(headerBytes, dataBytes);


        OFPacket packet = OFStreamParser.parseStream(packetBytes).getPackets().get(0);
        ByteBuffer buffer = OFStreamParser.serializePacket(packet);

        byte[] parsed = buffer.array();

        assertEquals(packetBytes.length, parsed.length);

        for (int i = 0; i < packetBytes.length; i++) {
            assertEquals(packetBytes[i], parsed[i]);
        }
    }

    @Test
    public void parseEmpty() {
        byte[] empty = new byte[]{};
        OFStreamParseResult result = OFStreamParser.parseStream(empty);
        assert !result.hasPackets() && !result.hasRemaining();
    }
}