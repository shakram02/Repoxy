package of_packets;

import org.junit.Test;

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
    public void parseEmpty() {
        byte[] empty = new byte[]{};
        OFStreamParseResult result = OFStreamParser.parseStream(empty);
        assert !result.hasPackets() && !result.hasRemaining();
    }
}