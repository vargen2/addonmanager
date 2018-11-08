package addonmanager.gui.task;

import addonmanager.app.Updateable;
import addonmanager.app.Game;
import addonmanager.app.file.FileOperations;
import javafx.concurrent.Task;

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
