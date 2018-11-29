package addonmanager.app;

public class CurseAddon {

    private final String addonURL;
    private final String title;
    private final String description;

    public CurseAddon(String addonURL, String title, String description) {
        this.addonURL = addonURL;
        this.title = title;
        this.description = description;
    }

    public String getAddonURL() {
        return addonURL;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return title;
    }

    public String detailedToString() {
        return "addonURL=" + addonURL + " title=" + title + " description=" + description;
    }
}
