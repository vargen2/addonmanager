package addonmanager.gui.fxapp;

import addonmanager.app.Game;
import addonmanager.app.Model;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class FXModel extends Model {

    public final Property<Game> selectedGameProperty = new SimpleObjectProperty<Game>();

    FXModel() {
        super();
    }

    public FXModel(Model model) {
        super();
        model.getGames().forEach(x -> addGame(new FXGame(x)));
        getGames().stream().filter(x -> model.getSelectedGame().getDirectory().equals(x.getDirectory())).findAny().ifPresent(this::setSelectedGame);
    }

    @Override
    protected void setSelectedGame(Game selectedGame) {
        super.setSelectedGame(selectedGame);
        selectedGameProperty.setValue(selectedGame);
    }
}
