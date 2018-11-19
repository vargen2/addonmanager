package addonmanager.gui.fxapp;

import addonmanager.app.Addon;
import addonmanager.app.Game;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FXGame extends Game {

    public final ObservableList<Addon> addonObservableList;

    FXGame(String name, String directory, String addonDirectory) {
        super(name, directory, addonDirectory);
        addonObservableList = FXCollections.observableList(addons);
    }

    FXGame(Game game) {
        this(game.getName(), game.getDirectory(), game.getAddonDirectory());
        game.getAddons().forEach(x -> addAddon(new FXAddon(this, x)));
    }

    @Override
    public void addAddon(Addon addon) {
        addonObservableList.add(addon);
    }

    @Override
    public void removeAddon(Addon addon) {
        addonObservableList.remove(addon);
    }
}
