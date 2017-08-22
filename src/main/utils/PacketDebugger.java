package utils;

import of_packets.OFMsgType;
import of_packets.OFPacket;
import of_packets.OFStreamParser;
import utils.events.SocketDataEventArg;
import utils.logging.ConsoleColors;

public class PacketDebugger {
    public void debugPackets(SocketDataEventArg arg) {
        String debugMessage = stringifyPackets(arg.getSenderType(), arg.getPacket());
        if (debugMessage.length() == 0) {
            return;
        }

        System.out.println(debugMessage);
    }

    public String stringifyPackets(SenderType sender, OFPacket packet) {
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


        if (packet.getHeader().getMessageCode() == OFMsgType.OFPT_ECHO_REPLY ||
                packet.getHeader().getMessageCode() == OFMsgType.OFPT_ECHO_REQUEST) {
            return "";
        }

        infoBuilder.append("\t");
        infoBuilder.append(packet.getHeader());
        infoBuilder.append("\n\t\t");
        infoBuilder.append(OFStreamParser.serializePacket(packet));
        infoBuilder.append("\n");

        infoBuilder.append(ConsoleColors.RESET);
        return infoBuilder.toString();
    }
}
