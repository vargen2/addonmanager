package addonmanager.core;

import addonmanager.file.ReplaceAddonTask;
import addonmanager.net.FindProject;
import addonmanager.net.GetVersionsTask;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.text.Collator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Addon {

    public enum Status {NONE, GETTING_VERSIONS, CAN_UPDATE, UPDATING, UP_TO_DATE}

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
    private ReplaceAddonTask replaceAddonTask;
    private String folderName;
    private String absolutePath;
    private String projectUrl;
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

    private final ObjectProperty<Status> status;


    public Addon(String folderName, String absolutePath) {
        this.folderName = folderName;
        this.absolutePath = absolutePath;
        //  this.status = new SimpleObjectProperty<>(this, "status");
        this.status = new SimpleObjectProperty<>(this, "status");
        this.latestDownload = new SimpleObjectProperty<Download>(this, "latestDownload");
        setLatestDownload(null);
        setReleaseType(ReleaseType.RELEASE);
        updateLatestDownload();
        setStatus(Status.NONE);
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
            setStatus(Status.NONE);
            return;
        }
        downloads.stream().filter(x -> x.release.equalsIgnoreCase(getReleaseType().name)).findFirst().ifPresent(this::setLatestDownload);
        Collator collator = Collator.getInstance(new Locale("sv", "SE"));
        collator.setStrength(Collator.CANONICAL_DECOMPOSITION);
        if (getLatestDownload() != null && getVersion() != null) {

            if (collator.compare(getLatestDownload().title, getVersion()) > 0) {
                setStatus(Status.CAN_UPDATE);

            } else {
                setStatus(Status.UP_TO_DATE);
            }
        }else if(getLatestDownload() !=null && getVersion()==null){
            setStatus(Status.CAN_UPDATE);
        }

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
        setStatus(Status.GETTING_VERSIONS);
    }

    public ReplaceAddonTask getReplaceAddonTask() {
        return replaceAddonTask;
    }

    public void setReplaceAddonTask(ReplaceAddonTask replaceAddonTask) {
        this.replaceAddonTask = replaceAddonTask;
        setStatus(Status.UPDATING);
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    //todo move this method to file.someetinhg
    public void refreshToc() {
        File d = new File(absolutePath);
        var tocFile = d.listFiles((dir, name) -> name.toLowerCase().endsWith(".toc"));
        if (tocFile == null || tocFile[0] == null)
            return;
        List<String> lines = null;

        try {
            lines = Files.readAllLines(tocFile[0].toPath());
        } catch (MalformedInputException e) {


            try {
                lines = Files.readAllLines(tocFile[0].toPath(), Charset.forName("ISO-8859-1"));
            } catch (IOException e1) {

                e1.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lines == null)
            return;

        for (var line : lines) {
            if (line.contains("Interface:")) {
                setGameVersion(line.substring(line.indexOf("Interface:") + 10).trim());
            } else if (line.contains("Version:")) {
                setVersion(line.substring(line.indexOf("Version:") + 8).trim());
            } else if (line.contains("Title:")) {

                setTitle(line.substring(line.indexOf("Title:") + 6).replaceAll("\\|c[a-zA-Z_0-9]{8}", "").replaceAll("\\|r", "").trim());
            }
        }
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }
}
