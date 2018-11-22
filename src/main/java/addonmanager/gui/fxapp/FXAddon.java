package addonmanager.gui.fxapp;

import addonmanager.app.Addon;
import addonmanager.app.Download;
import addonmanager.app.Game;
import javafx.beans.property.*;

import java.util.List;

public class FXAddon extends Addon {

    private transient final StringProperty titleVersionProperty = new SimpleStringProperty(this, "titleVersion");
    private transient final StringProperty gameVersionProperty = new SimpleStringProperty(this, "gameVersion");
    private transient final ObjectProperty<ReleaseType> releaseTypeProperty = new SimpleObjectProperty<>(this, "releaseType");
    private transient final ObjectProperty<Download> latestDownloadProperty = new SimpleObjectProperty<>(this, "latestDownload");
    private transient final StringProperty releaseLatestProperty = new SimpleStringProperty(this, "releaseLatest");
    private transient final ObjectProperty<Status> statusProperty = new SimpleObjectProperty<>(this, "status");

    FXAddon(Game game, String folderName, String absolutePath) {
        super(game, folderName, absolutePath);
        setStatus(super.getStatus());
        setReleaseType(super.getReleaseType());
    }

    FXAddon(Game game, Addon addon) {
        super(game, addon);
        updateTitleVersion();
        setGameVersion(addon.getGameVersion());
        setLatestDownload(addon.getLatestDownload());
        setReleaseType(addon.getReleaseType());
        setStatus(addon.getStatus());
    }

    public ReadOnlyStringProperty titleVersionProperty() {
        return titleVersionProperty;
    }

    private void updateTitleVersion() {
        String v = "";
        if (getLatestUpdate() != null)
            v = getLatestUpdate().getRelease() + " " + getLatestUpdate().getTitle();
        else if (getVersion() != null)
            v = getVersion();
        titleVersionProperty.setValue(getTitle() + "\n" + v);
    }

    @Override
    public void setTitle(String value) {
        super.setTitle(value);
        updateTitleVersion();
    }

    @Override
    public void setVersion(String version) {
        super.setVersion(version);
        updateTitleVersion();
    }

    public ReadOnlyStringProperty gameVersionProperty() {
        return gameVersionProperty;
    }

    @Override
    public void setGameVersion(String gameVersion) {
        super.setGameVersion(gameVersion);
        gameVersionProperty.setValue(gameVersion);
    }

    public ReadOnlyObjectProperty<ReleaseType> releaseTypeProperty() {
        return releaseTypeProperty;
    }

    @Override
    public void setReleaseType(ReleaseType releaseType) {
        super.setReleaseType(releaseType);
        releaseTypeProperty.setValue(releaseType);
        updateReleaseLatest();
    }

    @Override
    protected void setLatestDownload(Download download) {
        super.setLatestDownload(download);
        latestDownloadProperty.setValue(download);
    }

    public ReadOnlyObjectProperty<Download> latestDownloadProperty() {
        return latestDownloadProperty;
    }

    public ReadOnlyStringProperty releaseLatestProperty() {
        return releaseLatestProperty;
    }

    private void updateReleaseLatest() {
        releaseLatestProperty.setValue(getReleaseType().toString() + ((getLatestDownload() != null) ? "\n" + getLatestDownload() : ""));
    }

    @Override
    public void setDownloads(List<Download> downloads) {
        super.setDownloads(downloads);
        updateReleaseLatest();
    }

    public ReadOnlyObjectProperty<Status> statusProperty() {
        return statusProperty;
    }

    @Override
    protected void setStatus(Status status) {
        super.setStatus(status);
        statusProperty.setValue(status);
    }

    @Override
    public void setLatestUpdate(Download latestUpdate) {
        super.setLatestUpdate(latestUpdate);
        updateTitleVersion();
    }
}
