package addonmanager.app.net.version;

import addonmanager.app.Addon;
import addonmanager.app.Download;

import java.util.List;

public abstract class VersionDownloader {

    public static VersionDownloader create(Addon addon) {
        if (addon.getProjectUrl().contains("https://wow.curseforge.com/projects/")) {

            return new WowCurseForge(addon);

        }else if (addon.getProjectUrl().contains("https://www.wowace.com/projects/")) {

            return new WowAce(addon);

        }

        return new WwwCurseForge(addon);

    }

    protected Addon addon;
    protected int page;

    public VersionDownloader(Addon addon) {
        this.addon = addon;
    }

    public abstract List<Download> getDownloads();

    public Addon getAddon() {
        return addon;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
