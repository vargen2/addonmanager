package addonmanager.app;

import addonmanager.core.Addon;
import addonmanager.core.Addon.State;
import addonmanager.core.Download;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;

public class ReleaseLatestVersionCell extends TableCell<Addon, String> {

    private static final Tooltip releaseButtonToolTip=new Tooltip("Set this preferred release type");
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

            b1.setTooltip(releaseButtonToolTip);
            b2.setTooltip(releaseButtonToolTip);
            b3.setTooltip(releaseButtonToolTip);

            VBox vBox = new VBox();

            SegmentedButton segmentedButton = new SegmentedButton(b1, b2, b3);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().add(segmentedButton);
            ListView<Download> listView = new ListView<>(FXCollections.unmodifiableObservableList(FXCollections.observableList(addon.getDownloads())));
            listView.setPrefWidth(300);
            vBox.getChildren().add(listView);
            listView.setCellFactory(new Callback<ListView<Download>, ListCell<Download>>() {
                @Override
                public ListCell<Download> call(ListView<Download> param) {
                    return new ListCell<Download>() {
                        @Override
                        protected void updateItem(Download item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                var hour = item.fileDateUploaded.getHour() - LocalDateTime.now().getHour();
                                var period = Period.between(item.fileDateUploaded.toLocalDate(), LocalDateTime.now().toLocalDate()).normalized();

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

                                Label release = new Label(item.release);
                                release.setPrefWidth(60);
                                Label version = new Label(item.title);
                                version.setPrefWidth(90);
                                Label time = new Label(periodFrom);
                                time.setPrefWidth(90);
                                HBox hBox = new HBox(0, release, version, time);
                                this.setTooltip(new Tooltip(item.toDetailedString()));
                                setText(null);
                                setGraphic(hBox);
                            }

                        }
                    };
                }
            });

            popOver = new PopOver(vBox);
            segmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == null)
                    return;
                if (newValue != null && newValue.getUserData() instanceof Addon.ReleaseType) {
                    addon.setReleaseType((Addon.ReleaseType) newValue.getUserData());
                    popOver.hide();

                }
            });

            popOver.setAnimated(true);
            popOver.setDetachable(false);
            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);

            popOver.show(this);
            popOver.setAnchorX(popOver.getAnchorX() + 30);

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
