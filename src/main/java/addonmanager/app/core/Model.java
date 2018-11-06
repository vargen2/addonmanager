package addonmanager.app.core;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

    public final ObservableList<Game> games = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    public final Property<Game> selectedGame = new SimpleObjectProperty<Game>();


}
