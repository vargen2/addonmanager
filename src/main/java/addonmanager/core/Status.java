package addonmanager.core;

import addonmanager.net.GetVersionsTask;

public class Status {
    private String folderName;
    private GetVersionsTask getVersionsTask;

    private Download download;

    public GetVersionsTask getNewVersionsTask() {
        return getVersionsTask;
    }

    public void setNewVersionsTask(GetVersionsTask getVersionsTask) {
        this.getVersionsTask = getVersionsTask;
    }



    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        this.download = download;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
