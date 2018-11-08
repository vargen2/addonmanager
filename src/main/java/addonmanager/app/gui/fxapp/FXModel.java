package addonmanager.app.gui.fxapp;

import addonmanager.app.core.Game;
import addonmanager.app.core.Model;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class FXModel extends Model {

    public final Property<Game> selectedGameProperty = new SimpleObjectProperty<Game>();

    FXModel() {
        super();
    }

    @Override
    public void setSelectedGame(Game selectedGame) {
        super.setSelectedGame(selectedGame);
        selectedGameProperty.setValue(selectedGame);
    }
}
