package utils.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ColoredConsoleFormatter extends Formatter {


    @Override
    public String format(LogRecord logRecord) {
        String levelColor = this.levelColor(logRecord.getLevel());

        return levelColor + "[" + logRecord.getLevel() + " - " +
                logRecord.getSourceMethodName() + "] "
                + logRecord.getMessage()
                + "\n" + ConsoleColors.RESET;
    }

    private String levelColor(Level level) {
        if (level == NetworkLogLevels.CONTROLLER) {
            return ConsoleColors.BLUE_BOLD;
        } else if (level == NetworkLogLevels.REPLICA) {
            return ConsoleColors.GREEN;
        } else if (level == NetworkLogLevels.SWITCH) {
            return ConsoleColors.CYAN;
        } else if (level == NetworkLogLevels.DIFFER) {
            return ConsoleColors.RED_UNDERLINED;
        } else if (level == Level.INFO) {
            return ConsoleColors.BLUE_BRIGHT;
        }
        return ConsoleColors.WHITE;
    }

}
