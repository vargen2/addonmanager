package addonmanager.app.gui;

import addonmanager.app.core.Addon;
import addonmanager.app.core.Addon.Status;
import addonmanager.app.gui.task.UpdateAddonTask;
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
//        pane.getChildren().add(button);
//        pane.getChildren().add(progressBar);
//        pane.getChildren().add(label);
        label.setPrefHeight(20);
        progressBar.setPrefHeight(20);
        pane = new StackPane(progressBar, button, label);
        setOnMouseClicked(event -> {
            var addon = getTableRow().getItem();
            if (addon != null && addon.getProjectUrl() != null)
                System.out.println(addon.getProjectUrl());
        });
    }

    //todo debugga
    // TODO: 2018-10-24 progressindicator instead
    @Override
    protected void updateItem(Status item, boolean empty) {
        //pane.layout();
        //pane.requestLayout();
        var addon = getTableRow().getItem();
//        if (item != null && addon != null) {
//            if(!item.getFolderName().equals(addon.getFolderName())) {
//                System.out.println("not equal");
//                return;
//            }
//        }

        super.updateItem(item, empty);
        if (empty) {
            pane.setVisible(false);
            setGraphic(null);
        } else {
            if (!pane.prefWidthProperty().isBound()) {
                pane.prefWidthProperty().bind(getTableColumn().widthProperty());
                //label.prefWidthProperty().bind(getTableColumn().widthProperty());
                button.prefWidthProperty().bind(getTableColumn().widthProperty());
                progressBar.prefWidthProperty().bind(getTableColumn().widthProperty());
            }
            pane.setVisible(true);
            progressBar.progressProperty().unbind();

            final TableColumn<Addon, Status> column = getTableColumn();
            statusObjectValue = column == null ? null : column.getCellObservableValue(getIndex());

            if (statusObjectValue != null) {

                var tempStatus = statusObjectValue.getValue();
//                if (addon != null && addon.getFolderName() != null&&tempStatus!=null && tempStatus.getFolderName() != null)
//                    System.out.println(addon.getTitle() + " " + tempStatus.getFolderName());

                if (tempStatus != null && (tempStatus == Status.GETTING_VERSIONS || tempStatus == Status.UPDATING) && addon != null) {

                    progressBar.progressProperty().bind(addon.getUpdateable().progressProperty());
                    progressBar.setVisible(true);
                    label.textProperty().bind(addon.getUpdateable().messageProperty());
                    label.setVisible(true);
                    button.setVisible(false);
                    button.setText("");
                }


                if (tempStatus != null && tempStatus == Status.CAN_UPDATE && addon != null) {
                    //System.out.println("hit ever");
                    //if(!label.textProperty().isBound()) {
                    label.textProperty().unbind();
                    label.setVisible(false);
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText("update");
                    button.setOnAction(event -> {
                        UpdateAddonTask updateAddonTask = new UpdateAddonTask(addon);
                        Thread t = new Thread(updateAddonTask);
                        t.setDaemon(true);
                        t.start();

                    });

                    button.setVisible(true);
                    //  label.setText("can donload");
                    //  label.setVisible(true);
                    //}
                }
//                if (tempStatus != null && tempStatus == Status.UPDATING && addon != null) {
//
//                    //addon.progressProperty().addListener((observable, oldValue, newValue) -> progressBar.setProgress((double)newValue));
//                    //progressBar.progressProperty().bind(addon.progressProperty());
//                    progressBar.progressProperty().bind(addon.getUpdateable().progressProperty());
//                    progressBar.setVisible(true);
//                    // addon.messageProperty().addListener((observable, oldValue, newValue) -> label.setText(newValue));
//                    label.textProperty().bind(addon.getUpdateable().messageProperty());
//                    label.setVisible(true);
//                    button.setVisible(false);
//                    button.setText("");
//                }

                if (tempStatus != null && tempStatus == Status.UP_TO_DATE && addon != null) {
                    //System.out.println("hit ever");
                    //if(!label.textProperty().isBound()) {
                    label.textProperty().unbind();
                    label.setVisible(true);
                    label.setText("up to date");
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText("");
                    button.setVisible(false);
                    //  label.setText("can donload");
                    //  label.setVisible(true);
                    //}
                }

                if (tempStatus != null && tempStatus == Status.NONE && addon != null) {
                    //System.out.println("hit ever");
                    //if(!label.textProperty().isBound()) {
                    label.textProperty().unbind();
                    label.setVisible(true);
                    label.setText("NONE");
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText("");
                    button.setVisible(false);
                    //  label.setText("can donload");
                    //  label.setVisible(true);
                    //}

                }

                //System.out.println("status obj value !=null");
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
        //if (item!=null &&item == getItem()) {
        //System.out.println("item==getitem()");
        //    return;
        //}
//        if (addon != null && addon.getTitle() != null && addon.getTitle().contains("rabber")) {
//            System.out.println(addon.getTitle() + " " + addon.getVersion() + " ");
//            System.out.println(label.getText());
//            if (item != null)
//                System.out.println(item.getLatestDownload());
//        }

//        if (empty) {
//            progressBar.setVisible(false);
//            button.setVisible(false);
//            label.setText("");
//            label.setVisible(false);
//            super.setGraphic(null);
//            System.out.println("empty");
//            return;
//
//        } else {
//            //System.out.println("hit1 " + ((addon != null) ? addon.getTitle() : "nulladdon"));
//            if (super.getGraphic() != pane) {
//                super.setGraphic(pane);
//                System.out.println("hit2 " + ((addon != null) ? addon.getTitle() : "nulladdon"));
//            }
//        }
//
//        //var addon = getTableRow().getItem();
//        if (addon == null || addon.statusProperty() == null || addon.statusProperty().get() == null)
//            return;
//
//        //System.out.println(addon.getTitle()+" "+addon.getVersion()+" "+item.getLatestDownload());
//        //System.out.println(addon.getFolderName() + " " + pane.getBackground().getFills().get(0).getFill().toString());
//        if (addon.statusProperty().get().getNewVersionsTask() != null) {
//            progressBar.progressProperty().bind(addon.statusProperty().get().getNewVersionsTask().progressProperty());
//            progressBar.setVisible(true);
//            button.setVisible(false);
//            label.setVisible(false);
//        } else {
//            progressBar.setVisible(false);
//            progressBar.progressProperty().unbind();
//        }
//
//        if (addon.statusProperty().get().getLatestDownload() != null) {
//            label.setText(addon.statusProperty().get().getLatestDownload());
//            label.setVisible(true);
//            //System.out.println(addon.getTitle() + " " + addon.getVersion() + " " + item.getLatestDownload() + " : " + label.getText());
//        }

//        if (!empty)
//            setGraphic(pane);
//        else
//            System.out.println(empty);

    }

    public static Callback<TableColumn<Addon, Status>, TableCell<Addon, Status>> cellFactory() {
        return param -> new StatusCell();
    }

}
