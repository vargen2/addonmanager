package addonmanager.app.gui.fxapp;

import addonmanager.app.core.Addon;
import addonmanager.app.core.Game;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FXGame extends Game {

    public final ObservableList<Addon> addonObservableList;

    FXGame(String name, String directory, String addonDirectory) {
        super(name, directory, addonDirectory);
        addonObservableList = FXCollections.observableList(addons);
    }

    @Override
    public void addAddon(Addon addon) {
        addonObservableList.add(addon);
    }
}
