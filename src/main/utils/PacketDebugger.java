package utils;

import of_packets.OFMsgType;
import of_packets.OFPacket;
import of_packets.OFStreamParser;
import utils.events.SocketDataEventArg;
import utils.logging.ConsoleColors;

public class PacketDebugger {
    public void debugDataEventArg(SocketDataEventArg arg) {
        String debugMessage = stringifyPacket(arg.getSenderType(), arg.getPacket());
        if (debugMessage.length() == 0) {
            return;
        }

        System.out.println(debugMessage);
    }

    public String stringifyPacket(SenderType sender, OFPacket packet) {
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

    private boolean isBatchMode;
    private StringBuilder batchStringBuilder = new StringBuilder();

    public void batchDebugStart() {
        if (isBatchMode) {
            // clear queue
            batchStringBuilder.setLength(0);
        }
        isBatchMode = true;
    }

    public void addToBatchDebug(SenderType sender, OFPacket packet) {
        batchStringBuilder.append(this.stringifyPacket(sender, packet));
    }

    public String batchDebugEnd() {
        isBatchMode = false;
        return batchStringBuilder.toString();
    }
}
