import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;

public class Controller {

    @FXML
    private Button experimentButton, scanButton;
    @FXML
    private ChoiceBox<Game> gameChoiceBox;
    @FXML
    private TableView<Addon> tableView;

    @FXML
    private void initialize() {
        Model model = new Model();

        gameChoiceBox.setItems(model.games);

        experimentButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Experi exp = new Experi();
                exp.experimentRedirect("omni-cc");
            }
        });

        scanButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Game game = new Game("Wow", "D:" + File.separator + "Program (x86)" + File.separator + "World of Warcraft", File.separator + "Interface" + File.separator + "AddOns");
                model.games.add(game);
                gameChoiceBox.valueProperty().bindBidirectional(model.selectedGame);
                model.selectedGame.setValue(game);
                game.refresh();
                if (tableView.itemsProperty().getValue().size() == 0) {
                    System.out.println("kom hit " + game.addons.size());

                    tableView.setItems(model.selectedGame.getValue().addons);
                }
            }
        });

        TableColumn<Addon, String> nameCol = new TableColumn<Addon, String>("Title");
        nameCol.setCellValueFactory(new PropertyValueFactory("title"));
        nameCol.setPrefWidth(200);
        TableColumn<Addon, String> gameVersionCol = new TableColumn<Addon, String>("Game Version");
        gameVersionCol.setCellValueFactory(new PropertyValueFactory("gameVersion"));
        gameVersionCol.setPrefWidth(100);

        tableView.getColumns().setAll(nameCol, gameVersionCol);

    }
}
