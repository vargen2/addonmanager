package addonmanager.app.file;

import addonmanager.app.Addon;
import addonmanager.app.App;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.List;

class TocRefresher {

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
                var liness = Files.readString(tocFile[0].toPath(), charset);
                //liness.lines()
                //var lines = Files.readAllLines(tocFile[0].toPath(), charset);
                liness.lines().filter(line -> line.contains("Interface:")).findAny().ifPresent(line -> addon.setGameVersion(line.substring(line.indexOf("Interface:") + 10).trim()));
                liness.lines().filter(line -> line.contains("Version:")).findAny().ifPresent(line -> addon.setVersion(line.substring(line.indexOf("Version:") + 8).trim()));
                liness.lines().filter(line -> line.contains("Title:")).findAny().ifPresent(line -> addon.setTitle(line.substring(line.indexOf("Title:") + 6).replaceAll("\\|c[a-zA-Z_0-9]{8}", "").replaceAll("\\|r", "").trim()));
                if (addon.getFolderName().equalsIgnoreCase("dbm-core"))
                    return true;
                return liness.lines().noneMatch(line -> line.contains("Dependencies") || line.contains("RequiredDeps"));
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
