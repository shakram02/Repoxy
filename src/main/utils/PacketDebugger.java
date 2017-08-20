package utils;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import utils.logging.ConsoleColors;

import java.util.Arrays;
import java.util.List;

public class PacketDebugger {
    public String stringifyPackets(SenderType sender, List<OFPacket> packets) {
        StringBuilder infoBuilder = new StringBuilder();
        String color = "";
        switch (sender) {
            case ReplicaRegion:
                color = ConsoleColors.CYAN_BRIGHT;
                break;
            case ControllerRegion:
                color = ConsoleColors.GREEN_BRIGHT;
                break;
            case SwitchesRegion:
                color = ConsoleColors.BLUE;
                break;
        }
        infoBuilder.append(color);
        infoBuilder.append("From:");
        infoBuilder.append(sender);
        infoBuilder.append("\n");

        packets.forEach(p -> {
            if (p.getHeader().getXId() == 0) {
                return;
            }
            infoBuilder.append("\t");
            infoBuilder.append(p.getHeader().getXId());
            infoBuilder.append(" ");
            infoBuilder.append(p.getPacketType());
            infoBuilder.append("\n\t\t");
            infoBuilder.append(Arrays.toString(OFStreamParser.serializePacket(p).array()));
            infoBuilder.append("\n");
        });
        infoBuilder.append(ConsoleColors.RESET);

        return infoBuilder.toString();
    }
}
