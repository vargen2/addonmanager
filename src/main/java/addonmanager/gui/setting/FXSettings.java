package addonmanager.gui.setting;

import addonmanager.app.Settings;
import addonmanager.app.Util;
import addonmanager.app.file.Saver;

public class FXSettings implements Settings {

    private int refreshDelay;
    private boolean autoRefresh;

    public FXSettings(int refreshDelay) {
        this.refreshDelay = refreshDelay;
    }

    public int getRefreshDelay() {
        return refreshDelay;
    }

    public void setRefreshDelay(int refreshDelay) {
        this.refreshDelay = refreshDelay;
        Saver.saveSettings();
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        Saver.saveSettings();
    }

    @Override
    public void load(String load) {
        if (load == null || load.isEmpty())
            return;

        load.lines().filter(x -> x.contains("fxrefreshdelay")).findAny().ifPresent(s -> refreshDelay = Integer.valueOf(s.replaceAll("fxrefreshdelay", "").strip()));
        load.lines().filter(x -> x.contains("fxautorefresh")).findAny().ifPresent(s -> autoRefresh = Boolean.valueOf(s.replaceAll("fxautorefresh", "").strip()));
    }

    @Override
    public String save() {
        return "fxrefreshdelay " + Integer.toString(refreshDelay) + Util.LINE +
                "fxautorefresh " + Boolean.toString(autoRefresh) + Util.LINE;
    }
}
