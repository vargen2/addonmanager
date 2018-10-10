import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.File;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class DirectoryScanner {

    private AtomicInteger max = new AtomicInteger();
    private AtomicInteger current = new AtomicInteger();
    public DoubleProperty progress = new SimpleDoubleProperty();

    public Queue<File> searchForWowDirectorys() {
        var drives = File.listRoots();

        max.set(10000);
        progress.setValue(0);
        current.set(0);

        System.out.println(progress.get());
        Queue<File> directorys = new ConcurrentLinkedQueue<>();
        Arrays.stream(drives).parallel().forEach(drive -> {
//            var first = new File(drive.getPath()).listFiles(File::isDirectory);
//            if (first == null)
//                return;
//            for (var d : first) {
//                //System.out.println(d.canRead());
//                var second = new File(d.getPath()).listFiles(File::isDirectory);
//                if (second == null)
//                    continue;
//                if (d.getPath().contains("$"))
//                    continue;
//
//                max.addAndGet(1);
//                progress.setValue((double) current.get() / (double) max.get());
//            }
            searchForWowDirectorys(directorys,drive);
        });
        Queue<File> results = new ConcurrentLinkedQueue<>();
        //results.stream().forEach(x -> System.out.println(x.getPath()));
        return results;
    }

    private void searchDriveForWowDirectorys(Queue<File> results, File parent) {
        var directorys = new File(parent.getPath()).listFiles(File::isDirectory);
        if (directorys == null)
            return;
        max.addAndGet(directorys.length);
        progress.setValue((double) current.get() / (double) max.get());
        Arrays.stream(directorys).parallel().forEach(child -> finder.accept(results, child));
        //current.addAndGet(1);
        progress.setValue((double) current.get() / (double) max.get());

    }

    private void searchForWowDirectorys(Queue<File> results, File parent) {
        var directorys = new File(parent.getPath()).listFiles(File::isDirectory);
        if (directorys == null) {
            //current.addAndGet(1);
            progress.setValue((double) current.get() / (double) max.get());
            return;
        }
        max.addAndGet(directorys.length);
        progress.setValue((double) current.get() / (double) max.get());
        Arrays.stream(directorys).parallel().forEach(child -> finder.accept(results, child));
        current.addAndGet(1);
        progress.setValue((double) current.get() / (double) max.get());

    }

    private BiConsumer<Queue<File>, File> finder = (results, child) -> {
        if (child.getPath().contains("$")) {
            //current.addAndGet(1);
            progress.setValue((double) current.get() / (double) max.get());
            return;
        }
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
