package addonmanager.app.file;

import addonmanager.app.Download;
import addonmanager.app.Updateable;
import addonmanager.app.Addon;
import addonmanager.app.Game;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

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
        RefreshGameDirectory.refresh(game);
    }

    public static boolean refreshToc(Addon addon) {
        return new TocRefresher(addon).refresh();
    }

    public static boolean replaceAddon(Addon addon, Download download, File zipFile) {
        return replaceAddon(addon,download, zipFile, 0, 1);
    }

    public static boolean replaceAddon(Addon addon,Download download, File zipFile, double from, double to) {
        if (addon == null||download==null || zipFile == null || !zipFile.exists())
            return false;
        ReplaceAddon replaceAddon = new ReplaceAddon(addon,download, zipFile);
        return replaceAddon.replace(from, to);
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
