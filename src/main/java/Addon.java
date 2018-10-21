import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Addon {

    enum ReleaseType {ALPHA, BETA, RELEASE}
    enum UpdateMode {AUTO,MANUAL}

    private Addon.ReleaseType wantedReleaseType;
    private UpdateMode updateMode;
    private LocalDateTime dateLastModified;
    private ReleaseType releaseType;
    private StringProperty title;
    private StringProperty version;
    private LocalDateTime dateUploaded;
    private StringProperty gameVersion;


    public void setTitle(String value) {
        titleProperty().set(value);
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
    }

    public StringProperty versionProperty() {
        if (version == null)
            version = new SimpleStringProperty(this, "version");
        return version;
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
        return releaseType;
    }

    public void setReleaseType(ReleaseType releaseType) {
        this.releaseType = releaseType;
    }


    public LocalDateTime getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(LocalDateTime dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

}
