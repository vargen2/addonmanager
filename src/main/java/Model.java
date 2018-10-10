import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

    ObservableList<Game> games = FXCollections.observableArrayList();
    Property<Game> selectedGame= new SimpleObjectProperty<Game>();
}
