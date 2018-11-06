package addonmanager.app.core;

import addonmanager.app.core.file.ReplaceAddon;
import addonmanager.app.core.net.DownloadAddon;

import java.io.File;

public class App {

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
