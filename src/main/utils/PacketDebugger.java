package utils;

import of_packets.OFMsgType;
import of_packets.OFPacket;
import of_packets.OFStreamParser;
import utils.events.SocketDataEventArg;
import utils.logging.ConsoleColors;

import java.util.Arrays;

public class PacketDebugger {
    private long basetime;

    public PacketDebugger() {
        basetime = MonotonicClock.getTimeMillis();
    }

    public void debugDataEventArg(ConnectionId id, SocketDataEventArg arg) {
        String debugMessage = stringifyPacket(id, arg.getSenderType(), arg.getPacket());
        if (debugMessage.length() == 0) {
            return;
        }

        System.out.println(debugMessage);
    }

    public String stringifyPacket(ConnectionId id, SenderType sender, OFPacket packet) {
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
        infoBuilder.append(MonotonicClock.getTimeMillis() - basetime);
        infoBuilder.append(" | ");
        infoBuilder.append("From");
        infoBuilder.append(" [id:");
        infoBuilder.append(id.toString());
        infoBuilder.append("] ");
        infoBuilder.append(sender);
        infoBuilder.append("\n");

//        if (packet.getHeader().getMessageCode() == OFMsgType.OFPT_ECHO_REPLY ||
//                packet.getHeader().getMessageCode() == OFMsgType.OFPT_ECHO_REQUEST) {
//            return "";
//        }

        infoBuilder.append("\t");
        infoBuilder.append(packet.getHeader());
//        infoBuilder.append("\t\t");
//        infoBuilder.append(Arrays.toString(OFStreamParser.serializePacket(packet).array()));
        infoBuilder.append("\n");

        infoBuilder.append(ConsoleColors.RESET);
        return infoBuilder.toString();
    }

    private StringBuilder batchStringBuilder = new StringBuilder();

    public void batchDebugStart() {
        batchStringBuilder.setLength(0);
    }

    public void addToBatchDebug(ConnectionId id, SenderType sender, OFPacket packet) {
        String stringed = this.stringifyPacket(id, sender, packet);

        if (stringed.length() == 0) {
            return;
        }

        batchStringBuilder.append(stringed);
    }

    public String batchDebugEnd() {
        return batchStringBuilder.toString();
    }
}
