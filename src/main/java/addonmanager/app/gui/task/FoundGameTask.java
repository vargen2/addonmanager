package addonmanager.app.gui.task;

import addonmanager.app.core.App;
import addonmanager.app.core.Game;
import addonmanager.app.core.Model;
import addonmanager.app.gui.ChoiceBoxItem;
import addonmanager.app.gui.Controller;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ChoiceBox;

import java.io.File;

public class FoundGameTask extends Task<Void> {

    private static final Object lock = new Object();
    private Model model;
    private ChoiceBox choiceBox;
    private Game game;

    public FoundGameTask(Game game, Model model, ChoiceBox choiceBox) {
        this.game = game;
        this.model = model;
        this.choiceBox = choiceBox;
    }

    @Override
    protected Void call() {

        synchronized (lock) {
            for (var g : model.games) {
                if (g.getDirectory().equals(g.getDirectory()))
                    return null;
            }
        }
        Platform.runLater(() -> {
            Task<Void> refreshTask;
            ChoiceBoxItem cbi = new ChoiceBoxItem(game);
            if (model.selectedGame.getValue() == null) {
                choiceBox.setValue(cbi);
                refreshTask = new Task<Void>() {
                    @Override
                    protected Void call() {
                        App.refreshGameDirectory(game);
                        Controller.refreshFromNet(game);
                        return null;
                    }
                };
            } else {
                refreshTask = new Task<Void>() {
                    @Override
                    protected Void call() {
                        App.refreshGameDirectory(game);
                        return null;
                    }
                };
            }
            choiceBox.getItems().add(0, cbi);

            synchronized (lock) {
                model.games.add(game);
            }

            Thread t = new Thread(refreshTask);
            t.setDaemon(true);
            t.start();
        });


        return null;
    }
}
