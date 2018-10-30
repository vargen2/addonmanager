package addonmanager.core;

import addonmanager.net.GetVersionsTask;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Addon {

    public enum State {NONE, GETTING_VERSIONS, CAN_UPDATE, UPDATING, UP_TO_DATE}

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
    private GetVersionsTask getVersionsTask;
    private String folderName;
    private UpdateMode updateMode;
    private LocalDateTime dateLastModified;

    private StringProperty title;
    private StringProperty version;
    private StringProperty titleVersion;

    private LocalDateTime dateUploaded;
    private StringProperty gameVersion;

    private ObjectProperty<ReleaseType> releaseType;
    private final ObjectProperty<Download> latestDownload;
    private StringProperty releaseLatest;

    private final ObjectProperty<State> state;


    public Addon(String folderName) {
        this.folderName = folderName;
        //  this.status = new SimpleObjectProperty<>(this, "status");
        this.state = new SimpleObjectProperty<>(this, "state");
        this.latestDownload = new SimpleObjectProperty<Download>(this, "latestDownload");
        setLatestDownload(null);
        setReleaseType(ReleaseType.RELEASE);
        updateLatestDownload();

        setState(State.NONE);
    }


    public State getState() {
        return stateProperty().get();
    }

    public ObjectProperty<State> stateProperty() {

        return state;
    }

    public void setState(State state) {
        stateProperty().set(state);
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

    public Download getLatestDownload() {
        return latestDownloadProperty().get();
    }

    private void setLatestDownload(Download version) {
        this.latestDownloadProperty().set(version);


    }

    public ObjectProperty<Download> latestDownloadProperty() {
        return latestDownload;
    }

    public void updateLatestDownload() {
        if (downloads.isEmpty()) {
            setLatestDownload(null);
            setState(State.NONE);
            return;
        }
        downloads.stream().filter(x -> x.release.equalsIgnoreCase(getReleaseType().name)).findFirst().ifPresent(this::setLatestDownload);
        setState(State.CAN_UPDATE);
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
        updateLatestDownload();
        updateReleaseLatest();
    }

    public ObjectProperty<ReleaseType> releaseTypeProperty() {
        if (releaseType == null)
            releaseType = new SimpleObjectProperty<>(this, "releaseType");
        return releaseType;
    }

    private void updateReleaseLatest() {
        releaseLatestProperty().setValue(getReleaseType().name + ((getLatestDownload() != null) ? "\n" + getLatestDownload() : ""));
    }

    public String getReleaseLatest() {
        return releaseLatestProperty().get();
    }

    public StringProperty releaseLatestProperty() {
        if (releaseLatest == null)
            releaseLatest = new SimpleStringProperty(this, "releaseLatest");
        return releaseLatest;
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
        updateReleaseLatest();
    }

    public GetVersionsTask getNewVersionsTask() {
        return getVersionsTask;
    }

    public void setNewVersionsTask(GetVersionsTask getVersionsTask) {
        this.getVersionsTask = getVersionsTask;
        setState(State.GETTING_VERSIONS);
    }

}
