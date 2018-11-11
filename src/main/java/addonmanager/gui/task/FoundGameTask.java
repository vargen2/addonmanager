package addonmanager.gui.task;

import addonmanager.app.Game;
import addonmanager.app.Model;
import addonmanager.app.file.FileOperations;
import addonmanager.gui.ChoiceBoxItem;
import addonmanager.gui.Controller;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ChoiceBox;

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
        for (var g : model.getGames()) {
            if (g.getDirectory().equals(game.getDirectory()))
                return null;
        }

        Platform.runLater(() -> {
            if(!model.addGame(game))
                return;

            Task<Void> refreshTask;
            ChoiceBoxItem cbi = new ChoiceBoxItem(game);
            if (model.getSelectedGame() == null) {
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