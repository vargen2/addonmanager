package addonmanager.app.net;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Download;
import addonmanager.app.net.version.DownloadVersions;

import java.io.File;
import java.util.List;

public class NetOperations {

    public static File downLoadFile(Addon addon, Download download) {
        return downLoadFile(addon, download, 0, 1);
    }

    public static File downLoadFile(Addon addon, Download download, double from, double to) {
        return new AddonDownloader(addon, download).downLoad(from, to);
    }

    public static void findProject(Addon addon) {
        if (addon.getProjectUrl() == null || addon.getProjectUrl().equals("https://www.curseforge.com/wow/addons/"))
            addon.setProjectUrl(new ProjectURLFinder(addon).find());
        App.LOG.fine("App.downloadversions found: " + addon.getProjectUrl());
    }

    public static boolean downLoadVersions(Addon addon) {
        DownloadVersions downloadVersions = DownloadVersions.createDownloadVersion(addon);
        List<Download> downloads = downloadVersions.getDownloads();
        if (downloads.isEmpty()) {
            addon.setDownloads(downloads);
            return false;
        }
        int page = 2;
        while (downloads.stream().noneMatch(x -> x.getRelease().equalsIgnoreCase(Addon.ReleaseType.RELEASE.toString()))) {
            addon.getUpdateable().updateProgress(0.2, 1);
            App.LOG.info(page + " hit " + addon.getFolderName() + " " + addon.getProjectUrl());
            DownloadVersions moreDownloadversions = DownloadVersions.createDownloadVersion(addon);
            moreDownloadversions.setPage(page);
            downloads.addAll(moreDownloadversions.getDownloads());
            page++;
        }
        addon.getUpdateable().updateProgress(1, 1);
        addon.setDownloads(downloads);
        return true;
    }
}
