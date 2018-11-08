package addonmanager.app.core;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model {

    private final Object lock = new Object();
    private Game selectedGame;
    private List<Game> games = Collections.synchronizedList(new ArrayList<>());

    protected Model() {

    }

    public List<Game> getGames() {
        return Collections.unmodifiableList(games);
    }

    public boolean addGame(Game game) {
        synchronized (lock) {
            if (games.contains(game))
                return false;
            if (games.stream().anyMatch(x -> x.getDirectory().equals(game.getDirectory())))
                return false;
            games.add(game);
            return true;
        }
    }

    //    public final ObservableList<Game> games = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    public Game getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(Game selectedGame) {
        this.selectedGame = selectedGame;
    }
}
