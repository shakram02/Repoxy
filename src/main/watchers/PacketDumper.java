package watchers;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class PacketDumper implements SocketEventObserver {

    String fileName;
    HashMap<SenderType, Path> pathCache;

    public PacketDumper(String fileName) throws IOException {
        Path logDirectory = getLogDirectoryPath();

        if (!Files.exists(logDirectory)) {
            // TODO: monitor file sizes, delete oldest
            Files.createDirectory(logDirectory);
        }

        // TODO: flush the written files every now and then
        this.fileName = logDirectory.toString() + "/" + fileName;
        pathCache = new HashMap<>();
    }

    private void dumpPacket(OFPacket packet, SenderType type) {

        Path path = getPathFromCache(type);

        try {
            Files.write(path, OFStreamParser.serializePacket(packet).array(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Couldn't write to file");
        }
    }

    @Override
    public void dispatchEvent(@NotNull SocketEventArguments eventArgs) {
        if (!(eventArgs instanceof SocketDataEventArg)) return;

        SocketDataEventArg dataEventArg = (SocketDataEventArg) eventArgs;
        dumpPacket(dataEventArg.getPacket(), dataEventArg.getSenderType());
    }

    private static Path getLogDirectoryPath() {
        // Current folder /log
        return Paths.get(Paths.get("").toAbsolutePath() + "/log");
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
