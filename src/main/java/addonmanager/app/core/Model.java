package addonmanager.app.core;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

 public class Model {

    private Game selectedGame;

    protected Model(){

    }

    public final ObservableList<Game> games = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    public Game getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(Game selectedGame) {
        this.selectedGame = selectedGame;
    }
}
