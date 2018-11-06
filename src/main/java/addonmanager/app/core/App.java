package addonmanager.app.core;

import addonmanager.Updateable;
import addonmanager.app.core.file.FindGames;
import addonmanager.app.core.file.RefreshGameDirectory;
import addonmanager.app.core.file.RefreshToc;
import addonmanager.app.core.file.ReplaceAddon;
import addonmanager.app.core.net.DownloadAddon;
import addonmanager.app.core.net.FindProject;
import addonmanager.app.core.net.version.DownloadVersions;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class App {

    public static void setReleaseType(Addon addon, Addon.ReleaseType releaseType) {
        if (addon == null)
            return;
        addon.setReleaseType(releaseType);
    }

    public static void setReleaseType(Game game, Addon.ReleaseType releaseType) {
        if (game == null)
            return;
        game.addons.forEach(x -> x.setReleaseType(releaseType));
    }

    public static List<Game> findGames(Updateable updateable, Consumer<Game> consumer, boolean mustHaveExe) {
        FindGames findGames = new FindGames(updateable, consumer, mustHaveExe);
        return findGames.find();
    }

    public static List<Game> findGames(boolean mustHaveExe) {
        return findGames(Updateable.EMPTY_UPDATEABLE, game -> {
        }, mustHaveExe);
    }

    public static void refreshGameDirectory(Game game) {
        RefreshGameDirectory.refresh(game);
    }

    public static boolean downLoadVersions(Addon addon) {
        addon.setStatus(Addon.Status.GETTING_VERSIONS);
        if (addon.getProjectUrl() == null)
            addon.setProjectUrl(FindProject.find(addon));

        DownloadVersions downloadVersions = DownloadVersions.createDownloadVersion(addon);

        List<Download> downloads = downloadVersions.getDownloads();
        if (downloads.isEmpty()) {
            addon.setDownloads(downloads);
            return false;
        }
        int page = 2;
        while (downloads.stream().noneMatch(x -> x.release.equalsIgnoreCase(Addon.ReleaseType.RELEASE.toString()))) {
            DownloadVersions moreDownloadversions = DownloadVersions.createDownloadVersion(addon);
            moreDownloadversions.setPage(page);
            downloads.addAll(moreDownloadversions.getDownloads());
            page++;
        }
        addon.setDownloads(downloads);
        return true;
    }

    public static boolean updateAddon(Addon addon) {
        addon.setStatus(Addon.Status.UPDATING);
        if (!ReplaceAddon.directoriesExists()) {
            addon.setStatus(Addon.Status.NONE);
            return false;
        }
        File zipFile = DownloadAddon.downLoadFile(addon, 0, 0.7);
        if (!zipFile.exists()) {
            addon.setStatus(Addon.Status.NONE);
            return false;
        }

        ReplaceAddon replaceAddon = new ReplaceAddon(addon, zipFile);
        if (!replaceAddon.replace(0.8, 1.0)) {
            addon.setStatus(Addon.Status.NONE);
            return false;
        }
        RefreshToc.refresh(addon);
        addon.setStatus(Addon.Status.UP_TO_DATE);
        return true;
    }

}
