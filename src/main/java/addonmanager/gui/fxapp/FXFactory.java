package addonmanager.gui.fxapp;

import addonmanager.app.*;

public class FXFactory implements Factory {
    @Override
    public Game createGame(String name, String directory, String addonDirectory) {
        return new FXGame(name, directory, addonDirectory);
    }

    @Override
    public Addon createAddon(String folderName, String absolutePath) {
        return App.DEFAULT_FACTORY.createAddon(folderName, absolutePath);
    }

    @Override
    public Model createModel() {
        return new FXModel();
    }
}
