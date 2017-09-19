package watchers;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.Dumper;
import utils.SenderType;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class PacketDumper implements SocketEventObserver {

    private final Dumper<OFPacket> fileDumper;
    String fileName;
    HashMap<SenderType, Path> pathCache;

    public PacketDumper(String fileName) throws IOException {
        this.fileName = fileName;
        fileDumper = new Dumper<>(this::serializeToBytes);
        pathCache = new HashMap<>();
    }

    private byte[] serializeToBytes(OFPacket packet) {
        return OFStreamParser.serializePacket(packet).array();
    }

    private void dumpPacket(SocketDataEventArg arg, SenderType type) {
        Path path = getPathFromCache(type);
        this.fileDumper.dump(arg.getPacket(), path);
    }

    @Override
    public void dispatchEvent(@NotNull SocketEventArguments eventArgs) {
        if (!(eventArgs instanceof SocketDataEventArg)) return;

        SocketDataEventArg dataEventArg = (SocketDataEventArg) eventArgs;
        dumpPacket(dataEventArg, dataEventArg.getSenderType());
    }

    private Path getPathFromCache(SenderType type) {
        Path path;

        if (pathCache.containsKey(type)) {
            path = pathCache.get(type);
        } else {
            path = Paths.get(fileName + type.toString() + ".dat");
            pathCache.put(type, path);
        }

        return path;
    }
}
