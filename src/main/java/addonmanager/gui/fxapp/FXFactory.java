package addonmanager.gui.fxapp;

import addonmanager.app.*;

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

    @Override
    public Model load(Model model) {
        return new FXModel(model);
    }

    @Override
    public CurseAddon load(CurseAddon curseAddon) {
        return new FXCurseAddon(curseAddon);
    }
}
