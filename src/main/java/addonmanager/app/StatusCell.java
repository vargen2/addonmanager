package addonmanager.app;

import addonmanager.core.Addon;
import addonmanager.core.Status;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.layout.StackPane;

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
    }

    @Override
    protected void updateItem(Status item, boolean empty) {
        super.updateItem(item, empty);
        var addon = getTableRow().getItem();

        if (addon != null && addon.getTitle() != null && addon.getTitle().contains("rabber")) {
            System.out.println(addon.getTitle() + " " + addon.getVersion() + " ");
            System.out.println(label.getText());
            if (item != null)
                System.out.println(item.getLatestVersion());
        }
        if (item == null)
            return;

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

        }

        if (!empty)
            setGraphic(pane);
    }


}
