package addonmanager.app.file;

import addonmanager.app.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FileOperations {

    public static List<Game> findGames(Updateable updateable, Consumer<Game> consumer, boolean mustHaveExe) {
        GameFinder gameFinder = new GameFinder(updateable, consumer, mustHaveExe);
        return gameFinder.find();
    }


    public static List<Game> findGames(boolean mustHaveExe) {
        return findGames(Updateable.EMPTY_UPDATEABLE, game -> {
        }, mustHaveExe);
    }


    public static void refreshGameDirectory(Game game) {
        new GameDirectoryRefresher(game).refresh();
        Saver.save();
    }

    public static boolean refreshToc(Addon addon) {
        return new TocRefresher(addon).refresh();
    }

    public static boolean replaceAddon(Addon addon, Download download, File zipFile) {
        return replaceAddon(addon, download, zipFile, 0, 1);
    }

    public static boolean replaceAddon(Addon addon, Download download, File zipFile, double from, double to) {
        if (addon == null || download == null || zipFile == null || !zipFile.exists())
            return false;
        AddonReplacer addonReplacer = new AddonReplacer(addon, download, zipFile);
        return addonReplacer.replace(from, to);
    }

    public static boolean installAddon(Addon addon, CurseAddon curseAddon, Download download, File zipFile, double from, double to, Updateable updateable) {
        if (addon == null || addon.getGame() == null || curseAddon == null || download == null || zipFile == null || !zipFile.exists())
            return false;
        AddonInstaller addonInstaller = new AddonInstaller(addon, curseAddon, download, zipFile);


        List<File> folders = addonInstaller.install(from, to, updateable);

        //todo flytta det som är under  här till App
        if (folders.isEmpty())
            return false;
        refreshGameDirectory(addon.getGame());
        var addons = addon.getGame().getAddons().stream()
                .filter(a -> folders.stream().anyMatch(folder -> a.getFolderName().equalsIgnoreCase(folder.getName())))
                .collect(Collectors.toList());

        addons.forEach(App::downLoadVersions);

        addons.stream().filter(a -> a.getProjectUrl().equalsIgnoreCase(addon.getProjectUrl())).findAny().ifPresent(a -> App.updateAddon(a, a.getLatestDownload(), zipFile));


        addons.forEach(App::removeSubFoldersFromGame);

        return true;
    }

    public static boolean directoriesExists() {
        boolean returnValue = true;
        if (!Files.isDirectory(Paths.get("temp"))) {
            try {
                FileUtils.forceMkdir(new File("temp"));
            } catch (IOException e) {
                e.printStackTrace();
                returnValue = false;
            }
        }
        if (!Files.isDirectory(Paths.get("backup"))) {
            try {
                FileUtils.forceMkdir(new File("backup"));
            } catch (IOException e) {
                e.printStackTrace();
                returnValue = false;
            }
        }
        return returnValue;
    }
}
