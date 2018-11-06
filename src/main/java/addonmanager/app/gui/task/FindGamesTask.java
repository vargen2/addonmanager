package addonmanager.app.gui.task;

import addonmanager.Updateable;
import addonmanager.app.core.App;
import addonmanager.app.core.Game;
import addonmanager.app.core.file.FileOperations;
import javafx.concurrent.Task;

import java.io.File;
import java.util.function.Consumer;

public class FindGamesTask extends Task<Void> {

    private Consumer<Game> consumer;
    private boolean mustHaveExe;

    public FindGamesTask(Consumer<Game> consumer, boolean mustHaveExe) {
        this.mustHaveExe = mustHaveExe;
        this.consumer = consumer;
    }

    @Override
    protected Void call() {
        FileOperations.findGames(Updateable.createUpdateable(this, this::updateMessage, this::updateProgress), consumer, mustHaveExe);
        return null;
    }

}
