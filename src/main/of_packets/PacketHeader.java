package of_packets;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Represents an OF packet header
 */
public class PacketHeader {
    private static final HashMap<Integer, String> MSG_TYPE;
    private static final int INVALID_VERSION = 113;

    private byte version;
    private String message_type;
    private short len;
    private long x_id;

    private PacketHeader(byte version, byte msg_t_id, short len, long x_id) {

        this.version = version;
        this.len = len;
        this.x_id = x_id;
        this.message_type = MSG_TYPE.getOrDefault((int) msg_t_id, "Invalid OF Packet");
    }

    @NotNull
    public static PacketHeader ParsePacket(byte[] bytes) {
        ByteBuffer buff = ByteBuffer.wrap(bytes);
        byte version = (byte) buff.getChar();
        byte msg_t = (byte) buff.getChar();
        short len = buff.getShort();
        long x_id = buff.getLong();

        return new PacketHeader(version, msg_t, len, x_id);
    }

    @Override
    public String toString() {
        return String.format(
                "VER:%d TYPE:%s LEN:%d T_ID:%d%n",
                this.version, this.message_type, this.len, this.x_id);
    }

    public boolean isValid() {
        return this.version == INVALID_VERSION && this.len != -1;
    }

    public boolean isInvalid() {
        return !this.isValid();
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

    public short getLen() {
        return len;
    }

    public String getMessage_type() {
        return message_type;
    }

}

