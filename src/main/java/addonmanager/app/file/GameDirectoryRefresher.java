package addonmanager.app.file;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Game;

import java.io.File;

class GameDirectoryRefresher {

    private Game game;

    GameDirectoryRefresher(Game game) {
        this.game = game;
    }

    void refresh() {
        File[] directories = new File(game.getDirectory() + game.getAddonDirectory()).listFiles(File::isDirectory);
        if (directories == null)
            return;

        for (var d : directories) {
            if (game.getAddons().parallelStream().anyMatch(x -> (x.getFolderName().equals(d.getName()))))
                continue;

            Addon addon = App.getFactory().createAddon(game, d.getName(), d.getPath());
            if (new TocRefresher(addon).refresh())
                game.addAddon(addon);
        }
    }
}
