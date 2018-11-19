package addonmanager.gui;

import addonmanager.app.*;
import addonmanager.gui.fxapp.FXFactory;
import addonmanager.gui.fxapp.FXGame;
import addonmanager.gui.fxapp.FXModel;
import addonmanager.gui.setting.FXSettings;
import addonmanager.gui.setting.Settings;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.controlsfx.control.TaskProgressView;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.Optional;
import java.util.function.Consumer;

public class Controller {

    public static FXSettings fxSettings;

    @FXML
    private Button refreshButton;
    @FXML
    private Button removeButton;
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


    private AddonContextMenu addonContextMenu;
    private Settings settings;

    @FXML
    private void initialize() {


        App.setFactory(new FXFactory());
        Model model = App.getFactory().createModel();
        Controller.fxSettings = new FXSettings();


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
                    if (gameChoiceBox.getItems().contains(oldValue) && !(oldValue == manual || oldValue == scan))
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
                } else {
                    tableView.setItems(null);
                    //System.err.println("game not FXGame");
                }
                refreshButton.setDisable(newValue == null);
                removeButton.setDisable(newValue == null);
            });
        } else {
            App.LOG.info("model not FXModel");
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
        tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (addonContextMenu == null) {
                addonContextMenu = new AddonContextMenu();
                App.LOG.info("addoncontextmenu not async loaded");
            }
            if (event.getButton() != MouseButton.SECONDARY) {

                addonContextMenu.hide();
                return;
            }

            var pickResult = event.getPickResult().getIntersectedNode();
            if (pickResult == null || pickResult instanceof Button)
                return;
            while (pickResult != null && !(pickResult instanceof TableCell)) {
                pickResult = pickResult.getParent();
            }

            if (pickResult == null)
                return;

            TableCell tableCell = (TableCell) pickResult;
            var addon = tableCell.getTableRow().getItem();
            if (addon instanceof Addon) {
                addonContextMenu.show((Addon) addon, tableCell, event.getScreenX(), event.getScreenY());
            }


        });
        Icon.setIcon(refreshButton, FontAwesome.Glyph.REFRESH, Color.MEDIUMSEAGREEN);
        refreshButton.setTooltip(new Tooltip("Refresh all addons."));
        refreshButton.setOnAction(event -> {
            Game game = model.getSelectedGame();
            if (game == null)
                return;

            Thread thread = new Thread(new RefreshGameTask(game));
            thread.setDaemon(true);
            thread.start();
        });

        Icon.setIcon(removeButton, FontAwesome.Glyph.REMOVE, Color.INDIANRED);
        removeButton.setTooltip(new Tooltip("Remove game from this application."));
        removeButton.setOnAction(event -> {
            if (model.getSelectedGame() == null)
                return;
            Game game = model.getSelectedGame();
            model.removeGame(game);
            model.setSelectedGame(null);
            Optional<ChoiceBoxItem> gameChoiceBoxItem = gameChoiceBox.getItems().stream().filter(ChoiceBoxItem.class::isInstance).map(ChoiceBoxItem.class::cast).filter(x -> ((ChoiceBoxItem) x).getGame() == game).findFirst();
            gameChoiceBoxItem.ifPresent(gameChoiceBox.getItems()::remove);
            if (model.getGames().isEmpty()) {
                gameChoiceBox.getItems().add(0, add);
                gameChoiceBox.setValue(add);
            }

        });

        Icon.setIcon(settingsButton, FontAwesome.Glyph.COG, Color.DARKSLATEGRAY);
        settingsButton.setOnAction(event -> {
            if (!settings.isShowing())
                settings.show(settingsButton);
            else
                settings.hide();
        });

        Thread thread = new Thread(() -> {
            addonContextMenu = new AddonContextMenu();
            settings = new Settings(model, Controller.fxSettings);
        });
        thread.setDaemon(true);
        thread.start();
        // CompletableFuture.runAsync(() -> addonContextMenu = new AddonContextMenu());
        // CompletableFuture.runAsync(() -> settings = new Settings(model, Controller.fxSettings));
    }


    public static void refreshFromNet(Game game) {

        game.getAddons().forEach(addon -> {
            Thread thread = new Thread(
                    new Task<Void>() {
                        @Override
                        protected Void call() {

                            Updateable updateable = Updateable.createUpdateable(this, this::updateMessage, this::updateProgress);
                            addon.setUpdateable(updateable);

                            if (!App.downLoadVersions(addon)) {
                                cancel();
                                return null;
                            }
                            return null;
                        }
                    }
            );
            thread.setDaemon(true);
            thread.start();
            try {
                //todo lägg till denna i settings
                Thread.sleep(fxSettings.getRefreshDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


    }


}
