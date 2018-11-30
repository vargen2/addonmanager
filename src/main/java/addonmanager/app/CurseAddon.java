package addonmanager.app;

public class CurseAddon {

    public enum Status {INSTALLED, INSTALLING, NOT_INSTALLED, UNKNOWN}

    private transient Status status = Status.UNKNOWN;
    private transient Updateable updateable = Updateable.EMPTY_UPDATEABLE;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Updateable getUpdateable() {
        return updateable;
    }

    public void setUpdateable(Updateable updateable) {
        this.updateable = updateable;
    }

    @Override
    public String toString() {
        return title;
    }

    public String detailedToString() {
        return "addonURL=" + addonURL + " title=" + title + " description=" + description;
    }
}
