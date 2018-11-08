package addonmanager.app.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Game {

    private String name;
    private String directory;
    private String addonDirectory;
    public final ObservableList<Addon> addons = FXCollections.observableArrayList();

    protected Game(String name, String directory, String addonDirectory) {
        this.name = name;
        this.directory = directory;
        this.addonDirectory = addonDirectory;
    }

    public String getDirectory() {
        return directory;
    }

    public String getAddonDirectory() {
        return addonDirectory;
    }

    @Override
    public String toString() {
        return name + " " + directory;
    }
}
