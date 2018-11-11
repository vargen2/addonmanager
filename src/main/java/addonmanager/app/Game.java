package addonmanager.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private final String name;
    private final String directory;
    private final String addonDirectory;
    protected final List<Addon> addons = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public List<Addon> getAddons() {
        return Collections.unmodifiableList(addons);
    }

    public void addAddon(Addon addon) {
        addons.add(addon);
    }

    @Override
    public String toString() {
        return name + " " + directory;
    }
}