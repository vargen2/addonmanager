package addonmanager.app;

import addonmanager.core.Game;

import java.util.function.Consumer;

public class ChoiceBoxItem {

    private Game game;
    private Consumer consumer;
    private String name;

    public ChoiceBoxItem(Game game) {
        this.game = game;
    }

    public ChoiceBoxItem(Consumer consumer, String name) {
        this.consumer = consumer;
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if(game!=null)
            return game.toString();
        return name;
    }
}
