package addonmanager.gui.fxapp;

import addonmanager.app.CurseAddon;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FXCurseAddon extends CurseAddon {

    private StringProperty urlProperty;
    private StringProperty titleProperty;
    private StringProperty descriptionProperty;

    public FXCurseAddon(String addonURL, String title, String description) {
        super(addonURL, title, description);
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

}
