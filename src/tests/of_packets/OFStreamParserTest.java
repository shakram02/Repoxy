package of_packets;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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

    @Test(expected = AssertionError.class)
    public void testSerialization() throws IOException {
        // All assertions will fail as binary serialization adds JVM attributes to
        // the serialized object

        byte[] bytes = new byte[]{1, 0, 0, 8, 0, 0, 0, 1};
        OFPacketHeader helloHeader = new OFPacketHeader((byte) 1, (byte) 0, 8, 1);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(outStream);
        objStream.writeObject(helloHeader);
        objStream.flush();
        byte[] serialized = outStream.toByteArray();

        assert bytes.length == serialized.length : "Different lengths";

        for (int i = 0; i < serialized.length; i++) {
            assert (int) serialized[i] == bytes[i] :
                    String.format("Object bytes not equal, left: %d right: %d", serialized[i], bytes[i]);
        }
    }

    @Test
    public void parseEmpty() {
        byte[] empty = new byte[]{};
        OFStreamParseResult result = OFStreamParser.parseStream(empty);
        assert !result.hasPackets() && !result.hasRemaining();
    }
}