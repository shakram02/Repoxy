package utils;

public class ControllerChangeEventArg extends BasicSocketEventArg {

    private final String ip;
    private final int port;

    public ControllerChangeEventArg(String ip, int port) {
        super(SenderType.Mediator, EventType.ChangeController);
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
