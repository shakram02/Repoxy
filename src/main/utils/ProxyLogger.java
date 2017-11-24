package utils;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ProxyLogger extends Formatter {

    @Override
    public String format(LogRecord logRecord) {
        Level l = logRecord.getLevel();
        return null;
    }
}

class EntryContent extends Level {
//    public static final EntryContent OF_PACKET = new EntryContent("PACKET",);

    protected EntryContent(String s, int i) {
        super(s, i);
    }
}