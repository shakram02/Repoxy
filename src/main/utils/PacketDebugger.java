package utils;

import of_packets.OFMsgType;
import of_packets.OFPacket;
import of_packets.OFStreamParser;
import utils.events.SocketDataEventArg;
import utils.logging.ConsoleColors;

import java.util.Arrays;
import java.util.List;

public class PacketDebugger {
    public void debugPackets(SocketDataEventArg arg) {
        String debugMessage = stringifyPackets(arg.getSenderType(), arg.getPackets());
        if (debugMessage.length() == 0) {
            return;
        }

        System.out.println(debugMessage);
    }

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
        int count = 0;
        for (OFPacket p : packets) {
            if (p.getHeader().getMessageCode() == OFMsgType.OFPT_ECHO_REPLY ||
                    p.getHeader().getMessageCode() == OFMsgType.OFPT_ECHO_REQUEST) {
                continue;
            }
            count++;
            infoBuilder.append("\t");
            infoBuilder.append(p.getHeader());
            infoBuilder.append("\n\t\t");
            infoBuilder.append(Arrays.toString(OFStreamParser.serializePacket(p).array()));
            infoBuilder.append("\n");
        }
        infoBuilder.append(ConsoleColors.RESET);
        if (count == 0) {
            return "";
        }
        return infoBuilder.toString();
    }
}
