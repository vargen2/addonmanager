package addonmanager.app.core.file;

import addonmanager.app.core.Addon;
import addonmanager.app.core.Game;

import java.io.File;

class RefreshGameDirectory {

    static void refresh(Game game) {
        File[] directories = new File(game.getDirectory() + game.getAddonDirectory()).listFiles(File::isDirectory);
        if (directories == null)
            return;

        for (var d : directories) {
            if (game.addons.parallelStream().anyMatch(x -> (x.getFolderName().equals(d.getName()))))
                continue;

            Addon addon = new Addon(d.getName(), d.getPath());
            if (RefreshToc.refresh(addon))
                game.addons.add(addon);
        }
    }
}
