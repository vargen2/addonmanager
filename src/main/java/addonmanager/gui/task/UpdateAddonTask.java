package addonmanager.gui.task;

import addonmanager.app.Download;
import addonmanager.app.Updateable;
import addonmanager.app.Addon;
import addonmanager.app.App;
import javafx.concurrent.Task;


public class UpdateAddonTask extends Task<Void> {

    private Addon addon;
    private Download download;

    public UpdateAddonTask(Addon addon,Download download) {
        super();
        this.addon = addon;
        this.download=download;
    }

    @Override
    protected Void call() {
        Updateable updateable = Updateable.createUpdateable(this,this::updateMessage, this::updateProgress);
        addon.setUpdateable(updateable);
        updateProgress(0,1);
        if(!App.updateAddon(addon,download)){
            updateMessage("canceled");
            cancel();
            return null;
        }
        updateMessage("done");
        updateProgress(1, 1);
        return null;
    }

}
