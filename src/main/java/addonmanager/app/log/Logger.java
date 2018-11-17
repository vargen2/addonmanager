package addonmanager.app.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public class Logger {
    //public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("Logger");
    private static Handler fileHandler;

    public static void setLogToFile(boolean logToFile) {
        if (fileHandler == null) {
            try {
                fileHandler = new FileHandler("log.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        fileHandler.setLevel(Level.SEVERE);
        if (logToFile)
            java.util.logging.Logger.getGlobal().addHandler(fileHandler);
        else
            java.util.logging.Logger.getGlobal().removeHandler(fileHandler);
    }

}
