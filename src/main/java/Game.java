import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Game {

    private String name;
    private String directory;
    private String addonDirectory;
    public final ObservableList<Addon> addons = FXCollections.observableArrayList();

    public Game(String name, String directory, String addonDirectory) {
        this.name = name;
        this.directory = directory;
        this.addonDirectory = addonDirectory;
    }

    public void refresh() {
        File[] directories = new File(directory + addonDirectory).listFiles(File::isDirectory);
        for (var d : directories) {
            if (addons.parallelStream().anyMatch(x -> (x.getFolderName().equals(d.getName()))))
                continue;
            var tocFile = d.listFiles((dir, name) -> name.toLowerCase().endsWith(".toc"));
            if (tocFile == null || tocFile[0] == null)
                continue;
            try {
                var lines = Files.readAllLines(tocFile[0].toPath());
                Addon addon = new Addon(d.getName());
                for (var line : lines) {
                    if (line.contains("Interface:")) {
                        addon.setGameVersion(line.substring(line.indexOf("Interface:") + 10).trim());
                    } else if (line.contains("Version:")) {
                        addon.setVersion(line.substring(line.indexOf("Version:") + 8).trim());
                    } else if (line.contains("Title:")) {
                        addon.setTitle(line.substring(line.indexOf("Title:") + 6).replaceAll("\\|c[a-zA-Z_0-9]{8}", "").replaceAll("\\|r", "").trim());
                    }
                }
                addons.add(addon);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        addons.parallelStream().forEach(addon -> {

        });
    }

    public String getDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return name + " " + directory;
    }
}
