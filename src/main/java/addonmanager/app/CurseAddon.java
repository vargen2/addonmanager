package addonmanager.app;

public class CurseAddon {

    private final String addonURL;
    private final String title;
    private final String description;
    private long downloads;
    private String updatedEpoch;
    private final String createdEpoch;

    public CurseAddon(String addonURL, String title, String description, long downloads, String updatedEpoch, String createdEpoch) {
        this.addonURL = addonURL;
        this.title = title;
        this.description = description;
        this.downloads = downloads;
        this.updatedEpoch = updatedEpoch;
        this.createdEpoch = createdEpoch;
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

    public long getDownloads() {
        return downloads;
    }


    public String getUpdatedEpoch() {
        return updatedEpoch;
    }

    public String getCreatedEpoch() {
        return createdEpoch;
    }


    @Override
    public String toString() {
        return title;
    }

    public String detailedToString() {
        return "addonURL=" + addonURL + " title=" + title + " description=" + description;
    }
}
