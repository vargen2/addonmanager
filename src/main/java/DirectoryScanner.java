import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DirectoryScanner {

    private final AtomicInteger max = new AtomicInteger();
    private final AtomicInteger current = new AtomicInteger();
    public final DoubleProperty progress = new SimpleDoubleProperty();
    public final ObservableSet<File> fileObservableList = FXCollections.synchronizedObservableSet(FXCollections.observableSet(new HashSet<File>()));

    public void searchForWowDirectorys() {
        var drives = File.listRoots();

        max.set(0);
        progress.setValue(0);
        current.set(0);

        //System.out.println(progress.get());
        //Queue<File> results = new ConcurrentLinkedQueue<>();
        Queue<File> directorys = new ConcurrentLinkedQueue<>();
        Arrays.stream(drives).parallel().forEach(drive -> {
            var first = new File(drive.getPath()).listFiles(File::isDirectory);
            if (first == null)
                return;
            for (var d : first) {
                var second = new File(d.getPath()).listFiles(File::isDirectory);
                if (second == null)
                    continue;
                if (d.getPath().contains("$"))
                    continue;

                if (d.getPath().contains("World of Warcraft")) {
                    var valid = new File(d.getPath() + File.separator + "Interface" + File.separator + "AddOns");
                    if (valid.isDirectory()) {
//                        results.add(d);
                        //System.out.println("added from root");
                        fileObservableList.add(d);
                    }
                } else {
                    directorys.add(d);
                }
                for (var s : second) {
                    var third = new File(s.getPath()).listFiles(File::isDirectory);
                    if (third == null)
                        continue;
                    if (s.getPath().contains("$"))
                        continue;

                    if (s.getPath().contains("World of Warcraft")) {
                        var valid = new File(s.getPath() + File.separator + "Interface" + File.separator + "AddOns");
                        if (valid.isDirectory()) {
                            //System.out.println("added from root 2");
                            fileObservableList.add(s);
                        }
                    } else {
                        directorys.add(s);
                    }
                }
            }
        });
        max.set(directorys.size());
        progress.setValue((double) current.get() / (double) max.get());
        directorys.stream().parallel().forEach(this::searchDriveForWowDirectorys);

    }

    private void searchDriveForWowDirectorys(File parent) {
        var directorys = new File(parent.getPath()).listFiles(File::isDirectory);
        if (directorys == null) {
            current.addAndGet(1);
            progress.setValue((double) current.get() / (double) max.get());
            return;
        }
        Arrays.stream(directorys).parallel().forEach(finder);
        current.addAndGet(1);
        progress.setValue((double) current.get() / (double) max.get());
    }

    private void searchForWowDirectorys(File parent) {
        var directorys = new File(parent.getPath()).listFiles(File::isDirectory);
        if (directorys == null)
            return;
        Arrays.stream(directorys).parallel().forEach(finder);
    }

    private Consumer<File> finder = (child) -> {
        if (child.getPath().contains("$"))
            return;
        if (child.getPath().contains("World of Warcraft")) {
            var valid = new File(child.getPath() + File.separator + "Interface" + File.separator + "AddOns");
            if (valid.isDirectory()) {
                //System.out.println("added from consumer");
                fileObservableList.add(child);
            }
        } else {
            searchForWowDirectorys(child);
        }
    };
}
