package addonmanager.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.Collator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Addon {

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

    private List<Download> downloads = new ArrayList<>();
    private String folderName;
    private UpdateMode updateMode;
    private LocalDateTime dateLastModified;
    private ObjectProperty<ReleaseType> releaseType;
    private StringProperty title;
    private StringProperty version;
    private StringProperty latestVersion;
    private LocalDateTime dateUploaded;
    private StringProperty gameVersion;
    private StringProperty titleVersion;
    private final ObjectProperty<Status> status;

    public Addon(String folderName) {
        this.folderName = folderName;
        this.status = new SimpleObjectProperty<>(this, "status");
        setReleaseType(ReleaseType.RELEASE);
        updateLatestVersion();
    }

    public Status getStatus() {
        return statusProperty().get();
    }

    public ObjectProperty<Status> statusProperty() {

        return status;
    }

    public void setStatus(Status status) {
        statusProperty().set(status);
    }


    private void updateTitleVersion() {
        titleVersionProperty().setValue(titleProperty().get() + ((versionProperty().get() != null) ? "\n" + versionProperty().get() : ""));
    }

    public String getTitleVersion() {
        return titleVersionProperty().get();
    }

    public StringProperty titleVersionProperty() {
        if (titleVersion == null)
            titleVersion = new SimpleStringProperty(this, "titleVersion");
        return titleVersion;
    }

    public void setTitle(String value) {
        titleProperty().set(value);
        updateTitleVersion();
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public StringProperty titleProperty() {
        if (title == null)
            title = new SimpleStringProperty(this, "title");
        return title;
    }

    public String getVersion() {
        return versionProperty().get();
    }

    public void setVersion(String version) {
        this.versionProperty().set(version);
        updateTitleVersion();
    }

    public StringProperty versionProperty() {
        if (version == null)
            version = new SimpleStringProperty(this, "version");
        return version;
    }

    public String getLatestVersion() {
        return latestVersionProperty().get();
    }

    public void setLatestVersion(String version) {
        this.latestVersionProperty().set(version);
        Collator collator = Collator.getInstance(new Locale("sv", "SE"));
        collator.setStrength(Collator.CANONICAL_DECOMPOSITION);
        if (getLatestVersion() != null && getVersion() != null) {
            System.out.println(getTitle() + " " + collator.compare(getLatestVersion(), getVersion()));
            if(collator.compare(getLatestVersion(), getVersion()) > 0){
                Status newStatus=new Status();
                newStatus.setDownload(downloads.get(0));
                setStatus(newStatus);

            }
        }

    }

    public StringProperty latestVersionProperty() {
        if (latestVersion == null)
            latestVersion = new SimpleStringProperty(this, "latestVersion");
        return latestVersion;
    }

    public void updateLatestVersion() {
        if (downloads.isEmpty()) {
            setLatestVersion("no data");
            return;
        }
        downloads.stream().filter(x -> x.release.equalsIgnoreCase(getReleaseType().name)).map(x -> x.title).findFirst().ifPresent(this::setLatestVersion);

    }

    public String getGameVersion() {
        return gameVersionProperty().get();
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersionProperty().set(gameVersion);
    }

    public StringProperty gameVersionProperty() {
        if (gameVersion == null)
            gameVersion = new SimpleStringProperty(this, "gameVersion");
        return gameVersion;
    }

    public ReleaseType getReleaseType() {
        return releaseTypeProperty().get();
    }

    public void setReleaseType(ReleaseType releaseType) {
        releaseTypeProperty().setValue(releaseType);
    }

    public ObjectProperty<ReleaseType> releaseTypeProperty() {
        if (releaseType == null)
            releaseType = new SimpleObjectProperty<>(this, "releaseType");
        return releaseType;
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
        updateLatestVersion();
    }
}
