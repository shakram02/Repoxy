package tests.utils.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ColoredConsoleFormatter extends Formatter {


    @Override
    public String format(LogRecord logRecord) {
        String levelColor = this.levelColor(logRecord.getLevel());

        return levelColor + "[" + logRecord.getLevel() + " - " +
                logRecord.getSourceMethodName() + " - " + logRecord.getSourceClassName() + "] "
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
        } else if (level == Level.WARNING) {
            return ConsoleColors.YELLOW_BOLD;
        }else if(level == Level.SEVERE ){
            return ConsoleColors.RED_BOLD_BRIGHT;
        }
        return ConsoleColors.WHITE;
    }

}
