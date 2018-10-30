package addonmanager.app;

import addonmanager.core.Addon;
import addonmanager.core.Addon.State;
import addonmanager.core.Download;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;

public class ReleaseLatestVersionCell extends TableCell<Addon, String> {

    private PopOver popOver;

    public ReleaseLatestVersionCell() {
        super();

        setOnMouseClicked(event -> {

            if (popOver != null && popOver.isShowing()) {
                popOver.hide();
                return;
            }

            var addon = getTableRow().getItem();
            if (addon == null)
                return;

            ToggleButton b1 = new ToggleButton("release");
            b1.setUserData(Addon.ReleaseType.RELEASE);
            ToggleButton b2 = new ToggleButton("beta");
            b2.setUserData(Addon.ReleaseType.BETA);
            ToggleButton b3 = new ToggleButton("alpha");
            b3.setUserData(Addon.ReleaseType.ALPHA);

            if (addon.getReleaseType() == Addon.ReleaseType.RELEASE)
                b1.setSelected(true);
            else if (addon.getReleaseType() == Addon.ReleaseType.BETA)
                b2.setSelected(true);
            else if (addon.getReleaseType() == Addon.ReleaseType.ALPHA)
                b3.setSelected(true);

            VBox vBox=new VBox();
            SegmentedButton segmentedButton = new SegmentedButton(b1, b2, b3);
            vBox.getChildren().add(segmentedButton);
            ListView<Download> listView=new ListView<>();
            listView.setItems(FXCollections.unmodifiableObservableList(FXCollections.observableList(addon.getDownloads())));
            vBox.getChildren().add(listView);

            popOver = new PopOver(vBox);
            segmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == null)
                    return;
                if (newValue != null && newValue.getUserData() instanceof Addon.ReleaseType) {
                    addon.setReleaseType((Addon.ReleaseType) newValue.getUserData());
                    popOver.hide();

                }
            });
            //popOver.setTitle("ti ");
            popOver.setAnimated(true);
            //popOver.setCloseButtonEnabled(true);
            //popOver.setHeaderAlwaysVisible(true);
            popOver.setDetachable(false);
            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

            popOver.show(this);

        });
    }

    @Override
    public void updateItem(String item, boolean empty) {
        if (item == getItem()) return;

        super.updateItem(item, empty);

        if (item == null) {
            super.setText(null);
            super.setGraphic(null);
        } else {
            super.setText(item.toString());
            super.setGraphic(null);
        }
    }


    public static Callback<TableColumn<Addon, String>, TableCell<Addon, String>> cellFactory() {
        return param -> new ReleaseLatestVersionCell();
    }

}
