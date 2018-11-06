package addonmanager.app.gui.task;

import addonmanager.Updateable;
import addonmanager.app.core.Addon;
import addonmanager.app.core.App;
import javafx.concurrent.Task;

public class UpdateAddonTask extends Task<Void> {

    private Addon addon;

    public UpdateAddonTask(Addon addon) {
        super();
        this.addon = addon;
    }

    @Override
    protected Void call() {
        Updateable updateable = Updateable.createUpdateable(this,this::updateMessage, this::updateProgress);
        addon.setUpdateable(updateable);
        if(!App.updateAddon(addon)){
            updateMessage("canceled");
            cancel();
            return null;
        }
        updateMessage("done");
        updateProgress(1, 1);
        return null;
    }

}
