package of_packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

/**
 * Represents an OF packet header
 */
public class OFPacketHeader {
    private static final HashMap<Integer, String> MSG_TYPE;
    public static final int HEADER_LEN = 8;
    private byte version;
    private String messageType;
    private int len;
    private int xId;
    private boolean valid;

    private OFPacketHeader(byte version, byte msg_t_id, int len, int x_id) {

        // FIXME some TCP packets may match the OFMSG type. we need to find
        // a better checking mechanism as raw TCP packets are used for testing so far

        this.version = version;
        this.len = len;
        this.xId = x_id;
        if (MSG_TYPE.containsKey((int) msg_t_id)) {
            this.messageType = MSG_TYPE.get((int) msg_t_id);
            this.valid = true;
        } else {
            this.valid = false;
        }
    }

    private OFPacketHeader() {
    }

    private static OFPacketHeader CreateInvalid() {
        OFPacketHeader header = new OFPacketHeader();
        header.valid = false;
        return header;
    }

    /**
     * Extracts the first OF packet header from an array of bytes containing
     * one or more packet headers.
     *
     * @param bytes Byte array containing one or more packet headers
     * @return - Parsed {@link OFPacketHeader} if the input condition is met
     * - Invalid {@link OFPacketHeader} If the byte array provided is smaller than OF Header
     */

    @NotNull
    public static Optional<OFPacketHeader> ParseHeader(byte[] bytes) {
        if (bytes.length < OFPacketHeader.HEADER_LEN) {
            return Optional.empty();
        }

        ByteArrayDataInput buff = ByteStreams.newDataInput(bytes);

        byte version = buff.readByte();
        byte msg_t = buff.readByte();
        int len = buff.readUnsignedShort();
        int x_id = buff.readInt();

        return Optional.of(new OFPacketHeader(version, msg_t, len, x_id));
    }

    @Override
    public String toString() {
        return String.format(
                "VER:%d TYPE:%s LEN:%d T_ID:%d%n",
                this.version, this.messageType, this.len, this.xId);
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isInvalid() {
        return !this.valid;
    }

    static {
        MSG_TYPE = new HashMap<>();
        MSG_TYPE.put(0, "Hello");
        MSG_TYPE.put(1, "Error");
        MSG_TYPE.put(2, "Echo Request");
        MSG_TYPE.put(3, "Echo Reply");
        MSG_TYPE.put(4, "Vendor");
        MSG_TYPE.put(5, "Features Request");
        MSG_TYPE.put(6, "Features Reply");
        MSG_TYPE.put(7, "Get Config Request");
        MSG_TYPE.put(8, "Get Config Reply");
        MSG_TYPE.put(9, "Set Config");
        MSG_TYPE.put(10, "Packet Input Notification");
        MSG_TYPE.put(11, "Flow Removed Notification");
        MSG_TYPE.put(12, "Port Status Notification");
        MSG_TYPE.put(13, "Packet Output");
        MSG_TYPE.put(14, "Flow Modification");
        MSG_TYPE.put(15, "Port Modification");
        MSG_TYPE.put(16, "Stats Request");
        MSG_TYPE.put(17, "Stats Reply");
        MSG_TYPE.put(18, "Barrier Request");
        MSG_TYPE.put(19, "Barrier Reply");
    }

    public int getLen() {
        return len;
    }

    public String getMessageType() {
        return messageType;
    }

}

