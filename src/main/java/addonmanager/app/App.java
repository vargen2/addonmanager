package addonmanager.app;

import addonmanager.app.file.FileOperations;
import addonmanager.app.net.DownloadAddon;
import addonmanager.app.net.FindProject;
import addonmanager.app.net.version.DownloadVersions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.*;

public class App {

    //public enum LogLevel {OFF,INFO,SEVERE}
    public static List<Level> levels= List.of(Level.OFF,Level.INFO,Level.SEVERE);
    public static final Logger LOGGER = Logger.getGlobal();
    private static Handler fileHandler;
    private static Handler consoleHandler = new ConsoleHandler();

    static {
        LogManager.getLogManager().reset();
        try {
            fileHandler = new FileHandler("log.txt");
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.addHandler(consoleHandler);
    }

    public static final Factory DEFAULT_FACTORY = new Factory() {
        @Override
        public Game createGame(String name, String directory, String addonDirectory) {
            return new Game(name, directory, addonDirectory);
        }

        @Override
        public Addon createAddon(String folderName, String absolutePath) {
            return new Addon(folderName, absolutePath);
        }

        @Override
        public Model createModel() {
            return new Model();
        }
    };
    private static Factory factory = DEFAULT_FACTORY;

    public static void setFileLoggingLevel(Level level) {
        if (fileHandler != null) fileHandler.setLevel(level);
    }

    public static void setConsoleLoggingLevel(Level level) {
        consoleHandler.setLevel(level);
    }

    public static Level getFileLoggingLevel() {
        return (fileHandler != null) ? fileHandler.getLevel() : Level.OFF;
    }

    public static Level getConsoleLoggingLevel() {
        return consoleHandler.getLevel();
    }

    public static void setFactory(Factory factory) {
        App.factory = factory;
    }

    public static Factory getFactory() {
        return App.factory;
    }

    public static void setReleaseType(Addon addon, Addon.ReleaseType releaseType) {
        if (addon == null)
            return;
        addon.setReleaseType(releaseType);
    }

    public static void setReleaseType(Game game, Addon.ReleaseType releaseType) {
        if (game == null)
            return;
        game.getAddons().forEach(x -> x.setReleaseType(releaseType));
    }


    //todo movto net.NetOperations net.NetActions??
    public static boolean downLoadVersions(Addon addon) {
        if (addon.getStatus() == Addon.Status.IGNORE)
            return false;
        addon.setStatus(Addon.Status.GETTING_VERSIONS);
        if (addon.getProjectUrl() == null)
            addon.setProjectUrl(FindProject.find(addon));
        DownloadVersions downloadVersions = DownloadVersions.createDownloadVersion(addon);
        List<Download> downloads = downloadVersions.getDownloads();
        if (downloads.isEmpty()) {
            addon.setDownloads(downloads);
            return false;
        }
        int page = 2;
        while (downloads.stream().noneMatch(x -> x.getRelease().equalsIgnoreCase(Addon.ReleaseType.RELEASE.toString()))) {
            DownloadVersions moreDownloadversions = DownloadVersions.createDownloadVersion(addon);
            moreDownloadversions.setPage(page);
            downloads.addAll(moreDownloadversions.getDownloads());
            page++;
        }
        addon.setDownloads(downloads);
        return true;
    }


    public static boolean updateAddon(Addon addon, Download download) {
        if (addon == null || download == null)
            return false;
        addon.setStatus(Addon.Status.UPDATING);
        File zipFile = DownloadAddon.downLoadFile(addon, download, 0, 0.7);
        if (!FileOperations.replaceAddon(addon, download, zipFile, 0.8, 1.0)) {
            addon.setStatus(Addon.Status.NONE);
            return false;
        }
        FileOperations.refreshToc(addon);
        //addon.setStatus(Addon.Status.UP_TO_DATE);
        return true;
    }

    public static void Ignore(Addon addon) {
        if (addon == null)
            return;
        addon.setStatus(Addon.Status.IGNORE);
    }

    public static boolean unIgnore(Addon addon) {
        if (addon == null)
            return false;
        addon.setStatus(Addon.Status.NONE);
        return App.downLoadVersions(addon);
    }

}
