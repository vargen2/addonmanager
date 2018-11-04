package addonmanager.net.version;

import addonmanager.core.Addon;
import addonmanager.core.Download;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class DownloadVersions {

    public static DownloadVersions createDownloadVersion(Addon addon, Consumer<String> updateMessage, BiConsumer<Double, Double> updateProgress) {
        if (addon.getProjectUrl().contains("https://wow.curseforge.com/projects/")) {

            return new WowCurseForge(addon, updateMessage, updateProgress);

        }else if (addon.getProjectUrl().contains("https://www.wowace.com/projects/")) {

            return new WowAce(addon, updateMessage, updateProgress);

        }

        return new WwwCurseForge(addon, updateMessage, updateProgress);

    }

    protected Addon addon;
    protected Consumer<String> updateMessage;
    protected BiConsumer<Double, Double> updateProgress;
    protected int page;

    public DownloadVersions(Addon addon, Consumer<String> updateMessage, BiConsumer<Double, Double> updateProgress) {
        this.addon = addon;
        this.updateMessage = updateMessage;
        this.updateProgress = updateProgress;
    }

    public abstract List<Download> getDownloads();

    public Addon getAddon() {
        return addon;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
