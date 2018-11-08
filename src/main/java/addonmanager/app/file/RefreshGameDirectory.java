package addonmanager.app.file;

import addonmanager.app.App;
import addonmanager.app.Game;
import addonmanager.app.Addon;

import java.io.File;

class RefreshGameDirectory {

    static void refresh(Game game) {
        File[] directories = new File(game.getDirectory() + game.getAddonDirectory()).listFiles(File::isDirectory);
        if (directories == null)
            return;

        for (var d : directories) {
            if (game.getAddons().parallelStream().anyMatch(x -> (x.getFolderName().equals(d.getName()))))
                continue;

            Addon addon = App.getFactory().createAddon(d.getName(), d.getPath());
            if (RefreshToc.refresh(addon))
                game.addAddon(addon);
        }
    }
}
