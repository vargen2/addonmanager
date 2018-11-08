package addonmanager.gui.tableview;

import addonmanager.app.Addon;
import addonmanager.app.Addon.Status;
import addonmanager.gui.task.UpdateAddonTask;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

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
        setOnMouseClicked(event -> {
            var addon = getTableRow().getItem();
            if (addon != null && addon.getProjectUrl() != null)
                System.out.println(addon.getProjectUrl());
        });
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

            if (statusObjectValue != null) {
                var tempStatus = statusObjectValue.getValue();
                if (tempStatus != null && (tempStatus == Status.GETTING_VERSIONS || tempStatus == Status.UPDATING) && addon != null) {

                    progressBar.progressProperty().bind(addon.getUpdateable().progressProperty());
                    progressBar.setVisible(true);
                    label.textProperty().bind(addon.getUpdateable().messageProperty());
                    label.setVisible(true);
                    button.setVisible(false);
                    button.setText("");
                }

                if (tempStatus != null && tempStatus == Status.CAN_UPDATE && addon != null) {
                    label.textProperty().unbind();
                    label.setVisible(false);
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText("update");
                    button.setVisible(true);
                    button.setOnAction(event -> {
                        UpdateAddonTask updateAddonTask = new UpdateAddonTask(addon);
                        Thread t = new Thread(updateAddonTask);
                        t.setDaemon(true);
                        t.start();

                    });
                }
                if (tempStatus != null && tempStatus == Status.UP_TO_DATE && addon != null) {
                    label.textProperty().unbind();
                    label.setVisible(true);
                    label.setText("up to date");
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText("");
                    button.setVisible(false);
                }

                if (tempStatus != null && tempStatus == Status.NONE && addon != null) {
                    label.textProperty().unbind();
                    label.setVisible(true);
                    label.setText(" - ");
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText("");
                    button.setVisible(false);
                }

            } else if (item != null) {
                System.out.println("item !=null");
//                if (item.getNewVersionsTask() != null) {
//                    progressBar.setProgress(item.getNewVersionsTask().getProgress());
//                    progressBar.setVisible(true);
//                } else {
//                    progressBar.setVisible(false);
//                    progressBar.progressProperty().unbind();
//                }

//                if(item.getLatestDownload() !=null){
//                    label.setText(item.getLatestDownload());
//                    label.setVisible(true);
//                }else {
//                    label.setText("item");
//                    label.setVisible(true);
//                }
            }

            setGraphic(pane);
        }

    }

    public static Callback<TableColumn<Addon, Status>, TableCell<Addon, Status>> cellFactory() {
        return param -> new StatusCell();
    }

}
