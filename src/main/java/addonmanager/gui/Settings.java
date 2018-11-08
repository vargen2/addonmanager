package addonmanager.gui;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Model;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

public class Settings {


    private VBox releaseTypeVBox;
    private VBox rootVBox;
    private PopOver popOver;
    private Model model;


    public Settings(Model model) {
        this.model = model;

    }

    private void init() {
        Label releaseTypeLabel = new Label("Set all");
        Button b1 = new Button("release");
        Button b2 = new Button("beta");
        Button b3 = new Button("alpha");
        b1.setOnAction(event -> App.setReleaseType(model.getSelectedGame(), Addon.ReleaseType.RELEASE));
        b2.setOnAction(event -> App.setReleaseType(model.getSelectedGame(), Addon.ReleaseType.BETA));
        b3.setOnAction(event -> App.setReleaseType(model.getSelectedGame(), Addon.ReleaseType.ALPHA));
        HBox releaseTypeHBox = new HBox(0, b1, b2, b3);
        releaseTypeVBox = new VBox(0, releaseTypeLabel, releaseTypeHBox);
        rootVBox = new VBox(0, releaseTypeVBox);
        popOver = new PopOver(rootVBox);
        popOver.setAnimated(true);
        popOver.setDetachable(false);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
    }

    private void refresh() {
        if (model.getSelectedGame() == null) {
            rootVBox.getChildren().remove(releaseTypeVBox);
        } else {
            if (!rootVBox.getChildren().contains(releaseTypeVBox))
                rootVBox.getChildren().addAll(releaseTypeVBox);
        }

    }

    public void show(Node node) {
        if (popOver == null)
            init();
        refresh();
        popOver.show(node);

    }

    public void hide() {
        if (popOver == null)
            return;
        popOver.hide();
    }

    public boolean isShowing() {
        if (popOver == null)
            return false;
        return popOver.isShowing();
    }
}
