package addonmanager.app.net;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.CurseAddon;
import addonmanager.app.Download;
import addonmanager.app.net.version.VersionDownloader;

import java.io.File;
import java.util.List;

public class NetOperations {

    public static File downLoadFile(Addon addon, Download download) {
        return downLoadFile(addon, download, 0, 1);
    }

    public static File downLoadFile(Addon addon, Download download, double from, double to) {
        return new AddonDownloader(addon, download).downLoad(from, to);
    }

    public static String findProject(CurseAddon curseAddon) {
        return ProjectURLFinder.find(curseAddon);
    }

    public static void findProject(Addon addon) {
        if (addon.getProjectUrl() == null || addon.getProjectUrl().equals("https://www.curseforge.com/wow/addons/"))
            addon.setProjectUrl(new ProjectURLFinder(addon).find());
        //App.LOG.fine("NetfindProject(Addon addon) {: " + addon.getProjectUrl());
    }

    public static boolean downLoadVersions(Addon addon, double from, double to) {
        addon.getUpdateable().updateProgress(from + (to - from) * 0.0, 1);
        List<Download> downloads = VersionDownloader.create(addon).getDownloads();
        if (downloads.isEmpty()) {
            addon.setDownloads(downloads);
            addon.getUpdateable().updateProgress(from + (to - from) * 1.0, 1);
            return false;
        }
        addon.getUpdateable().updateProgress(from + (to - from) * 0.5, 1);
        int page = 2;
        while (downloads.stream().noneMatch(x -> x.getRelease().equalsIgnoreCase(Addon.ReleaseType.RELEASE.toString()))) {

            App.LOG.info(page + " hit " + addon.getFolderName() + " " + addon.getProjectUrl());
            VersionDownloader versionDownloader = VersionDownloader.create(addon);
            versionDownloader.setPage(page);
            downloads.addAll(versionDownloader.getDownloads());
            page++;
        }
        addon.getUpdateable().updateProgress(from + (to - from) * 1.0, 1);
        addon.setDownloads(downloads);
        return true;
    }

    public static boolean downLoadVersions(Addon addon) {
        return downLoadVersions(addon, 0, 1);
    }


}
