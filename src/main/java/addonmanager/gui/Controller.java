package addonmanager.gui;

import addonmanager.app.*;
import addonmanager.app.file.Saver;
import addonmanager.app.file.TocRefresher;
import addonmanager.gui.fxapp.FXFactory;
import addonmanager.gui.fxapp.FXGame;
import addonmanager.gui.fxapp.FXModel;
import addonmanager.gui.setting.FXSettings;
import addonmanager.gui.setting.SettingsController;
import addonmanager.gui.tableview.ReleaseLatestVersionCell;
import addonmanager.gui.tableview.StatusCell;
import addonmanager.gui.task.FindGamesTask;
import addonmanager.gui.task.FoundGameTask;
import addonmanager.gui.task.RefreshGameTask;
import addonmanager.gui.task.UpdateAddonTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.controlsfx.control.TaskProgressView;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Controller {


    private static FXSettings fxSettings;

    @FXML
    private Button notSureButton, refreshButton, removeButton, settingsButton;
    @FXML
    private ChoiceBox gameChoiceBox;
    @FXML
    private TableView<Addon> tableView;
    @FXML
    private TableColumn<Addon, String> titleVersionCol, releaseLatestCol, gameVersionCol;
    @FXML
    private TableColumn<Addon, Addon.Status> stateCol;
    @FXML
    private TaskProgressView<Task<Void>> taskProgressView;
    @FXML
    private CustomTextField searchField;
    @FXML
    private TableColumn<CurseAddon, String> curseTitleCol, curseDescCol, curseDLCol, curseUpdatedCol, curseCreatedCol;
    @FXML
    private TableColumn<CurseAddon, CurseAddon.Status> curseInstallCol;
    @FXML
    private TableView<CurseAddon> getMoreTableView;
    @FXML
    private Tab getMoreTab;

    @FXML
    private void initialize() {
        TocRefresher.loadKnownSubFolders();
        App.setFactory(new FXFactory());
        var loadedModel = Saver.load(App.getFactory());
        App.model = loadedModel.orElse(App.getFactory().createModel());

        var appSettings = new AppSettings(Level.OFF, Level.OFF);
        fxSettings = new FXSettings(250);
        Saver.loadSettings(appSettings, fxSettings);
        App.init(appSettings);


        App.curseAddons = Saver.loadCurseAddons(App.getFactory());

        if (App.curseAddons != null && App.curseAddons.size() > 10) {
            curseTitleCol.setCellValueFactory(new PropertyValueFactory("title"));
            curseDescCol.setCellValueFactory(new PropertyValueFactory("description"));
            curseDLCol.setCellValueFactory(new PropertyValueFactory("downloads"));
            curseUpdatedCol.setCellValueFactory(new PropertyValueFactory("updatedEpoch"));
            curseCreatedCol.setCellValueFactory(new PropertyValueFactory("createdEpoch"));
            curseDescCol.setCellFactory(new Callback<TableColumn<CurseAddon, String>, TableCell<CurseAddon, String>>() {
                @Override
                public TableCell<CurseAddon, String> call(TableColumn<CurseAddon, String> param) {
                    TableCell tableCell = new TableCell<CurseAddon, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            if (item == getItem()) return;

                            super.updateItem(item, empty);

                            if (item == null) {
                                super.setText(null);
                                super.setGraphic(null);
                            } else {
                                super.setText(item);
                                super.setGraphic(null);
                            }
                        }
                    };
                    tableCell.setPrefHeight(44); //44+ needed to get 2 lines
                    tableCell.setWrapText(true);
                    tableCell.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);

                    return tableCell;
                }
            });
            curseInstallCol.setCellValueFactory(new PropertyValueFactory("status"));
            curseInstallCol.setCellFactory(new Callback<TableColumn<CurseAddon, CurseAddon.Status>, TableCell<CurseAddon, CurseAddon.Status>>() {
                @Override
                public TableCell<CurseAddon, CurseAddon.Status> call(TableColumn<CurseAddon, CurseAddon.Status> param) {
                    Button button = new Button("Install");
                    ProgressBar bar = new ProgressBar();
                    bar.setPrefWidth(140);
                    button.setPrefWidth(140);

                    TableCell tableCell = new TableCell<CurseAddon, CurseAddon.Status>() {

                        private final Button installButton = button;
                        private final ProgressBar progressBar = bar;

                        @Override
                        protected void updateItem(CurseAddon.Status item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(null);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                final TableColumn<CurseAddon, CurseAddon.Status> column = getTableColumn();
                                var statusObjectValue = column == null ? null : column.getCellObservableValue(getIndex());
                                CurseAddon.Status tempStatus = null;
                                if (statusObjectValue != null)
                                    tempStatus = statusObjectValue.getValue();

                                var addon = getTableRow().getItem();
                                var game = App.model.getSelectedGame();
                                if (tempStatus != null && addon != null && game != null) {
                                    if (tempStatus == CurseAddon.Status.UNKNOWN) {
                                        List<Addon> addonList = game.getAddons();
                                        if (addonList.stream().anyMatch(a ->
                                                a.getFolderName().equalsIgnoreCase(addon.getTitle()) ||
                                                        a.getTitle().equalsIgnoreCase(addon.getTitle()) ||
                                                        a.getFolderName().equalsIgnoreCase(addon.getAddonURL()) ||
                                                        a.getTitle().equalsIgnoreCase(addon.getAddonURL()) ||
                                                        (a.getProjectUrl() != null && a.getProjectUrl().contains(addon.getAddonURL()))
                                        )) {
                                            progressBar.progressProperty().unbind();
                                            addon.setStatus(CurseAddon.Status.INSTALLED);
                                            installButton.setText("Installed");
                                            installButton.setDisable(true);
                                            setGraphic(installButton);
                                        } else {
                                            progressBar.progressProperty().unbind();
                                            addon.setStatus(CurseAddon.Status.NOT_INSTALLED);
                                            installButton.setText("Install");
                                            installButton.setDisable(false);
                                            installButton.setOnAction(event -> {
                                                CompletableFuture.runAsync(new Task<Void>() {
                                                    @Override
                                                    protected Void call() {
                                                        System.out.println("start1");
                                                        Updateable updateable = Updateable.createUpdateable(this, this::updateMessage, this::updateProgress);
                                                        addon.setUpdateable(updateable);
//                                                        Platform.runLater(() -> {
//                                                            progressBar.progressProperty().bind(updateable.progressProperty());
//
//                                                        });


                                                        System.out.println("start2");
                                                        if (!App.installAddon(game, addon, updateable)) {

                                                            cancel();

                                                        }
                                                        addon.setStatus(CurseAddon.Status.UNKNOWN);
                                                        return null;
                                                    }
                                                });
                                                addon.setStatus(CurseAddon.Status.INSTALLING);
                                            });
                                            setGraphic(installButton);
                                        }
                                    } else if (tempStatus == CurseAddon.Status.INSTALLED) {
                                        progressBar.progressProperty().unbind();
                                        installButton.setText("Installed");
                                        installButton.setDisable(true);
                                        setGraphic(installButton);
                                    } else if (tempStatus == CurseAddon.Status.NOT_INSTALLED) {
                                        progressBar.progressProperty().unbind();
                                        installButton.setText("Install");
                                        installButton.setDisable(false);
                                        installButton.setOnAction(event -> {
                                            CompletableFuture.runAsync(new Task<Void>() {
                                                @Override
                                                protected Void call() {

                                                    Updateable updateable = Updateable.createUpdateable(this, this::updateMessage, this::updateProgress);
                                                    addon.setUpdateable(updateable);
//                                                    Platform.runLater(() -> {
//                                                        progressBar.progressProperty().bind(updateable.progressProperty());
//
//                                                    });


                                                    if (!App.installAddon(game, addon, updateable)) {

                                                        cancel();

                                                    }
                                                    addon.setStatus(CurseAddon.Status.UNKNOWN);
                                                    return null;
                                                }
                                            });
                                            addon.setStatus(CurseAddon.Status.INSTALLING);
                                        });
                                        setGraphic(installButton);
                                    } else if (tempStatus == CurseAddon.Status.INSTALLING) {
                                        progressBar.progressProperty().bind(addon.getUpdateable().progressProperty());
                                        setGraphic(progressBar);
                                    }

                                } else {
                                    progressBar.progressProperty().unbind();
                                    installButton.setText("No Game");
                                    installButton.setDisable(true);
                                    setGraphic(installButton);
                                }

                            }
                        }
                    };
                    tableCell.setPrefHeight(44); //44+ needed to get 2 lines
                    tableCell.setWrapText(true);
                    tableCell.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);

                    return tableCell;
                }
            });
            Callback<TableColumn<CurseAddon, String>, TableCell<CurseAddon, String>> dateCallback = new Callback<>() {
                @Override
                public TableCell<CurseAddon, String> call(TableColumn<CurseAddon, String> param) {
                    return new TableCell<>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            if (item == getItem()) return;

                            super.updateItem(item, empty);

                            if (item == null) {
                                super.setText(null);
                                super.setGraphic(null);
                            } else {

                                super.setText(LocalDate.ofInstant(Instant.ofEpochSecond(Long.parseLong(item)), ZoneId.systemDefault()).toString());//format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
                                super.setGraphic(null);
                            }
                        }
                    };

                }
            };
            curseUpdatedCol.setCellFactory(dateCallback);
            curseCreatedCol.setCellFactory(dateCallback);


            getMoreTableView.setItems(FXCollections.observableList(new ArrayList<>(App.curseAddons)));


            var provider = AddonSuggestionProvider.create(curseAddon -> curseAddon.getAddonURL() + " " + curseAddon.getTitle(), App.curseAddons);
            provider.setObservedList(getMoreTableView.getItems());
            var autoCompletionBinding = TextFields.bindAutoCompletion(searchField, provider);
            autoCompletionBinding.prefWidthProperty().bind(searchField.widthProperty());
            autoCompletionBinding.setOnAutoCompleted(new EventHandler<AutoCompletionBinding.AutoCompletionEvent<CurseAddon>>() {
                @Override
                public void handle(AutoCompletionBinding.AutoCompletionEvent<CurseAddon> param) {
                    if (param == null)
                        return;
                    CurseAddon curseAddon = param.getCompletion();
                    if (curseAddon == null)
                        return;
                    getMoreTableView.getItems().setAll(curseAddon);

//                    CurseAddon curseAddon = param.getCompletion();
//                    titleLabel.setText(curseAddon.getTitle());
//                    linkLabel.setOnAction(event -> {
//                        Hyperlink link = (Hyperlink) event.getSource();
//                        if (link == null)
//                            return;
//                        AddonContextMenu.openWebpage(URI.create(link.getText()));
//                    });
//                    linkLabel.setText("[" + curseAddon.getAddonURL() + "]");
//                    descriptionLabel.setText(curseAddon.getDescription());
//                    installButton.setOnAction(actionEvent -> {
//                        System.out.println("hit");
//                    });

                }
            });

        }
        getMoreTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                return;
            getMoreTableView.getItems().stream().forEach(curseAddon -> curseAddon.setStatus(CurseAddon.Status.UNKNOWN));
            //getMoreTableView.refresh();
        });

        var settingsController = new SettingsController(App.model, fxSettings);
        var addonContextMenu = new AddonContextMenu();

        gameChoiceBox.getItems().add(new Separator());

        ChoiceBoxItem add = new ChoiceBoxItem(o -> {

        }, "Add Games...");
        gameChoiceBox.getItems().add(add);
        ChoiceBoxItem manual = new ChoiceBoxItem(o -> {

        }, "Add Directory manually...");
        gameChoiceBox.getItems().add(manual);
        ChoiceBoxItem scan = new ChoiceBoxItem(o -> {
            Consumer<Game> consumer = game -> CompletableFuture.runAsync(new FoundGameTask(game, gameChoiceBox));
            FindGamesTask ds = new FindGamesTask(consumer, true);
            Platform.runLater(() -> taskProgressView.getTasks().add(ds));
            CompletableFuture.runAsync(ds);
        }, "Scan for Directories...");
        gameChoiceBox.getItems().add(scan);

        gameChoiceBox.setValue(add);
        gameChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            //System.out.println("triggred");
            if (((ChoiceBoxItem) newValue).getGame() != null) {
                gameChoiceBox.getItems().remove(add);
                App.setSelectedGame(((ChoiceBoxItem) newValue).getGame());
            }
            if (newValue == manual || newValue == scan) {
                ((ChoiceBoxItem) newValue).getConsumer().accept(0);
                if (gameChoiceBox.getItems().contains(oldValue) && !(oldValue == manual || oldValue == scan))
                    gameChoiceBox.setValue(oldValue);
            }
        });


        taskProgressView.getTasks().addListener((ListChangeListener<Task<Void>>) c -> {
            c.next();
            if (c.wasAdded()) {
                taskProgressView.setVisible(true);
                taskProgressView.setPrefHeight(taskProgressView.getPrefHeight() + 90);
            }
            if (c.wasRemoved()) {
                taskProgressView.setPrefHeight(taskProgressView.getPrefHeight() - 90);
                taskProgressView.setVisible(false);
            }
        });

        if (App.model instanceof FXModel) {
            ((FXModel) App.model).selectedGameProperty.addListener((observable, oldValue, newValue) -> {
                if (newValue instanceof FXGame) {
                    tableView.setItems(((FXGame) newValue).addonObservableList);
                } else {
                    tableView.setItems(null);
                    //System.err.println("game not FXGame");
                }
                refreshButton.setDisable(newValue == null);
                removeButton.setDisable(newValue == null);
                getMoreTableView.getItems().stream().forEach(curseAddon -> curseAddon.setStatus(CurseAddon.Status.UNKNOWN));
                getMoreTableView.refresh();
                notSureButton.setDisable(newValue == null);
            });
        } else {
            App.LOG.info("model not FXModel");
        }

        titleVersionCol.setCellValueFactory(new PropertyValueFactory("titleVersion"));
        releaseLatestCol.setCellValueFactory(new PropertyValueFactory("releaseLatest"));
        releaseLatestCol.setCellFactory(ReleaseLatestVersionCell.cellFactory());
        stateCol.setCellFactory(StatusCell.cellFactory());
        stateCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        gameVersionCol.setCellValueFactory(new PropertyValueFactory("gameVersion"));

        tableView.getColumns().setAll(titleVersionCol, releaseLatestCol, stateCol, gameVersionCol);
        tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {

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

        notSureButton.setOnAction(event -> {
            Game game = App.model.getSelectedGame();
            if (game == null)
                return;
            CompletableFuture.runAsync(() -> game.getAddons().
                    stream().
                    filter(a -> (a.getStatus() == Addon.Status.NOT_SURE && a.getLatestDownload() != null)).
                    forEach(addon ->
                    {
                        CompletableFuture.runAsync(new Task<Void>() {
                            @Override
                            protected Void call() {

                                UpdateAddonTask updateAddonTask = new UpdateAddonTask(addon, addon.getLatestDownload());
                                updateAddonTask.setOnSucceeded(workerStateEvent -> CompletableFuture.runAsync(() -> App.removeSubFoldersFromGame(addon)));
                                Thread t = new Thread(updateAddonTask);
                                t.setDaemon(true);
                                t.start();
                                return null;
                            }
                        });
                        Util.sleep(fxSettings.getRefreshDelay());

                    }));
        });

        //Icon.setIcon(refreshButton, FontAwesome.Glyph.REFRESH, Color.MEDIUMSEAGREEN);
        refreshButton.setOnAction(event -> {
            Game game = App.model.getSelectedGame();
            if (game == null)
                return;

            Thread thread = new Thread(new RefreshGameTask(game));
            thread.setDaemon(true);
            thread.start();
        });

        //Icon.setIcon(removeButton, FontAwesome.Glyph.REMOVE, Color.INDIANRED);
        removeButton.setOnAction(event -> {
            if (App.model.getSelectedGame() == null)
                return;
            Game game = App.model.getSelectedGame();
            App.removeGame(game);
            App.setSelectedGame(null);
            Optional<ChoiceBoxItem> gameChoiceBoxItem = gameChoiceBox.getItems().stream().filter(ChoiceBoxItem.class::isInstance).map(ChoiceBoxItem.class::cast).filter(x -> ((ChoiceBoxItem) x).getGame() == game).findFirst();
            gameChoiceBoxItem.ifPresent(gameChoiceBox.getItems()::remove);
            if (App.model.getGames().isEmpty()) {
                gameChoiceBox.getItems().add(0, add);
                gameChoiceBox.setValue(add);
            }

        });

        //Icon.setIcon(settingsButton, FontAwesome.Glyph.COG, Color.DARKSLATEGRAY);
        settingsButton.setOnAction(event -> {

            if (!settingsController.isShowing())
                settingsController.show(settingsButton);
            else
                settingsController.hide();
        });

        loadedModel.ifPresent((model1) -> {
            var selectedGame = App.model.getSelectedGame();
            App.model.getGames().forEach(game -> {
                ChoiceBoxItem cbi = new ChoiceBoxItem(game);
                gameChoiceBox.getItems().add(0, cbi);
                if (selectedGame != null && game == selectedGame) {
                    App.LOG.fine("selected game found: " + selectedGame.toString());
                    game.getAddons().forEach(addon -> App.LOG.fine("loaded addons: " + addon.getFolderName()));
                    gameChoiceBox.setValue(cbi);
                }
            });
            if (selectedGame instanceof FXGame) {
                tableView.setItems(((FXGame) selectedGame).addonObservableList);
                if (fxSettings.isAutoRefresh()) {
                    CompletableFuture.runAsync(() -> {
                        Util.sleep(2000);
                        CompletableFuture.runAsync(new RefreshGameTask(selectedGame));
                    });
                    //todo lägg till auto donwload på individ addons lägg till på completable future andthen
                }

            }
            refreshButton.setDisable(selectedGame == null);
            removeButton.setDisable(selectedGame == null);

        });

        tableView.getSortOrder().add(stateCol);
        tableView.sort();


    }


    public static void refreshFromNet(Game game) {

        game.getAddons().forEach(addon -> {
            CompletableFuture.runAsync(new Task<Void>() {
                @Override
                protected Void call() {
                    addon.setUpdateable(Updateable.createUpdateable(this, this::updateMessage, this::updateProgress));
                    if (!App.downLoadVersions(addon))
                        cancel();
                    return null;
                }
            });
            Util.sleep(fxSettings.getRefreshDelay());

        });


    }


}
