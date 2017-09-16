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

    Path file;

    public PacketDumper(String fileName, SenderType type) throws FileNotFoundException {
        this.file = Paths.get(fileName + type.toString());
    }

    private void dumpPacket(OFPacket packet) {
        try {

            Files.write(file, OFStreamParser.serializePacket(packet).array(),
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
        dumpPacket(dataEventArg.getPacket());
    }
}
