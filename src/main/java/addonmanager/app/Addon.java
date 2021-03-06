package addonmanager.app;

import java.io.File;
import java.io.Serializable;
import java.text.Collator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Addon implements Serializable {

    public static final long serialVersionUID = 73946550836612000L;

    public enum Status {CAN_UPDATE, UPDATING, NOT_SURE, UP_TO_DATE, GETTING_VERSIONS, NONE, IGNORE}

    public enum ReleaseType {
        ALPHA("alpha"),
        BETA("beta"),
        RELEASE("release");

        private final String name;

        ReleaseType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    enum UpdateMode {AUTO, MANUAL}


    private Game game;
    private List<Download> downloads = new ArrayList<>();
    private transient Updateable updateable = Updateable.EMPTY_UPDATEABLE;
    private String folderName;
    private String absolutePath;
    private String projectUrl;
    private UpdateMode updateMode;
    private LocalDateTime dateLastModified;
    private String title;
    private String version;
    private LocalDateTime dateUploaded;
    private String gameVersion;
    private ReleaseType releaseType;
    private Download latestDownload;
    private Download latestUpdate;
    private Status status;
    private List<File> extraFolders;

    protected Addon(Game game, String folderName, String absolutePath) {
        this.game = game;
        this.folderName = folderName;
        this.absolutePath = absolutePath;
        status = Status.NONE;
        releaseType = ReleaseType.RELEASE;
    }

    protected Addon(Game game, Addon addon) {
        this.game = game;
        this.folderName = addon.folderName;
        this.absolutePath = addon.absolutePath;
        this.status = addon.status;
        this.releaseType = addon.releaseType;
        this.projectUrl = addon.projectUrl;
        this.title = addon.title;
        this.version = addon.version;
        this.dateUploaded = addon.dateUploaded;
        this.gameVersion = addon.gameVersion;
        this.latestDownload = addon.latestDownload;
        this.latestUpdate = addon.latestUpdate;
        this.extraFolders = addon.extraFolders;
        this.downloads = addon.downloads;
    }

    public Updateable getUpdateable() {
        return updateable;
    }

    public void setUpdateable(Updateable updateable) {
        this.updateable = updateable;
    }

    public Status getStatus() {
        return status;
    }


    protected void setStatus(Status status) {
        this.status = status;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Download getLatestDownload() {
        return latestDownload;
    }

    protected void setLatestDownload(Download download) {
        latestDownload = download;

    }

    private void updateLatestDownload() {
        if (downloads.isEmpty()) {
            setLatestDownload(null);
            setStatus(Status.NONE);
            return;
        }
        downloads.stream().filter(x -> x.getRelease().equalsIgnoreCase(getReleaseType().name)).findFirst().ifPresent(this::setLatestDownload);
        if (getLatestUpdate() != null && getLatestDownload() != null) {
//            System.out.println(getLatestUpdate().getFileDateUploaded().compareTo(getLatestDownload().getFileDateUploaded()));
//            System.out.println("this: " + getLatestUpdate().getFileDateUploaded());
//            System.out.println("dl: " + getLatestDownload().getFileDateUploaded());
            if (getLatestUpdate().getFileDateUploaded().compareTo(getLatestDownload().getFileDateUploaded()) < 0)
                setStatus(Status.CAN_UPDATE);
            else
                setStatus(Status.UP_TO_DATE);
        } else {

            Collator collator = Collator.getInstance(new Locale("sv", "SE"));
            collator.setStrength(Collator.CANONICAL_DECOMPOSITION);
            if (getLatestDownload() != null && getVersion() != null) {

                if (collator.compare(getLatestDownload().getTitle(), getVersion()) > 0) {
                    setStatus(Status.CAN_UPDATE);

                } else {
                    setStatus(Status.NOT_SURE);
                }
            } else if (getLatestDownload() != null && getVersion() == null) {
                setStatus(Status.CAN_UPDATE);
            }
        }
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public ReleaseType getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(ReleaseType releaseType) {
        this.releaseType = releaseType;
        updateLatestDownload();
    }

    public LocalDateTime getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(LocalDateTime dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public String getFolderName() {
        return folderName;
    }

    public List<Download> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<Download> downloads) {
        this.downloads = downloads;
        updateLatestDownload();
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public Download getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(Download latestUpdate) {
        this.latestUpdate = latestUpdate;
        updateLatestDownload();
    }

    public List<File> getExtraFolders() {
        return extraFolders;
    }

    public void setExtraFolders(List<File> extraFolders) {
        this.extraFolders = extraFolders;
    }

    public Game getGame() {
        return game;
    }


    public String toDetailedString() {
        return "game=" + game + "\n" +
                "downloads=" + downloads.size() + "\n" +
                "folderName=" + folderName + "\n" +
                "absolutePath=" + absolutePath + "\n" +
                "projectUrl=" + projectUrl + "\n" +
                "updateMode=" + updateMode + "\n" +
                "dateLastModified=" + dateLastModified + "\n" +
                "title=" + title + "\n" +
                "version=" + version + "\n" +
                "dateUploaded=" + dateUploaded + "\n" +
                "gameVersion=" + gameVersion + "\n" +
                "releaseType=" + releaseType + "\n" +
                "latestDownload=" + latestDownload + "\n" +
                "latestUpdate=" + latestUpdate + "\n" +
                "status=" + status + "\n" +
                "extraFolders=" + (extraFolders != null ? extraFolders.size() : "");
    }
}
