public class VersionCellData {
    private NewVersionsTask newVersionsTask;
    private String latestVersion;
    private Download download;

    public NewVersionsTask getNewVersionsTask() {
        return newVersionsTask;
    }

    public void setNewVersionsTask(NewVersionsTask newVersionsTask) {
        this.newVersionsTask = newVersionsTask;
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
}
