package utils;

public class ControllerChangeEventArg extends SocketEventArg {

    private final String ip;
    private final int port;

    public ControllerChangeEventArg(String ip, int port) {
        super(SenderType.Mediator, EventType.ChangeController, null);
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
