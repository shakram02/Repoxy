package utils;


public class CreateConnectionArgs extends SocketEventArg {
    private final String ip;
    private final int port;


    public CreateConnectionArgs(SenderType senderType,
                                EventType eventType,
                                ConnectionId id, String ip, int port) {
        super(senderType, eventType, id);
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
