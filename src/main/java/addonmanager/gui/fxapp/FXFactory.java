package addonmanager.gui.fxapp;

import addonmanager.app.Addon;
import addonmanager.app.Factory;
import addonmanager.app.Game;
import addonmanager.app.Model;

public class FXFactory implements Factory {
    @Override
    public Game createGame(String name, String directory, String addonDirectory) {
        return new FXGame(name, directory, addonDirectory);
    }

    @Override
    public Addon createAddon(Game game, String folderName, String absolutePath) {
        return new FXAddon(game, folderName, absolutePath);
    }

    @Override
    public Model createModel() {
        return new FXModel();
    }

    public Model load(Model model) {
        return new FXModel(model);
    }
}
