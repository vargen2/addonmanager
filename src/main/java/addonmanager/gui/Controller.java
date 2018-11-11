package addonmanager.gui;

import addonmanager.app.Updateable;
import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Game;
import addonmanager.app.Model;
import addonmanager.gui.fxapp.FXFactory;
import addonmanager.gui.fxapp.FXGame;
import addonmanager.gui.fxapp.FXModel;
import addonmanager.gui.tableview.ReleaseLatestVersionCell;
import addonmanager.gui.tableview.StatusCell;
import addonmanager.gui.task.FindGamesTask;
import addonmanager.gui.task.FoundGameTask;
import addonmanager.gui.task.RefreshGameTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.commons.io.IOUtils;
import org.controlsfx.control.TaskProgressView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class Controller {


    @FXML
    private Button refreshButton;
    @FXML
    private Button settingsButton;
    @FXML
    private ChoiceBox gameChoiceBox;
    @FXML
    private TableView<Addon> tableView;
    @FXML
    private StackPane bottomStackPane;
    @FXML
    private TaskProgressView<Task<Void>> taskProgressView;

    @FXML
    private void initialize() {
        App.setFactory(new FXFactory());
        Model model = App.getFactory().createModel();
        Settings settings = new Settings(model);

        gameChoiceBox.getItems().add(new Separator());

        ChoiceBoxItem add = new ChoiceBoxItem(new Consumer() {
            @Override
            public void accept(Object o) {

            }
        }, "Add Games...");
        gameChoiceBox.getItems().add(add);
        ChoiceBoxItem manual = new ChoiceBoxItem(new Consumer() {
            @Override
            public void accept(Object o) {

            }
        }, "Add Directory manually...");
        gameChoiceBox.getItems().add(manual);
        ChoiceBoxItem scan = new ChoiceBoxItem(o -> {
            Consumer<Game> consumer = game -> {
                Thread t = new Thread(new FoundGameTask(game, model, gameChoiceBox));
                t.setDaemon(true);
                t.start();
            };
            FindGamesTask ds = new FindGamesTask(consumer, false);
            Platform.runLater(() -> taskProgressView.getTasks().add(ds));

            Thread t = new Thread(ds);
            t.setDaemon(true);
            t.start();
        }, "Scan for Directories...");
        gameChoiceBox.getItems().add(scan);

        gameChoiceBox.setValue(add);
        gameChoiceBox.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                //System.out.println("triggred");
                if (((ChoiceBoxItem) newValue).getGame() != null) {
                    gameChoiceBox.getItems().remove(add);
                    model.setSelectedGame(((ChoiceBoxItem) newValue).getGame());
                }
                if (newValue == manual || newValue == scan) {
                    ((ChoiceBoxItem) newValue).getConsumer().accept(0);
                    gameChoiceBox.setValue(oldValue);
                }

            }
        });


        taskProgressView.getTasks().addListener(new ListChangeListener<Task<Void>>() {
            @Override
            public void onChanged(Change<? extends Task<Void>> c) {
                c.next();
                if (c.wasAdded())
                    taskProgressView.setPrefHeight(taskProgressView.getPrefHeight() + 70);
                if (c.wasRemoved())
                    taskProgressView.setPrefHeight(taskProgressView.getPrefHeight() - 70);

            }
        });

        if (model instanceof FXModel) {
            ((FXModel) model).selectedGameProperty.addListener((observable, oldValue, newValue) -> {
                if (newValue instanceof FXGame) {
                    tableView.setItems(((FXGame) newValue).addonObservableList);
                } else
                    System.err.println("game not FXGame");
                refreshButton.setDisable(newValue == null);
            });
        } else {
            System.err.println("model not FXModel");
        }

        TableColumn<Addon, String> titleVersionCol = new TableColumn<>("Title");
        titleVersionCol.setCellValueFactory(new PropertyValueFactory("titleVersion"));
        titleVersionCol.setPrefWidth(200);


//        TableColumn<Addon, Addon.ReleaseType> releaseTypeCol = new TableColumn<>("Release Type");
//        releaseTypeCol.setCellValueFactory(new PropertyValueFactory("releaseType"));
//        releaseTypeCol.setPrefWidth(50);
//        releaseTypeCol.setCellFactory(new Callback<TableColumn<Addon, Addon.ReleaseType>, TableCell<Addon, Addon.ReleaseType>>() {
//            @Override
//            public TableCell<Addon, Addon.ReleaseType> call(TableColumn<Addon, Addon.ReleaseType> param) {
//                return new TableCell<Addon, Addon.ReleaseType>() {
//
//                    @Override
//                    public void updateItem(Addon.ReleaseType item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (!isEmpty()) {
//                            this.setStyle("-fx-font-size: 2em");
//                            setText(item.toString());
//                        }else {
//                            setText(null);
//                        }
//                    }
//                };
//            }
//        });

        TableColumn<Addon, String> releaseLatestCol = new TableColumn<>("Latest Version");
        releaseLatestCol.setCellValueFactory(new PropertyValueFactory("releaseLatest"));
        releaseLatestCol.setPrefWidth(100);
        releaseLatestCol.setCellFactory(ReleaseLatestVersionCell.cellFactory());

        TableColumn<Addon, Addon.Status> stateCol = new TableColumn<>("Status");
        stateCol.setCellFactory(StatusCell.cellFactory());

        stateCol.setCellValueFactory(new PropertyValueFactory<Addon, Addon.Status>("status"));
        stateCol.setPrefWidth(200);


        TableColumn<Addon, String> gameVersionCol = new TableColumn<>("Game Version");
        gameVersionCol.setCellValueFactory(new PropertyValueFactory("gameVersion"));
        gameVersionCol.setPrefWidth(100);

        tableView.getColumns().setAll(titleVersionCol, releaseLatestCol, stateCol, gameVersionCol);


        refreshButton.setOnAction(event -> {
            Game game = model.getSelectedGame();
            if (game == null)
                return;

            Thread thread = new Thread(new RefreshGameTask(game));
            thread.setDaemon(true);
            thread.start();
        });

        try {

            Path p = Paths.get(getClass().getResource("../../fa-solid-900.ttf").toURI());
            FontAwesome fontAwesome = new FontAwesome(Files.newInputStream(p));
            settingsButton.setGraphic(fontAwesome.create(FontAwesome.Glyph.COG).size(20));
            settingsButton.setText("");
            settingsButton.setPrefHeight(25);
            settingsButton.setMinHeight(25);
            settingsButton.setMaxHeight(25);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        settingsButton.setOnAction(event -> {
            if (!settings.isShowing())
                settings.show(settingsButton);
            else
                settings.hide();
        });


    }


    public static void refreshFromNet(Game game) {

        game.getAddons().forEach(addon -> {
            Thread thread = new Thread(new Task() {
                @Override
                protected Object call() throws Exception {
                    Updateable updateable = Updateable.createUpdateable(this, this::updateMessage, this::updateProgress);
                    addon.setUpdateable(updateable);

                    if (!App.downLoadVersions(addon)) {
                        cancel();
                        return null;
                    }
                    return null;
                }
            });
            thread.setDaemon(true);
            thread.start();
            try {
                //todo lägg till denna i settings
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


    }


}
