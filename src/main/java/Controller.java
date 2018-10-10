import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
gameChoiceBox.valueProperty().bindBidirectional(model.selectedGame);

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
                int i=0;
                var directorys= Model.searchForWowDirectorys();
                for(var directory:directorys){
                    Game game=new Game("Wow "+(++i),directory.getPath(),File.separator + "Interface" + File.separator + "AddOns");
                    if (tableView.itemsProperty().getValue().size() == 0)
                        model.selectedGame.setValue(game);
                    model.games.add(game);
                    game.refresh();
                }
                //Game game = new Game("Wow", "D:" + File.separator + "Program (x86)" + File.separator + "World of Warcraft", File.separator + "Interface" + File.separator + "AddOns");
                //model.games.add(game);

                //model.selectedGame.setValue(game);

//                if (tableView.itemsProperty().getValue().size() == 0) {
//                    System.out.println("kom hit " + game.addons.size());
//
//                    tableView.setItems(model.selectedGame.getValue().addons);
//                }
            }
        });

        model.selectedGame.addListener(new ChangeListener<Game>() {


            @Override
            public void changed(ObservableValue<? extends Game> observable, Game oldValue, Game newValue) {

                tableView.setItems(newValue.addons);
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
