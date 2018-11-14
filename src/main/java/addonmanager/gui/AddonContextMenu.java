package addonmanager.gui;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Download;
import addonmanager.app.Updateable;
import addonmanager.gui.task.UpdateAddonTask;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.concurrent.ExecutionException;

public class AddonContextMenu extends ContextMenu {

    private Addon addon;
    private Task<Boolean> versionsListViewBuildingTask;

    public AddonContextMenu() {
        super();

        CheckMenuItem ignore = new CheckMenuItem("Ignore");
        ignore.setOnAction(event1 -> {
            if (addon.getStatus() != Addon.Status.IGNORE)
                App.Ignore(addon);
            else {
                Thread thread = new Thread(new Task() {
                    @Override
                    protected Object call() throws Exception {
                        Updateable updateable = Updateable.createUpdateable(this, this::updateMessage, this::updateProgress);
                        addon.setUpdateable(updateable);

                        if (!App.unIgnore(addon)) {
                            cancel();
                            return null;
                        }
                        return null;
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
        });

        CheckMenuItem alphaMenuItem = new CheckMenuItem("Alpha");
        CheckMenuItem betaMenuItem = new CheckMenuItem("Beta");
        CheckMenuItem releaseMenuItem = new CheckMenuItem("Release");
        alphaMenuItem.setOnAction(event -> App.setReleaseType(addon, Addon.ReleaseType.ALPHA));
        betaMenuItem.setOnAction(event -> App.setReleaseType(addon, Addon.ReleaseType.BETA));
        releaseMenuItem.setOnAction(event -> App.setReleaseType(addon, Addon.ReleaseType.RELEASE));

        ////////////////////////////////////////

        ListView<Download> listView = new ListView<>();
        listView.setPrefWidth(300);

        listView.setCellFactory(new Callback<ListView<Download>, ListCell<Download>>() {
            @Override
            public ListCell<Download> call(ListView<Download> param) {
                ListCell<Download> downloadListCell = new ListCell<Download>() {
                    @Override
                    protected void updateItem(Download item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            var hour = item.getFileDateUploaded().getHour() - LocalDateTime.now().getHour();
                            var period = Period.between(item.getFileDateUploaded().toLocalDate(), LocalDateTime.now().toLocalDate()).normalized();

                            String periodFrom = "";
                            if (period.getYears() != 0)
                                periodFrom += period.getYears() + "y ";
                            if (period.getMonths() != 0)
                                periodFrom += period.getMonths() + "m ";
                            if (period.getDays() != 0 && period.getYears() == 0)
                                periodFrom += period.getDays() + "d ";
                            if (hour != 0 && period.getYears() == 0 && period.getMonths() == 0)
                                periodFrom += hour + "h ";
                            periodFrom += "ago";

                            Label release = new Label(item.getRelease());
                            release.setPrefWidth(60);
                            Label version = new Label(item.getTitle());
                            version.setPrefWidth(90);
                            Label time = new Label(periodFrom);
                            time.setPrefWidth(90);
                            HBox hBox = new HBox(0, release, version, time);
                            this.setTooltip(new Tooltip("Click to change to this version.\n" + item.toDetailedStringLines()));
                            this.getTooltip().setShowDelay(Duration.millis(50));
                            this.getTooltip().setShowDuration(Duration.INDEFINITE);
                            setText(null);
                            setGraphic(hBox);
                        }

                    }
                };
                downloadListCell.setOnMouseClicked(event1 -> {

                    Download dl = downloadListCell.getItem();
                    if (dl == null)
                        return;
                    hide();
                    Window owner = getScene().getWindow();
                    Alert dlg = new Alert(Alert.AlertType.CONFIRMATION, "");
                    dlg.initModality(Modality.APPLICATION_MODAL);
                    dlg.initOwner(owner);
                    dlg.setTitle("Confirm");
                    dlg.getDialogPane().setGraphic(null);
                    dlg.getDialogPane().setHeaderText(null);
                    dlg.getDialogPane().setContentText("Do you want to change to " + dl.getRelease() + " " + dl.getTitle() + "?");
                    dlg.initStyle(StageStyle.DECORATED);
                    dlg.showAndWait().ifPresent(result -> {
                        if (result == ButtonType.OK) {
                            UpdateAddonTask updateAddonTask = new UpdateAddonTask(addon, dl);
                            Thread t = new Thread(updateAddonTask);
                            t.setDaemon(true);
                            t.start();
                        }
                    });
                    //popOver.hide();
                });

                return downloadListCell;
            }
        });
        CustomMenuItem versionsMenuItem = new CustomMenuItem(listView);

        Menu versionsMenu = new Menu("Versions");
        versionsMenu.setOnShowing(event -> {

            if (versionsListViewBuildingTask == null)
                return;
            if (versionsListViewBuildingTask.isDone())
                return;

            try {
                versionsListViewBuildingTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        });
        versionsMenu.getItems().add(versionsMenuItem);


        MenuItem infoMenuItem = new MenuItem("Info");
        Menu infoMenu = new Menu("Info");
        infoMenu.setOnShowing(event -> {
            infoMenuItem.setText(addon.getFolderName() + "\n" + addon.getProjectUrl() + "\n" + addon.getVersion());
        });
        infoMenu.getItems().add(infoMenuItem);

        getItems().addAll(ignore, alphaMenuItem, betaMenuItem, releaseMenuItem, versionsMenu, infoMenu);
        setOnShowing(event -> {
            ignore.setSelected(addon.getStatus() == Addon.Status.IGNORE);
            alphaMenuItem.setSelected(addon.getReleaseType() == Addon.ReleaseType.ALPHA);
            betaMenuItem.setSelected(addon.getReleaseType() == Addon.ReleaseType.BETA);
            releaseMenuItem.setSelected(addon.getReleaseType() == Addon.ReleaseType.RELEASE);
            versionsMenu.setDisable(addon.getDownloads().isEmpty());
            versionsListViewBuildingTask = new Task<>() {
                @Override
                protected Boolean call() {
                    listView.setItems(FXCollections.unmodifiableObservableList(FXCollections.observableList(addon.getDownloads())));
                    return true;
                }
            };


            Thread thread = new Thread(versionsListViewBuildingTask);
            thread.setDaemon(true);
            thread.start();

        });
    }


    public void show(Addon addon, Node anchor, double screenX, double screenY) {
        this.addon = addon;
        super.show(anchor, screenX, screenY);
    }
}
