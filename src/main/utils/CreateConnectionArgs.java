package utils;
import java.net.SocketAddress;

public class CreateConnectionArgs extends SocketEventArg {
    private final SocketAddress address;

    /**
     * Create connection prompts the controller region to open a new connection
     * when a client connects to the switches region
     *
     * @param senderType Event source
     * @param id         Connection ID of the newly connected client
     * @param address    Address of the controller
     */
    public CreateConnectionArgs(SenderType senderType,

                                ConnectionId id, SocketAddress address) {
        super(senderType, EventType.Connection, id);
        this.address = address;
    }

    public String getAddress() {
        return this.address.toString();
    }
}
