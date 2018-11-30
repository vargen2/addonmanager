package addonmanager.app;

import addonmanager.app.file.FileOperations;
import addonmanager.app.file.Saver;
import addonmanager.app.logging.SingleLineFormatter;
import addonmanager.app.net.NetOperations;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Collectors;

public class App {

    public static final List<Level> levels = List.of(Level.OFF, Level.SEVERE, Level.INFO, Level.FINE);
    public static final Logger LOG = Logger.getGlobal();
    private static Handler fileHandler;
    private static final Handler consoleHandler = new ConsoleHandler();
    public static Model model;
    public static AppSettings appSettings;
    public static List<CurseAddon> curseAddons;

    static {
        LogManager.getLogManager().reset();
        try {
            fileHandler = new FileHandler("log.txt");
            fileHandler.setFormatter(new SimpleFormatter());
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

        @Override
        public Model load(Model model) {
            return model;
        }

        @Override
        public CurseAddon load(CurseAddon curseAddon) {
            return curseAddon;
        }
    };
    private static Factory factory = DEFAULT_FACTORY;

    public static void init(AppSettings appSettings) {
        App.appSettings = appSettings;
        App.consoleHandler.setLevel(appSettings.getConsoleLevel());
        App.fileHandler.setLevel(appSettings.getFileLevel());
    }

    public static void setFileLoggingLevel(Level level) {
        if (fileHandler != null)
            fileHandler.setLevel(level);
        App.appSettings.setFileLevel(level);
        Saver.saveSettings();
    }

    public static void setConsoleLoggingLevel(Level level) {
        consoleHandler.setLevel(level);
        App.appSettings.setConsoleLevel(level);
        Saver.saveSettings();
    }

    public static Level getFileLoggingLevel() {
        return (fileHandler != null) ? fileHandler.getLevel() : App.appSettings.getFileLevel();
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


    public static boolean downLoadVersions(Addon addon) {
        if (addon.getStatus() == Addon.Status.IGNORE)
            return false;
        addon.setStatus(Addon.Status.GETTING_VERSIONS);
        NetOperations.findProject(addon);
        boolean downloaded = NetOperations.downLoadVersions(addon, 0, 1);
        if (downloaded) Saver.save();
        return downloaded;
    }

    public static boolean updateAddon(Addon addon, Download download, File zipFile) {
        if (addon == null || download == null)
            return false;
        addon.setStatus(Addon.Status.UPDATING);
        if (zipFile == null)
            zipFile = NetOperations.downLoadFile(addon, download, 0, 0.7);
        if (!FileOperations.replaceAddon(addon, download, zipFile, 0.8, 1.0)) {
            addon.setStatus(Addon.Status.NONE);
            return false;
        }
        FileOperations.refreshToc(addon);
        Saver.save();
        return true;
    }

    public static boolean updateAddon(Addon addon, Download download) {
        return updateAddon(addon, download, null);
    }

    public static boolean installAddon(Game game, CurseAddon curseAddon, Updateable updateable) {
        updateable.updateProgress(0, 1);
        System.out.println("hit1");
        String projectUrl = NetOperations.findProject(curseAddon);
        if (projectUrl.isEmpty())
            return false;
        System.out.println("hit2");
        updateable.updateProgress(0.1, 1);
        Addon addon = App.getFactory().createAddon(game, curseAddon.getAddonURL(), "");
        addon.setProjectUrl(projectUrl);
        addon.setUpdateable(updateable);
        System.out.println("hit3");
        NetOperations.downLoadVersions(addon, 0.1, 0.3);
        System.out.println("hit4");
        File zipFile = NetOperations.downLoadFile(addon, addon.getLatestDownload(), 0.3, 0.7);
        System.out.println("hit5");
        boolean installed = FileOperations.installAddon(addon, curseAddon, addon.getLatestDownload(), zipFile, 0.7, 0.9, updateable);
        updateable.updateProgress(1, 1);
        if (installed)
            Saver.save();
        return installed;
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
        Saver.save();
    }

    public static void setSelectedGame(Game game) {
        App.model.setSelectedGame(game);
        Saver.save();
    }

    public static boolean addGame(Game game) {
        boolean added = App.model.addGame(game);
        if (added) Saver.save();
        return added;
    }

    public static boolean removeGame(Game game) {
        boolean removed = App.model.removeGame(game);
        if (removed) Saver.save();
        return removed;
    }
}
