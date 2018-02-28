package network_io;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataOutput;
import openflow.OFPacket;
import openflow.OFStreamParser;
import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.PacketBuffer;
import utils.SenderType;
import utils.events.SocketDataEventArg;
import utils.events.SocketEventArguments;

import java.util.concurrent.LinkedBlockingQueue;

public class CommonNioHandler {
    private static final int BUFFER_SIZE = 4096;
    protected final HashBiMap<NetworkClient, ConnectionId> keyMap;
    protected final SenderType selfType;
    // Adding to output queue should be done only through calling emitToEventQueue()
    // as child classes may want to override what happens when adding an event
    private final LinkedBlockingQueue<SocketEventArguments> outputQueue;
    private final LinkedBlockingQueue<SocketEventArguments> inputQueue;

    // Packet buffer is kept as the selector notifies me when the
    // socket is writable, mean while the event processor doesn't
    // know that info.
    private final PacketBuffer packetBuffer;


    public CommonNioHandler(SenderType selfType) {
        this.selfType = selfType;

        this.keyMap = HashBiMap.create();

        this.inputQueue = new LinkedBlockingQueue<>();
        this.outputQueue = new LinkedBlockingQueue<>();
        this.packetBuffer = new PacketBuffer();
    }

    /**
     * Add an item to event queue.
     *
     * @param arg Event data argument to be added
     */
    protected void emitToEventQueue(@NotNull SocketEventArguments arg) {
        this.outputQueue.add(arg);
    }

    /**
     * Adds a socket event in the event queue for processing
     *
     * @param arg Command for socket IO (CloseConnection/SendData)
     */
    public void addToEventQueue(@NotNull SocketEventArguments arg) {
        this.inputQueue.add(arg);
    }

    protected void onData(NetworkClient client, ByteArrayDataOutput data) {
        ImmutableList<OFPacket> packets = OFStreamParser.parseStream(data.toByteArray());

        for (OFPacket p : packets) {
            SocketDataEventArg arg = utils.events.ImmutableSocketDataEventArg.builder()
                    .id(client.getId())
                    .senderType(this.selfType)
                    .packet(p)
                    .build();

            this.emitToEventQueue(arg);
        }
    }
}
