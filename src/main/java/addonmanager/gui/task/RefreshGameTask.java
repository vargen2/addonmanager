package addonmanager.gui.task;

import addonmanager.app.Game;
import addonmanager.app.file.FileOperations;
import addonmanager.gui.Controller;
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
