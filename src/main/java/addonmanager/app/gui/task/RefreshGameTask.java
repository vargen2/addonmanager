package addonmanager.app.gui.task;

import addonmanager.app.core.App;
import addonmanager.app.core.Game;
import addonmanager.app.core.file.FileOperations;
import addonmanager.app.gui.Controller;
import javafx.concurrent.Task;

public class RefreshGameTask extends Task<Void> {

    private Game game;

    public RefreshGameTask(Game game) {
        super();
        this.game = game;
    }

    @Override
    protected Void call() {
        FileOperations.refreshGameDirectory(game);
        Controller.refreshFromNet(game);
        return null;
    }


}
