package addonmanager.app;

import java.util.logging.Level;

public class AppSettings implements Settings {

    private Level consoleLevel;
    private Level fileLevel;

    public AppSettings(Level consoleLevel, Level fileLevel) {
        this.consoleLevel = consoleLevel;
        this.fileLevel = fileLevel;
    }

    public void load(String load) {
        if (load == null || load.isEmpty())
            return;

        load.lines().filter(x -> x.contains("consolelevel")).findAny().ifPresent(s -> consoleLevel = Level.parse(s.replaceAll("consolelevel", "").strip().toUpperCase()));
        load.lines().filter(x -> x.contains("filelevel")).findAny().ifPresent(s -> fileLevel = Level.parse(s.replaceAll("filelevel", "").strip().toUpperCase()));

    }

    @Override
    public String save() {
        return "consolelevel " + consoleLevel.toString() + Util.LINE +
                "filelevel " + fileLevel.toString() + Util.LINE;
    }

    public Level getConsoleLevel() {
        return consoleLevel;
    }

    public void setConsoleLevel(Level consoleLevel) {
        this.consoleLevel = consoleLevel;
    }

    public Level getFileLevel() {
        return fileLevel;
    }

    public void setFileLevel(Level fileLevel) {
        this.fileLevel = fileLevel;
    }
}
