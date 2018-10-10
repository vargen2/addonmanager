import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

public class Model {

    ObservableList<Game> games = FXCollections.observableArrayList();
    Property<Game> selectedGame = new SimpleObjectProperty<Game>();

    static Queue<File> searchForWowDirectorys() {
        var drives = File.listRoots();
        Queue<File> results = new ConcurrentLinkedQueue<>();
        Arrays.stream(drives).parallel().forEach(drive -> searchForWowDirectorys(results, drive));
        //results.stream().forEach(x -> System.out.println(x.getPath()));
        return results;
    }

    private static void searchForWowDirectorys(Queue<File> results, File parent) {
        var directorys = new File(parent.getPath()).listFiles(File::isDirectory);
        if (directorys == null)
            return;
        Arrays.stream(directorys).parallel().forEach(child -> finder.accept(results, child));
    }

    private static BiConsumer<Queue<File>, File> finder = (results, child) -> {
        if (child.getPath().contains("$"))
            return;
        if (child.getPath().contains("World of Warcraft")) {
            var valid = new File(child.getPath() + File.separator + "Interface" + File.separator + "AddOns");
            if (valid.isDirectory()) {
                results.add(child);
            }
        } else {
            searchForWowDirectorys(results, child);
        }
    };
}
