package watchers;

import of_packets.OFPacket;
import of_packets.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.SenderType;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;
import utils.events.SocketEventObserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PacketDumper implements SocketEventObserver {

    String fileName;

    public PacketDumper(String fileName) throws IOException {
        Path logDirectory = getLogDirectoryPath();

        if (!Files.exists(logDirectory)) {
            Files.createDirectory(logDirectory);
        }

        // TODO: flush the written files every now and then
        this.fileName = logDirectory.toString() + "/" + fileName;
    }

    private void dumpPacket(OFPacket packet, SenderType type) {
        try {
            Files.write(Paths.get(fileName + type.toString() + ".dat"),
                    OFStreamParser.serializePacket(packet).array(),
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
}
