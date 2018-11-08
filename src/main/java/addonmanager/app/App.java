package addonmanager.app;

import addonmanager.app.file.FileOperations;
import addonmanager.app.net.DownloadAddon;
import addonmanager.app.net.FindProject;
import addonmanager.app.net.version.DownloadVersions;

import java.io.File;
import java.util.List;

public class App {

    public static final Factory DEFAULT_FACTORY = new Factory() {
        @Override
        public Game createGame(String name, String directory, String addonDirectory) {
            return new Game(name, directory, addonDirectory);
        }

        @Override
        public Addon createAddon(String folderName, String absolutePath) {
            return new Addon(folderName, absolutePath);
        }

        @Override
        public Model createModel() {
            return new Model();
        }
    };
    private static Factory factory = DEFAULT_FACTORY;

    public static void setFactory(Factory factory) {
        App.factory = factory;
    }

    public static Factory getFactory() {
        return App.factory;
    }

    public static void setReleaseType(Addon addon, Addon.ReleaseType releaseType) {
        if (addon == null)
            return;
        addon.setReleaseType(releaseType);
    }

    public static void setReleaseType(Game game, Addon.ReleaseType releaseType) {
        if (game == null)
            return;
        game.getAddons().forEach(x -> x.setReleaseType(releaseType));
    }


    //todo movto net.NetOperations net.NetActions??
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
        File zipFile = DownloadAddon.downLoadFile(addon, 0, 0.7);
        if (!FileOperations.replaceAddon(addon, zipFile, 0.8, 1.0)) {
            addon.setStatus(Addon.Status.NONE);
            return false;
        }
        FileOperations.refreshToc(addon);
        addon.setStatus(Addon.Status.UP_TO_DATE);
        return true;
    }

}
