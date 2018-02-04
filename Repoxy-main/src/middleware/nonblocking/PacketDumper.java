package middleware.nonblocking;

import openflow.OFPacket;
import openflow.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.Dumper;
import utils.SenderType;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.io.IOException;
import java.util.HashMap;

public class PacketDumper implements SocketEventObserver {

    private final Dumper<OFPacket> fileDumper;
    String fileName;
    HashMap<SenderType, String> pathCache;

    public PacketDumper(String fileName) throws IOException {
        this.fileName = fileName;
        fileDumper = new Dumper<>(this::serializeToBytes);
        pathCache = new HashMap<>();
    }

    private byte[] serializeToBytes(OFPacket packet) {
        return OFStreamParser.serializePacket(packet).array();
    }

    private void dumpPacket(SocketDataEventArg arg, SenderType type) {
        String path = getPathFromCache(type);
        this.fileDumper.dump(arg.getPacket(), path);
    }

    @Override
    public void dispatchEvent(@NotNull SocketEventArguments eventArgs) {
        if (!(eventArgs instanceof SocketDataEventArg)) return;

        SocketDataEventArg dataEventArg = (SocketDataEventArg) eventArgs;
        dumpPacket(dataEventArg, dataEventArg.getSenderType());
    }

    private String getPathFromCache(SenderType type) {
        String path;

        if (pathCache.containsKey(type)) {
            path = pathCache.get(type);
        } else {
            path = fileName + type.toString() + ".dat";
            pathCache.put(type, path);
        }

        return path;
    }
}
