package addonmanager.app.core.file;

import addonmanager.Updateable;
import addonmanager.app.core.Addon;
import addonmanager.app.core.Game;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public class FileOperations {

    public static List<Game> findGames(Updateable updateable, Consumer<Game> consumer, boolean mustHaveExe) {
        FindGames findGames = new FindGames(updateable, consumer, mustHaveExe);
        return findGames.find();
    }


    public static List<Game> findGames(boolean mustHaveExe) {
        return findGames(Updateable.EMPTY_UPDATEABLE, game -> {
        }, mustHaveExe);
    }


    public static void refreshGameDirectory(Game game) {
        RefreshGameDirectory.refresh(game);
    }

    public static boolean refreshToc(Addon addon) {
        return RefreshToc.refresh(addon);
    }

    public static boolean replaceAddon(Addon addon, File zipFile) {
        return replaceAddon(addon, zipFile, 0, 1);
    }

    public static boolean replaceAddon(Addon addon, File zipFile, double from, double to) {
        if (addon == null || zipFile == null || !zipFile.exists())
            return false;
        ReplaceAddon replaceAddon = new ReplaceAddon(addon, zipFile);
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
