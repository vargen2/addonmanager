package addonmanager.app.core;

import addonmanager.app.core.file.ReplaceAddon;
import addonmanager.app.core.net.DownloadAddon;
import addonmanager.app.core.net.FindProject;
import addonmanager.app.core.net.version.DownloadVersions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static boolean downLoadVersions(Addon addon) {
        addon.setStatus(Addon.Status.GETTING_VERSIONS);
        if (addon.getProjectUrl() == null)
            addon.setProjectUrl(FindProject.find(addon));

        DownloadVersions downloadVersions = DownloadVersions.createDownloadVersion(addon, addon.getUpdateable()::updateMessage, addon.getUpdateable()::updateProgress);

        List<Download> downloads = downloadVersions.getDownloads();
        if (downloads.isEmpty()) {
            addon.setDownloads(downloads);
            return false;
        }
        int page = 2;
        while (downloads.stream().noneMatch(x -> x.release.equalsIgnoreCase(Addon.ReleaseType.RELEASE.toString()))) {
            DownloadVersions moreDownloadversions = DownloadVersions.createDownloadVersion(addon, addon.getUpdateable()::updateMessage, addon.getUpdateable()::updateProgress);
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
        addon.refreshToc();
        addon.setStatus(Addon.Status.UP_TO_DATE);
        return true;
    }

}
