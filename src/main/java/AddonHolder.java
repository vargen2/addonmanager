import java.time.LocalDateTime;

public class AddonHolder {

    public AddonHolder(Addon addon) {
        this.addon = addon;
    }

    private Addon addon;
    private Addon.ReleaseType wantedReleaseType;
    private LocalDateTime dateLastModified;

    public Addon getAddon() {
        return addon;
    }

    public Addon.ReleaseType getWantedReleaseType() {
        return wantedReleaseType;
    }
}
