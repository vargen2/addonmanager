package addonmanager.gui.setting;

import java.io.Serializable;

//save/load this for fx specific settings
public class FXSettings implements Serializable {

    private int refreshDelay=250;

    public int getRefreshDelay() {
        return refreshDelay;
    }

    public void setRefreshDelay(int refreshDelay) {
        this.refreshDelay = refreshDelay;
    }
}
