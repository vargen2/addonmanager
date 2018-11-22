package addonmanager.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game implements Serializable {

    public static final long serialVersionUID = 73946550836612022L;

    private final String name;
    private final String directory;
    private final String addonDirectory;
    protected final List<Addon> addons = new ArrayList<>();

    protected Game(String name, String directory, String addonDirectory) {
        this.name = name;
        this.directory = directory;
        this.addonDirectory = addonDirectory;
    }

//    Game(Game game) {
//        this(game.name, game.directory, game.addonDirectory);
//        game.getAddons().stream().forEach(x -> addons.add(new Addon(this, x)));
//    }

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

    public void removeAddon(Addon addon) {
        addons.remove(addon);
    }


    @Override
    public String toString() {
        return directory;
    }
}
