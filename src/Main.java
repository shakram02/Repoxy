import network_io.SwitchesRegion;

import java.io.IOException;

public class Main {
    public static final String localhost = "127.0.0.1";
    public static final int of_port = 6633;

    public static void main(String[] args) throws IOException {
        SwitchesRegion server = new SwitchesRegion();
        server.startListening(localhost, of_port);

        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                server.close();
                break;
            }
            server.cycle();
        }
    }
}
