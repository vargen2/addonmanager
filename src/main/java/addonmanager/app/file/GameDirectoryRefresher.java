package addonmanager.app.file;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Game;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

class GameDirectoryRefresher {

    private Game game;

    GameDirectoryRefresher(Game game) {
        this.game = game;
    }

    void refresh() {
        File[] directories = new File(game.getDirectory() + game.getAddonDirectory()).listFiles(File::isDirectory);
        if (directories == null)
            return;

        var subFolders = game.getAddons().stream().filter(x -> x.getExtraFolders() != null && !x.getExtraFolders().isEmpty()).flatMap(x -> x.getExtraFolders().stream()).collect(Collectors.toList());
        subFolders.forEach(x -> App.LOG.fine("all subdirectory folders: " + x.getName()));

        for (var d : directories) {
            if (game.getAddons().parallelStream().anyMatch(x -> (x.getFolderName().equals(d.getName()))))
                continue;
            if (subFolders.stream().anyMatch(x -> x.getName().equals(d.getName())))
                continue;

            Addon addon = App.getFactory().createAddon(game, d.getName(), d.getPath());
            if (new TocRefresher(addon).refresh())
                game.addAddon(addon);

        }

        var addons = new ArrayList<>(game.getAddons());
        for (var a : addons) {
            if (Arrays.stream(directories).noneMatch(d -> d.getName().equalsIgnoreCase(a.getFolderName()))) {
                game.removeAddon(a);
            }
        }
    }
}
