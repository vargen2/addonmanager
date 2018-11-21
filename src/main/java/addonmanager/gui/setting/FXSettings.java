package addonmanager.gui.setting;

import addonmanager.app.Settings;
import addonmanager.app.file.Saver;

//save/load this for fx specific settings
public class FXSettings implements Settings {

    private int refreshDelay;

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

    @Override
    public void load(String load) {
        if (load == null || load.isEmpty())
            return;

        load.lines().filter(x -> x.contains("fxrefreshdelay")).findAny().ifPresent(s -> refreshDelay = Integer.valueOf(s.replaceAll("fxrefreshdelay", "").strip()));
    }

    @Override
    public String save() {
        return "fxrefreshdelay " + Integer.toString(refreshDelay) + "\n";
    }
}
