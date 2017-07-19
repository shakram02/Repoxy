package regions;

import utils.CreateConnectionArgs;
import utils.SocketEventArg;


import java.io.IOException;
import java.util.logging.Level;

public final class ControllersRegion extends WatchedRegion {

    public ControllersRegion() {
        super(ControllersRegion.class);
    }

    @Override
    protected void onConnection(SocketEventArg a) {
        CreateConnectionArgs arg = (CreateConnectionArgs) a;

        System.out.println("Connected to controller");

        try {
            this.ioHandler.createConnection(arg.getIp(), arg.getPort());
        } catch (IOException e) {
            this.logger.log(Level.SEVERE,
                    "Error opening connection to controller", e);
        }
    }
}
