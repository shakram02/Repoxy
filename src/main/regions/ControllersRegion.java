package regions;

import utils.CreateConnectionArgs;
import utils.SocketEventArg;


import java.io.IOException;
import java.util.logging.Level;

public final class ControllersRegion extends WatchedRegion {

    public ControllersRegion() {
        super(ControllersRegion.class);
    }

    public void connect(String ip, int port) {
        try {
            this.ioHandler.createConnection(ip, port);
        } catch (IOException e) {
            this.logger.log(Level.SEVERE,
                    "Error opening connection to controller", e);
        }
    }

    @Override
    protected void onConnection(SocketEventArg a) {
        System.out.println("Connected to controller");
    }

    @Override
    protected void onDisconnect(SocketEventArg arg) {
        logger.log(Level.INFO, "Controller closed connection with ID " + arg.getId());
    }
}
