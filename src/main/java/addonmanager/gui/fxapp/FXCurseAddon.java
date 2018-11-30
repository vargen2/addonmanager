package addonmanager.gui.fxapp;

import addonmanager.app.CurseAddon;
import javafx.beans.property.*;

public class FXCurseAddon extends CurseAddon {

    private StringProperty urlProperty;
    private StringProperty titleProperty;
    private StringProperty descriptionProperty;
    private LongProperty downloadsProperty;
    private StringProperty updatedEpochProperty;
    private StringProperty createdEpochProperty;

    public FXCurseAddon(String addonURL, String title, String description, long downloads, String updatedEpoch, String createdEpoch) {
        super(addonURL, title, description, downloads, updatedEpoch, createdEpoch);
    }

    public FXCurseAddon(CurseAddon curseAddon) {
        this(curseAddon.getAddonURL(), curseAddon.getTitle(), curseAddon.getDescription(), curseAddon.getDownloads(), curseAddon.getUpdatedEpoch(), curseAddon.getCreatedEpoch());
    }

    public ReadOnlyStringProperty urlProperty() {
        if (urlProperty == null)
            urlProperty = new SimpleStringProperty(this, "url", getAddonURL());
        return urlProperty;
    }

    public ReadOnlyStringProperty titleProperty() {
        if (titleProperty == null)
            titleProperty = new SimpleStringProperty(this, "title", getTitle());
        return titleProperty;
    }

    public ReadOnlyStringProperty descriptionProperty() {
        if (descriptionProperty == null)
            descriptionProperty = new SimpleStringProperty(this, "description", getDescription());
        return descriptionProperty;
    }

    public ReadOnlyLongProperty downloadsProperty() {
        if (downloadsProperty == null)
            downloadsProperty = new SimpleLongProperty(this, "downloads", getDownloads());
        return downloadsProperty;
    }

    public ReadOnlyStringProperty updatedEpochProperty() {
        if (updatedEpochProperty == null)
            updatedEpochProperty = new SimpleStringProperty(this, "updatedEpoch", getUpdatedEpoch());
        return updatedEpochProperty;
    }

    public ReadOnlyStringProperty createdEpochProperty() {
        if (createdEpochProperty == null)
            createdEpochProperty = new SimpleStringProperty(this, "createdEpoch", getCreatedEpoch());
        return createdEpochProperty;
    }
}
