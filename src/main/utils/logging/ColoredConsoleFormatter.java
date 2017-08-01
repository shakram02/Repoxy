package utils.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ColoredConsoleFormatter extends Formatter {


    @Override
    public String format(LogRecord logRecord) {

        return ConsoleColors.RED_BRIGHT + "XX" + logRecord.getMessage() + " " + logRecord.getSourceMethodName()
                + " \n" + ConsoleColors.RESET;
    }


}
