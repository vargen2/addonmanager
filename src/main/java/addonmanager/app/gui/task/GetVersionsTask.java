package addonmanager.app.gui.task;

import addonmanager.Updateable;
import addonmanager.app.core.Addon;
import addonmanager.app.core.App;
import addonmanager.app.core.Download;
import addonmanager.app.core.net.FindProject;
import addonmanager.app.core.net.version.DownloadVersions;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class GetVersionsTask extends Task<Void> {

    private Addon addon;

    public GetVersionsTask(Addon addon) {
        super();
        this.addon = addon;
//        setOnScheduled(x -> {
//            updateMessage("connecting...");
//            addon.setNewVersionsTask(this);
//        });
//        setOnSucceeded(x -> {
//            updateMessage("done");
//            updateProgress(1, 1);
//        });
//        setOnCancelled();
//        setOnFailed();
    }

    @Override
    protected Void call() throws Exception {
        Updateable updateable = Updateable.createUpdateable(this,this::updateMessage, this::updateProgress);
        addon.setUpdateable(updateable);

        if(!App.downLoadVersions(addon)){
            cancel();
            return null;
        }

//        List<Download> downloads = new ArrayList<>();
//
//        if (addon.getProjectUrl() == null)
//            addon.setProjectUrl(FindProject.find(addon));

//
//        DownloadVersions downloadVersions = DownloadVersions.createDownloadVersion(addon, this::updateMessage, this::updateProgress);
//
//        downloads = downloadVersions.getDownloads();
//        if (downloads.isEmpty())
//            return downloads;
//        int page = 2;
//        while (downloads.stream().noneMatch(x -> x.release.equalsIgnoreCase(Addon.ReleaseType.RELEASE.toString()))) {
//            DownloadVersions moreDownloadversions = DownloadVersions.createDownloadVersion(addon, this::updateMessage, this::updateProgress);
//            moreDownloadversions.setPage(page);
//            downloads.addAll(moreDownloadversions.getDownloads());
//            page++;
//        }
//        return downloads;
        return null;
    }




}
