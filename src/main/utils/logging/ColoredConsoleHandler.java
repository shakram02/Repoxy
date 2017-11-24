package tests.utils.logging;

import java.util.logging.*;

public class ColoredConsoleHandler extends StreamHandler {

    public ColoredConsoleHandler() {
        this.setOutputStream(System.out);
        this.setFormatter(new ColoredConsoleFormatter());
        this.setFilter(null);
        this.setLevel(Level.ALL);
    }

    @Override
    public synchronized void publish(LogRecord logRecord) {
        // Implementation is taken from ConsoleHandler byte-code de-compilation
        super.publish(logRecord);
        this.flush();
    }
}
