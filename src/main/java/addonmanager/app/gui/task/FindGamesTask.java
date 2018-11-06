package addonmanager.app.gui.task;

import addonmanager.Updateable;
import addonmanager.app.core.App;
import javafx.concurrent.Task;

import java.io.File;
import java.util.function.Consumer;

public class FindGamesTask extends Task<Void> {

    private Consumer<File> consumer;
    private boolean mustHaveExe;

    public FindGamesTask(Consumer<File> consumer, boolean mustHaveExe) {
        this.mustHaveExe = mustHaveExe;
        this.consumer = consumer;
    }

    @Override
    protected Void call() {
        App.findGames(Updateable.createUpdateable(this, this::updateMessage, this::updateProgress), consumer, mustHaveExe);
        return null;
    }

}
