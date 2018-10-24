package addonmanager.core;

import addonmanager.net.GetVersionsTask;

public class Status {
    private String folderName;
    private GetVersionsTask getVersionsTask;
    private String latestVersion;
    private Download download;

    public GetVersionsTask getNewVersionsTask() {
        return getVersionsTask;
    }

    public void setNewVersionsTask(GetVersionsTask getVersionsTask) {
        this.getVersionsTask = getVersionsTask;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
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
