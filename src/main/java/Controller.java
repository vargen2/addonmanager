import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.File;

public class Controller {

    @FXML
    private Button experimentButton, scanButton;
    @FXML
    private ChoiceBox<Game> gameChoiceBox;
    @FXML
    private TableView<Addon> tableView;
    @FXML
    private StackPane bottomStackPane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        Model model = new Model();

        gameChoiceBox.setItems(model.games);
        gameChoiceBox.valueProperty().bindBidirectional(model.selectedGame);

        progressBar.prefWidthProperty().bind(bottomStackPane.widthProperty());
        statusLabel.prefWidthProperty().bind(bottomStackPane.widthProperty());

        experimentButton.setOnAction(event -> {
            Experi exp = new Experi();
            exp.experimentRedirect("omni-cc");
        });

        scanButton.setOnAction(event -> {
            IntegerProperty counter=new SimpleIntegerProperty();
            DirectoryScanner ds=new DirectoryScanner();
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {

                    //updateProgress(1,100);
                    updateMessage("searching...");

                    //var directorys = Model.searchForWowDirectorys();

                    var directorys = ds.searchForWowDirectorys();

                    //updateProgress(90,100);
                    updateMessage("building...");
                    int i=0;
                    for (var directory : directorys) {
                        Game game = new Game("Wow " + (++i), directory.getPath(), File.separator + "Interface" + File.separator + "AddOns");
                        Platform.runLater(() -> {
                            if (tableView.itemsProperty().getValue().size() == 0)
                                model.selectedGame.setValue(game);
                            model.games.add(game);

                        });
                        Task<Void> refreshTask = new Task<>() {
                            @Override
                            protected Void call() {
                                game.refresh();
                                return null;
                            }
                        };
                        Thread t = new Thread(refreshTask);
                        t.setDaemon(true);
                        t.start();
                    }
                    counter.setValue(i);
                    //updateProgress(100,100);
                    updateMessage("done");
                    return null;
                }
            };
            task.setOnScheduled(event1 -> {
                statusLabel.textProperty().bind(task.messageProperty());
                //progressBar.progressProperty().bind(task.progressProperty());
                progressBar.progressProperty().bind(ds.progress);
                progressBar.setVisible(true);
            });
            task.setOnSucceeded(workerStateEvent -> {
                progressBar.setVisible(false);
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                statusLabel.setText("found "+counter.get()+" games");
            });
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();

        });

        model.selectedGame.addListener((observable, oldValue, newValue) -> tableView.setItems(newValue.addons));


        TableColumn<Addon, String> nameCol = new TableColumn<Addon, String>("Title");
        nameCol.setCellValueFactory(new PropertyValueFactory("title"));
        nameCol.setPrefWidth(200);
        TableColumn<Addon, String> gameVersionCol = new TableColumn<Addon, String>("Game Version");
        gameVersionCol.setCellValueFactory(new PropertyValueFactory("gameVersion"));
        gameVersionCol.setPrefWidth(100);

        tableView.getColumns().setAll(nameCol, gameVersionCol);

    }
}
