package addonmanager.app;

import addonmanager.app.file.FileOperations;
import addonmanager.app.file.Saver;
import addonmanager.app.logging.SingleLineFormatter;
import addonmanager.app.net.DownloadAddon;
import addonmanager.app.net.FindProject;
import addonmanager.app.net.version.DownloadVersions;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Collectors;

public class App implements Serializable {

    public static final List<Level> levels = List.of(Level.OFF, Level.SEVERE, Level.INFO, Level.FINE);
    public static final Logger LOG = Logger.getGlobal();
    private static Handler fileHandler;
    private static final Handler consoleHandler = new ConsoleHandler();
    public static Model model;

    static {
        LogManager.getLogManager().reset();
        try {
            fileHandler = new FileHandler("log.txt");
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.OFF);
            LOG.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        consoleHandler.setFormatter(new SingleLineFormatter());

        LOG.addHandler(consoleHandler);
        LOG.setLevel(Level.FINE);
    }

    public static final Factory DEFAULT_FACTORY = new Factory() {
        @Override
        public Game createGame(String name, String directory, String addonDirectory) {
            return new Game(name, directory, addonDirectory);
        }

        @Override
        public Addon createAddon(Game game, String folderName, String absolutePath) {
            return new Addon(game, folderName, absolutePath);
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
        return factory;
    }

    public static void setReleaseType(Addon addon, Addon.ReleaseType releaseType) {
        if (addon == null)
            return;
        addon.setReleaseType(releaseType);
        Saver.save();
    }

    public static void setReleaseType(Game game, Addon.ReleaseType releaseType) {
        if (game == null)
            return;
        game.getAddons().forEach(x -> x.setReleaseType(releaseType));
        Saver.save();
    }


    //todo movto net.NetOperations net.NetActions??
    public static boolean downLoadVersions(Addon addon) {
        if (addon.getStatus() == Addon.Status.IGNORE)
            return false;
        addon.setStatus(Addon.Status.GETTING_VERSIONS);
        if (addon.getProjectUrl() == null)
            addon.setProjectUrl(FindProject.find(addon));
        LOG.fine("App.downloadversions found: " + addon.getProjectUrl());
        DownloadVersions downloadVersions = DownloadVersions.createDownloadVersion(addon);
        List<Download> downloads = downloadVersions.getDownloads();
        if (downloads.isEmpty()) {
            addon.setDownloads(downloads);
            return false;
        }
        int page = 2;
        while (downloads.stream().noneMatch(x -> x.getRelease().equalsIgnoreCase(Addon.ReleaseType.RELEASE.toString()))) {
            addon.getUpdateable().updateProgress(0.2, 1);
            LOG.info(page + " hit " + addon.getFolderName() + " " + addon.getProjectUrl());

            DownloadVersions moreDownloadversions = DownloadVersions.createDownloadVersion(addon);
            moreDownloadversions.setPage(page);
            downloads.addAll(moreDownloadversions.getDownloads());
            page++;
        }
        addon.getUpdateable().updateProgress(1, 1);
        addon.setDownloads(downloads);
        Saver.save();
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
        Saver.save();
        return true;
    }

    public static void Ignore(Addon addon) {
        if (addon == null)
            return;
        addon.setStatus(Addon.Status.IGNORE);
        Saver.save();
    }

    public static boolean unIgnore(Addon addon) {
        if (addon == null)
            return false;
        addon.setStatus(Addon.Status.NONE);
        return downLoadVersions(addon);
    }

    public static void removeSubFoldersFromGame(Addon addon) {
        if (addon == null || addon.getExtraFolders() == null)
            return;
        var game = addon.getGame();
        var addonsToBeRemoved = game.getAddons().stream()
                .filter(x -> addon.getExtraFolders().stream().
                        anyMatch(y -> y.getName().equals(x.getFolderName())))
                .collect(Collectors.toList());
        addonsToBeRemoved.forEach(x -> LOG.info("Sub folders to be removed " + x.getFolderName()));
        addonsToBeRemoved.forEach(game::removeAddon);
    }
}
