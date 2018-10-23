package addonmanager.app;

import addonmanager.core.Addon;
import addonmanager.core.Status;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class StatusCell extends TableCell<Addon, Status> {


    private final StackPane pane;
    private final Button button;
    private final Label label;
    private final ProgressBar progressBar;

    public StatusCell() {

        super();
        //getChildren().add(new Button("texte"));
        //getChildren().add(new Label("everythinf fine"));
        pane = new StackPane();
        button = new Button("");
        label = new Label("");
        StackPane.setAlignment(button, Pos.CENTER);
        StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
        pane.getChildren().add(button);
        pane.getChildren().add(label);
        button.setVisible(false);
        label.setVisible(false);

        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        StackPane.setAlignment(progressBar, Pos.TOP_CENTER);
        pane.getChildren().add(progressBar);

        var bf = new BackgroundFill(new Color(Math.random(), Math.random(), Math.random(), 1), null, null);
        pane.setBackground(new Background(bf));
        setGraphic(pane);
//        Platform.runLater(() -> {
//                    getTableRow().selectedProperty().addListener(new ChangeListener<Boolean>() {
//                        @Override
//                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                            System.out.println(getTableRow().getItem().getTitle() + " " + getTableRow().getItem().getVersion() + "" + getTableRow().getItem().statusProperty().getValue());
//                        }
//                    });
//                }
//        );
    }

    @Override
    protected void updateItem(Status item, boolean empty) {
        super.updateItem(item, empty);
        pane.layout();
        pane.requestLayout();
        var addon = getTableRow().getItem();

//        if (addon != null && addon.getTitle() != null && addon.getTitle().contains("rabber")) {
//            System.out.println(addon.getTitle() + " " + addon.getVersion() + " ");
//            System.out.println(label.getText());
//            if (item != null)
//                System.out.println(item.getLatestVersion());
//        }
        if (item == null) {
            progressBar.setVisible(false);
            button.setVisible(false);
            label.setText("item null");
            label.setVisible(true);
            return;

        }
        //System.out.println(addon.getTitle()+" "+addon.getVersion()+" "+item.getLatestVersion());
        if (item.getNewVersionsTask() != null) {
            progressBar.progressProperty().bind(item.getNewVersionsTask().progressProperty());
            progressBar.setVisible(true);
            button.setVisible(false);
            label.setVisible(false);
        } else {
            progressBar.setVisible(false);
            progressBar.progressProperty().unbind();
        }

        if (item.getLatestVersion() != null) {
            label.setText(item.getLatestVersion());
            label.setVisible(true);
            System.out.println(addon.getTitle() + " " + addon.getVersion() + " " + item.getLatestVersion() + " : " + label.getText());
        }

//        if (!empty)
//            setGraphic(pane);
//        else
//            System.out.println(empty);

    }


}
