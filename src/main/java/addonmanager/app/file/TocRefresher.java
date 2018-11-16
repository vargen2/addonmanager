package addonmanager.app.file;

import addonmanager.app.Addon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.List;

class TocRefresher {

    private static final List<Charset> CHARSETS = List.of(Charset.defaultCharset(), Charset.forName("ISO-8859-1"));
    private Addon addon;

    public TocRefresher(Addon addon) {
        this.addon = addon;
    }

    boolean refresh() {
        var tocFile = new File(addon.getAbsolutePath()).listFiles((dir, name) -> name.toLowerCase().endsWith(".toc"));
        if (tocFile == null || tocFile[0] == null)
            return false;

        for (var charset : CHARSETS) {
            try {
                var lines = Files.readAllLines(tocFile[0].toPath(), charset);
                lines.stream().filter(line -> line.contains("Interface:")).findAny().ifPresent(line -> addon.setGameVersion(line.substring(line.indexOf("Interface:") + 10).trim()));
                lines.stream().filter(line -> line.contains("Version:")).findAny().ifPresent(line -> addon.setVersion(line.substring(line.indexOf("Version:") + 8).trim()));
                lines.stream().filter(line -> line.contains("Title:")).findAny().ifPresent(line -> addon.setTitle(line.substring(line.indexOf("Title:") + 6).replaceAll("\\|c[a-zA-Z_0-9]{8}", "").replaceAll("\\|r", "").trim()));
                return !lines.stream().anyMatch(line -> line.contains("Dependencies") || line.contains("RequiredDeps"));
            } catch (MalformedInputException e) {
                System.err.println(addon.getFolderName() + " " + e.getMessage());
            } catch (IOException e) {
                System.err.println(addon.getFolderName() + " " + e.getMessage());
                return false;
            }
        }
        return false;
    }
}