package addonmanager.gui.tableview;

import addonmanager.app.Addon;
import addonmanager.app.Addon.Status;
import addonmanager.app.App;
import addonmanager.gui.task.UpdateAddonTask;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.util.concurrent.CompletableFuture;

public class StatusCell extends TableCell<Addon, Status> {

    private final StackPane pane;
    private final Button button;
    private final Label label;
    private final ProgressBar progressBar;
    private ObservableValue<Status> statusObjectValue;

    public StatusCell() {
        super();

        button = new Button("");
        label = new Label("");
        progressBar = new ProgressBar();
        button.setVisible(false);
        label.setVisible(false);
        progressBar.setVisible(false);
        StackPane.setAlignment(button, Pos.CENTER);
        StackPane.setAlignment(label, Pos.CENTER);
        StackPane.setAlignment(progressBar, Pos.CENTER);
        label.setPrefHeight(20);
        progressBar.setPrefHeight(20);
        pane = new StackPane(progressBar, button, label);
        // button.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);

    }


    @Override
    protected void updateItem(Status item, boolean empty) {
        var addon = getTableRow().getItem();
        super.updateItem(item, empty);
        if (empty) {
            pane.setVisible(false);
            setGraphic(null);
        } else {
            if (!pane.prefWidthProperty().isBound()) {
                pane.prefWidthProperty().bind(getTableColumn().widthProperty());
                button.prefWidthProperty().bind(getTableColumn().widthProperty());
                progressBar.prefWidthProperty().bind(getTableColumn().widthProperty());
            }
            pane.setVisible(true);
            progressBar.progressProperty().unbind();

            final TableColumn<Addon, Status> column = getTableColumn();
            statusObjectValue = column == null ? null : column.getCellObservableValue(getIndex());
            Status tempStatus = null;
            if (statusObjectValue != null)
                tempStatus = statusObjectValue.getValue();

            if (tempStatus != null && addon != null) {

                if (tempStatus == Status.GETTING_VERSIONS || tempStatus == Status.UPDATING) {
                    progressBar.progressProperty().bind(addon.getUpdateable().progressProperty());
                    progressBar.setVisible(true);
                    label.textProperty().bind(addon.getUpdateable().messageProperty());
                    label.setVisible(true);
                    label.setOpacity(1);
                    button.setVisible(false);
                    button.setText("");
                } else if (tempStatus == Status.CAN_UPDATE) {
                    label.textProperty().unbind();
                    label.setVisible(false);
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText("update");
                    button.setVisible(true);
                    button.setOnAction(event -> {
                        UpdateAddonTask updateAddonTask = new UpdateAddonTask(addon, addon.getLatestDownload());
                        updateAddonTask.setOnSucceeded(workerStateEvent -> CompletableFuture.runAsync(() -> App.removeSubFoldersFromGame(addon)));
                        Thread t = new Thread(updateAddonTask);
                        t.setDaemon(true);
                        t.start();

                    });
                } else if (tempStatus == Status.NOT_SURE || tempStatus == Status.UP_TO_DATE || tempStatus == Status.NONE || tempStatus == Status.IGNORE) {
                    label.textProperty().unbind();
                    label.setVisible(true);
                    label.setOpacity(1);
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText("");
                    button.setVisible(false);
                }


                if (tempStatus == Status.NOT_SURE) {
                    label.setText("not sure");
                } else if (tempStatus == Status.UP_TO_DATE) {
                    label.setText("up to date");
                } else if (tempStatus == Status.NONE) {
                    label.setText(" - ");
                } else if (tempStatus == Status.IGNORE) {
                    label.setText("Ignored");
                    label.setOpacity(0.4);
                }

            } else if (item != null) {
                App.LOG.severe("statuscell item !=null");

            }

            setGraphic(pane);
        }

    }

    public static Callback<TableColumn<Addon, Status>, TableCell<Addon, Status>> cellFactory() {
        return param -> new StatusCell();
    }

}
