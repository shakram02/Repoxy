package utils.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ColoredConsoleFormatter extends Formatter {


    @Override
    public String format(LogRecord logRecord) {

        return this.levelColor(logRecord.getLevel()) + "XX" + logRecord.getMessage() + " " + logRecord.getSourceMethodName()
                + " \n" + ConsoleColors.RESET;
    }

    private String levelColor(Level level) {
        if (level == NetworkLogLevels.CONTROLLER) {
            return ConsoleColors.YELLOW_BOLD;
        } else if (level == NetworkLogLevels.REPLICA) {
            return ConsoleColors.GREEN;
        } else if (level == NetworkLogLevels.SWITCH) {
            return ConsoleColors.CYAN_BOLD;
        } else if (level == NetworkLogLevels.DIFFER) {
            return ConsoleColors.RED_UNDERLINED;
        }
        return ConsoleColors.WHITE;
    }

}
