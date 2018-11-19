package addonmanager.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model implements Serializable {

    private final transient Object lock = new Object();
    private Game selectedGame;
    private List<Game> games = Collections.synchronizedList(new ArrayList<>());

    protected Model() {
    }

    public Model(Model model) {
        model.getGames().stream().forEach(x -> this.games.add(new Game(x)));
        this.games.stream().filter(x -> model.selectedGame.getDirectory().equals(x.getDirectory())).findAny().ifPresent(this::setSelectedGame);
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

    public boolean removeGame(Game game) {
        synchronized (lock) {
            return games.remove(game);
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
