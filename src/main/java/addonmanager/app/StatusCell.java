package addonmanager.app;

import addonmanager.core.Addon;
import addonmanager.core.Addon.State;
import addonmanager.core.Status;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class StatusCell extends TableCell<Addon, State> {


    private final StackPane pane;
    private final Button button;
    private final Label label;
    private final ProgressBar progressBar;
    private ObservableValue<State> statusObjectValue;

    public StatusCell() {
        super();

        //getChildren().add(new Button("texte"));
        //getChildren().add(new Label("everythinf fine"));
        this.pane = new StackPane();
        this.button = new Button("");
        this.label = new Label("");
        StackPane.setAlignment(this.button, Pos.TOP_CENTER);
        StackPane.setAlignment(this.label, Pos.BOTTOM_CENTER);
        this.pane.getChildren().add(this.button);
        this.pane.getChildren().add(this.label);
        this.button.setVisible(false);
        this.label.setVisible(false);
        this.progressBar = new ProgressBar();
        this.progressBar.setVisible(false);
        StackPane.setAlignment(this.progressBar, Pos.TOP_CENTER);
        this.pane.getChildren().add(this.progressBar);

        //var bf = new BackgroundFill(new Color(Math.random(), Math.random(), Math.random(), 1), null, null);
        //pane.setBackground(new Background(bf));
        //setGraphic(pane);
        // System.out.println("cnostructor");
//        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//        Platform.runLater(() -> {
//                    getTableRow().selectedProperty().addListener(new ChangeListener<Boolean>() {
//                        @Override
//                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                            System.out.println(getTableRow().getItem().getTitle() + " " + getTableRow().getItem().getVersion() + "" + getTableRow().getItem().statusProperty().getValue());
//                        }
//                    });
//                }
//        );
        setOnMouseClicked(event -> {
//            var addon = getTableRow().getItem();
//            String sp = "";
//            if (addon != null && addon.getStatus() != null)
//
//                System.out.println(getTableRow().getItem().getTitle() + " " + getTableRow().getItem().getVersion() + " label " + this.label + " labeltext " +
//                        this.label.getText() + " statusprop " + sp + " pane " + this.pane);
        });
    }

    //todo debugga
    // TODO: 2018-10-24 progressindicator instead
    @Override
    protected void updateItem(State item, boolean empty) {
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
            pane.setVisible(true);
            progressBar.progressProperty().unbind();

            final TableColumn<Addon, State> column = getTableColumn();
            statusObjectValue = column == null ? null : column.getCellObservableValue(getIndex());

            if (statusObjectValue != null) {

                var tempStatus = statusObjectValue.getValue();
//                if (addon != null && addon.getFolderName() != null&&tempStatus!=null && tempStatus.getFolderName() != null)
//                    System.out.println(addon.getTitle() + " " + tempStatus.getFolderName());

                if (tempStatus != null &&tempStatus==State.GETTING_VERSIONS&& addon!=null) {

                    progressBar.progressProperty().bind(addon.getNewVersionsTask().progressProperty());
                    progressBar.setVisible(true);
                    label.textProperty().bind(addon.getNewVersionsTask().messageProperty());
                    label.setVisible(true);
                    button.setVisible(false);
                    button.setText("");
                }


                if (tempStatus != null && tempStatus==State.CAN_UPDATE &&addon!=null) {
                    //System.out.println("hit ever");
                    //if(!label.textProperty().isBound()) {
                    label.textProperty().unbind();
                    label.setVisible(false);
                    progressBar.setVisible(false);
                    progressBar.progressProperty().unbind();
                    button.setText(addon.getFolderName());
                    button.setVisible(true);
                    //  label.setText("can donload");
                    //  label.setVisible(true);
                    //}
                }
                if (tempStatus != null && tempStatus==State.NONE &&addon!=null) {
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

    public static Callback<TableColumn<Addon, State>, TableCell<Addon, State>> cellFactory(){
        return param -> new StatusCell();
    }

}
