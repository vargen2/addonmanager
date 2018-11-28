package addonmanager.app.file;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TocRefresher {

    private static final Path KNOWN = Path.of("knownsubfolders.txt");
    private static final List<String> knownSubFolders = new ArrayList<>();

    public static void loadKnownSubFolders() {
        if (Files.notExists(KNOWN))
            return;
        try {
            knownSubFolders.addAll(Files.readString(KNOWN).lines().collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createKnownSubFolders() {
        var game = App.model.getSelectedGame();
        String saveString = game.getAddons().stream()
                .filter(addon -> addon.getExtraFolders() != null)
                .map(Addon::getExtraFolders)
                .flatMap(Collection::stream)
                .map(file -> file.getName() + Util.LINE)
                .collect(Collectors.joining());
        try {
            Files.writeString(KNOWN, saveString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final List<Charset> CHARSETS = List.of(Charset.defaultCharset(), Charset.forName("ISO-8859-1"));
    private Addon addon;

    TocRefresher(Addon addon) {
        this.addon = addon;
    }

    boolean refresh() {
        var tocFile = new File(addon.getAbsolutePath()).listFiles((dir, name) -> name.toLowerCase().endsWith(".toc"));
        if (tocFile == null || tocFile[0] == null)
            return false;

        for (var charset : CHARSETS) {
            try {
                var tocString = Files.readString(tocFile[0].toPath(), charset);
                tocString.lines().filter(line -> line.contains("Interface:")).findAny().ifPresent(line -> addon.setGameVersion(line.substring(line.indexOf("Interface:") + 10).trim()));
                tocString.lines().filter(line -> line.contains("Version:")).findAny().ifPresent(line -> addon.setVersion(line.substring(line.indexOf("Version:") + 8).trim()));
                tocString.lines().filter(line -> line.contains("Title:")).findAny().ifPresent(line -> addon.setTitle(line.substring(line.indexOf("Title:") + 6).replaceAll("\\|c[a-zA-Z_0-9]{8}", "").replaceAll("\\|r", "").trim()));
                return knownSubFolders.stream().noneMatch(folder -> folder.equalsIgnoreCase(addon.getFolderName()));
            } catch (MalformedInputException e) {
                App.LOG.info(addon.getFolderName() + " " + e.getMessage());
            } catch (IOException e) {
                App.LOG.info(addon.getFolderName() + " " + e.getMessage());
                return false;
            }
        }
        return false;
    }
}
