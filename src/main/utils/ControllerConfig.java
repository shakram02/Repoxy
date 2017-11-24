package utils;

public class ControllerConfig {
    private final String ip;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    private final int port;

    public ControllerConfig(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return String.format("IP:%s, Port:%s", ip, port);
    }
}
