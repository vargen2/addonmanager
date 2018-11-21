package addonmanager.gui.task;

import addonmanager.app.App;
import addonmanager.app.Game;
import addonmanager.app.file.FileOperations;
import addonmanager.gui.ChoiceBoxItem;
import addonmanager.gui.Controller;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ChoiceBox;

public class FoundGameTask extends Task<Void> {

    private static final Object lock = new Object();
    private ChoiceBox choiceBox;
    private Game game;

    public FoundGameTask(Game game, ChoiceBox choiceBox) {
        this.game = game;
        this.choiceBox = choiceBox;
    }

    @Override
    protected Void call() {
        for (var g : App.model.getGames()) {
            if (g.getDirectory().equals(game.getDirectory()))
                return null;
        }

        Platform.runLater(() -> {
            if (!App.addGame(game))
                return;

            Task<Void> refreshTask;
            ChoiceBoxItem cbi = new ChoiceBoxItem(game);
            if (App.model.getSelectedGame() == null) {
                choiceBox.setValue(cbi);
                refreshTask = new Task<Void>() {
                    @Override
                    protected Void call() {
                        FileOperations.refreshGameDirectory(game);
                        Controller.refreshFromNet(game);
                        return null;
                    }
                };
            } else {
                refreshTask = new Task<Void>() {
                    @Override
                    protected Void call() {
                        FileOperations.refreshGameDirectory(game);
                        return null;
                    }
                };
            }

            choiceBox.getItems().add(0, cbi);
            Thread t = new Thread(refreshTask);
            t.setDaemon(true);
            t.start();
        });


        return null;
    }
}
