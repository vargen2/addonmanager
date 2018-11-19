package addonmanager.app.logging;


import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter {


    @Override
    public String format(LogRecord record) {
        //String message = formatMessage(record);

        return record.getLevel() + " " + record.getMessage() + "\n";
    }
}
