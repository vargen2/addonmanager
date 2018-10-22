import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.List;

public class Addon {

    enum ReleaseType {ALPHA, BETA, RELEASE}
    enum UpdateMode {AUTO,MANUAL}

    private List<Download> downloads;
    private String folderName;
    private ReleaseType wantedReleaseType;
    private UpdateMode updateMode;
    private LocalDateTime dateLastModified;
    private ReleaseType releaseType;
    private StringProperty title;
    private StringProperty version;
    private LocalDateTime dateUploaded;
    private StringProperty gameVersion;
    private StringProperty titleVersion;
    private ObjectProperty<VersionCell> versionCell;
    private ObjectProperty<VersionCellData> versionCellData;

    public VersionCellData getVersionCellData() {
        return versionCellDataProperty().get();
    }

    public ObjectProperty<VersionCellData> versionCellDataProperty() {
        if(versionCellData==null)
            versionCellData=new SimpleObjectProperty<>(this,"versionCellData");
        return versionCellData;
    }

    public void setVersionCellData(VersionCellData versionCellData) {
        this.versionCellDataProperty().set(versionCellData);
    }

    public Addon(String folderName) {
        this.folderName = folderName;
    }

    public VersionCell getVercionCell() {
        return versionCellProperty().get();
    }

    public ObjectProperty<VersionCell> versionCellProperty() {
        if (versionCell == null)
            versionCell = new SimpleObjectProperty<VersionCell>(this,"versionCell");

        return versionCell;
    }

    private void updateTitleVersion(){
        titleVersionProperty().setValue(titleProperty().get()+((versionProperty().get()!=null)? "\n"+versionProperty().get():""));
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

    public String getFolderName() {
        return folderName;
    }

    public List<Download> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<Download> downloads) {
        this.downloads = downloads;
    }
}
