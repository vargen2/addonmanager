package addonmanager.app.task;

import addonmanager.Updateable;
import addonmanager.core.Addon;
import addonmanager.file.ReplaceAddon;
import addonmanager.net.DownloadAddon;
import javafx.concurrent.Task;

import java.io.File;

public class ReplaceAddonTask extends Task<Void> {

    private Addon addon;

    public ReplaceAddonTask(Addon addon) {
        super();
        this.addon = addon;
        setOnScheduled(x -> {
            updateProgress(0, 1);
            updateMessage("initializing...");
            addon.setReplaceAddonTask(this);
        });
        setOnCancelled(event -> {
            updateMessage("canceled");
            addon.setStatus(Addon.Status.NONE);
        });

        setOnSucceeded(x -> {
            updateMessage("done");
            updateProgress(1, 1);
            addon.refreshToc();
            addon.setStatus(Addon.Status.UP_TO_DATE);
        });
    }

    @Override
    protected Void call() {
        if (!ReplaceAddon.directoriesExists())
            return end();

        Updateable updateable = Updateable.createUpdateable(this::updateMessage, this::updateProgress);
        File zipFile = DownloadAddon.downLoadFile(addon, 0.7, updateable);
        if (!zipFile.exists())
            return end();

        ReplaceAddon replaceAddon = new ReplaceAddon(addon, zipFile);
        if (!replaceAddon.replace(updateable))
            return end();

        updateProgress(1, 1);
        return null;
    }

    private Void end() {
        cancel();
        return null;
    }
}
