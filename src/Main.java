import base_classes.WatchedRegion;
import network_io.ServerIOHandler;

import java.io.IOException;

/**
 * Created by ahmed on 7/16/17.
 */
public class Main {
    public static final String localhost = "127.0.0.1";
    public static final int of_port = 6633;

    public static void main(String[] args) throws IOException, InterruptedException {
        WatchedRegion region = new WatchedRegion();
        ServerIOHandler server = new ServerIOHandler(region);
        server.startListening(localhost, of_port);

        while (true) {
            server.cycle();

            Thread.sleep(100);
        }
    }
}
