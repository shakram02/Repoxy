package of_packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import jdk.nashorn.internal.ir.annotations.Immutable;
import of_packets.ImmutableOFPacket;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents an OF packet header
 */
@Value.Immutable
public abstract class OFPacketHeader {
    public static final int HEADER_LEN = 8;
    private static final HashMap<Byte, String> MSG_TYPE;

    static {
        MSG_TYPE = new HashMap<>();
        MSG_TYPE.put((byte) 0, "Hello");
        MSG_TYPE.put((byte) 1, "Error");
        MSG_TYPE.put((byte) 2, "Echo Request");
        MSG_TYPE.put((byte) 3, "Echo Reply");
        MSG_TYPE.put((byte) 4, "Vendor");
        MSG_TYPE.put((byte) 5, "Features Request");
        MSG_TYPE.put((byte) 6, "Features Reply");
        MSG_TYPE.put((byte) 7, "Get Config Request");
        MSG_TYPE.put((byte) 8, "Get Config Reply");
        MSG_TYPE.put((byte) 9, "Set Config");
        MSG_TYPE.put((byte) 10, "Packet Input Notification");
        MSG_TYPE.put((byte) 11, "Flow Removed Notification");
        MSG_TYPE.put((byte) 12, "Port Status Notification");
        MSG_TYPE.put((byte) 13, "Packet Output");
        MSG_TYPE.put((byte) 14, "Flow Modification");
        MSG_TYPE.put((byte) 15, "Port Modification");
        MSG_TYPE.put((byte) 16, "Stats Request");
        MSG_TYPE.put((byte) 17, "Stats Reply");
        MSG_TYPE.put((byte) 18, "Barrier Request");
        MSG_TYPE.put((byte) 19, "Barrier Reply");
    }

    public abstract byte getVersion();

    public abstract byte getMessageCode();

    public abstract int getLen();

    public abstract int getXid();

    public abstract OFPacketHeader withXid(int xid);

    @NotNull
    public String getMessageType() {
        return MSG_TYPE.get(this.getMessageCode());
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
        // FIXME remove this unnecessary code.
        // parsing errors are programmers mistakes and shouldn't be checked
        if (bytes.length < OFPacketHeader.HEADER_LEN) {
            return Optional.empty();
        }

        ByteArrayDataInput buff = ByteStreams.newDataInput(bytes);

        byte version = buff.readByte();
        byte msg_t = buff.readByte();
        int len = buff.readUnsignedShort();
        int x_id = buff.readInt();

        OFPacketHeader header = of_packets.ImmutableOFPacketHeader.builder()
                .version(version)
                .messageCode(msg_t)
                .len(len)
                .xid(x_id)
                .build();

        return Optional.of(header);
    }


    public boolean isEquivalentTo(OFPacketHeader other) {
        return this.equals(other);
    }

    @Override
    public String toString() {
        return String.format(
                "type:%s xId:%d len:%d%n",
                this.getMessageType(), this.getXid(), this.getLen());
    }
}

