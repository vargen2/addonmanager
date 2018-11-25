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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.HyperlinkLabel;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// TODO: 2018-11-22 show most recent update download, maybe under info
public class AddonContextMenu extends ContextMenu {

    private Addon addon;
    private CompletableFuture versionsListViewBuildingTask;

    public AddonContextMenu() {
        super();

        CheckMenuItem ignore = new CheckMenuItem("Ignore");
        ignore.setOnAction(event1 -> {
            if (addon.getStatus() != Addon.Status.IGNORE)
                App.Ignore(addon);
            else {
                CompletableFuture.runAsync(new Task() {
                    @Override
                    protected Object call() {
                        Updateable updateable = Updateable.createUpdateable(this, this::updateMessage, this::updateProgress);
                        addon.setUpdateable(updateable);
                        if (!App.unIgnore(addon))
                            cancel();
                        return null;
                    }
                });
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
                            var hour = LocalDateTime.now().getHour() - item.getFileDateUploaded().getHour();
                            var period = Period.between(item.getFileDateUploaded().toLocalDate(), LocalDateTime.now().toLocalDate()).normalized();

                            String periodFrom = period.getYears() != 0 ? period.getYears() + "y " : "";
                            periodFrom += period.getMonths() != 0 ? period.getMonths() + "m " : "";
                            periodFrom += period.getDays() != 0 && period.getYears() == 0 ? (hour < 0 ? period.getDays() - 1 : period.getDays()) + "d " : "";
                            periodFrom += period.getDays() == 0 && hour != 0 && period.getYears() == 0 && period.getMonths() == 0 ? (hour < 0 ? 24 + hour : hour) + "h " : "";
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
                    //todo fel theme
                    Window owner = getScene().getWindow();
                    Alert dlg = new Alert(Alert.AlertType.CONFIRMATION, "");
                    dlg.initModality(Modality.APPLICATION_MODAL);
                    dlg.initOwner(owner);
                    dlg.setTitle("Confirm");
                    dlg.getDialogPane().setGraphic(null);
                    dlg.getDialogPane().setHeaderText(null);
                    dlg.getDialogPane().setContentText("Do you want to change to " + dl.getRelease() + " " + dl.getTitle() + "?");
                    dlg.getDialogPane().getStylesheets().add("JMetroLightTheme.css");
                    //dlg.initStyle(StageStyle.DECORATED);
                    dlg.showAndWait().ifPresent(result -> {
                        if (result == ButtonType.OK) {
                            UpdateAddonTask updateAddonTask = new UpdateAddonTask(addon, dl);
                            updateAddonTask.setOnSucceeded(workerStateEvent -> CompletableFuture.runAsync(() -> App.removeSubFoldersFromGame(addon)));
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
            if (versionsListViewBuildingTask == null || versionsListViewBuildingTask.isDone())
                return;

            try {
                versionsListViewBuildingTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        versionsMenu.getItems().add(versionsMenuItem);


        HyperlinkLabel projectLink = new HyperlinkLabel();
        projectLink.setOnAction(event -> {
            Hyperlink link = (Hyperlink) event.getSource();
            if (link == null)
                return;
            openWebpage(URI.create(link.getText()));
        });
        CustomMenuItem projectURL = new CustomMenuItem(projectLink);
        projectURL.setId("hyperlinkmenu");

        MenuItem infoMenuItem = new MenuItem();


        Menu infoMenu = new Menu("Info");
        infoMenu.setOnShowing(event -> {
            infoMenuItem.setText(addon.toDetailedString());
            projectLink.setText("[" + addon.getProjectUrl() + "]");
        });
        infoMenu.getItems().addAll(infoMenuItem, projectURL);
//        infoMenu.setId("fancytext");
//        infoMenu.setStyle("#fancytext:focused {-fx-background-color: #cccccc; }");

        getItems().addAll(ignore, alphaMenuItem, betaMenuItem, releaseMenuItem, versionsMenu, infoMenu);
        setOnShowing(event -> {
            ignore.setSelected(addon.getStatus() == Addon.Status.IGNORE);
            alphaMenuItem.setSelected(addon.getReleaseType() == Addon.ReleaseType.ALPHA);
            betaMenuItem.setSelected(addon.getReleaseType() == Addon.ReleaseType.BETA);
            releaseMenuItem.setSelected(addon.getReleaseType() == Addon.ReleaseType.RELEASE);
            versionsMenu.setDisable(addon.getDownloads().isEmpty());
            versionsListViewBuildingTask = CompletableFuture.runAsync(() -> listView.setItems(FXCollections.unmodifiableObservableList(FXCollections.observableList(addon.getDownloads()))));
        });
    }


    public void show(Addon addon, Node anchor, double screenX, double screenY) {
        this.addon = addon;
        super.show(anchor, screenX, screenY);
    }

    public static boolean openWebpage(URI uri) {
        java.awt.Desktop desktop = java.awt.Desktop.isDesktopSupported() ? java.awt.Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
