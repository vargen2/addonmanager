import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.ChoiceBox;
import org.controlsfx.control.TaskProgressView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DirectoryScanner2 extends Task<Void> {

    private final AtomicInteger max = new AtomicInteger();
    private final AtomicInteger current = new AtomicInteger();
    //public final DoubleProperty progress = new SimpleDoubleProperty();
    public final ObservableSet<File> fileObservableList = FXCollections.synchronizedObservableSet(FXCollections.observableSet(new HashSet<File>()));

    public DirectoryScanner2(Model model, ChoiceBox cb, TaskProgressView taskProgressView) {
        AtomicInteger counter = new AtomicInteger(0);

        SetChangeListener<File> fileListener = change -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {

                    for(var g:model.games) {
                        if(g.getDirectory().equals(change.getElementAdded().getPath()))
                            return null;
                    }
                    int index=counter.addAndGet(1);
                    Game game = new Game("Wow " + (index), change.getElementAdded().getPath(), File.separator + "Interface" + File.separator + "AddOns");
                    Platform.runLater(() -> {
                        ChoiceBoxItem cbi=new ChoiceBoxItem(game);
                        if (model.selectedGame.getValue()==null) {


                            cb.setValue(cbi);
                        }
                        cb.getItems().add(index-1,cbi);
                        model.games.add(game);
                    });
                    Task<Void> refreshTask = new Task<>() {
                        @Override
                        protected Void call() {
                            game.refresh();
                            return null;
                        }
                    };
                    Thread t = new Thread(refreshTask);
                    t.setDaemon(true);
                    t.start();
                    return null;
                }
            };
            Platform.runLater(() -> taskProgressView.getTasks().add(task));
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        };
        fileObservableList.addListener(fileListener);

        setOnScheduled(event1 -> {
            updateProgress(0, 1);
           // taskProgressView.setPrefHeight(taskProgressView.getPrefHeight() + 90);
        });
        setOnSucceeded(workerStateEvent -> {
            fileObservableList.removeListener(fileListener);
           // taskProgressView.setPrefHeight(taskProgressView.getPrefHeight() - 90);
        });
        setOnCancelled(event -> fileObservableList.removeListener(fileListener));
        setOnFailed(event -> fileObservableList.removeListener(fileListener));
    }

    private void searchDriveForWowDirectorys(File parent) {
        var directories = new File(parent.getPath()).listFiles(directoryAndNotHidden);
        if (directories == null) {
            current.addAndGet(1);
            //progress.setValue((double) current.get() / (double) max.get());
            updateProgress(current.get(), max.get());
            return;
        }
        Arrays.stream(directories).parallel().forEach(finder);
        current.addAndGet(1);
        //progress.setValue((double) current.get() / (double) max.get());
        updateProgress(current.get(), max.get());
    }

    private void searchForWowDirectorys(File parent) {
        var directories = new File(parent.getPath()).listFiles(directoryAndNotHidden);
        if (directories == null)
            return;
        Arrays.stream(directories).parallel().forEach(finder);
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

    @Override
    protected Void call() throws Exception {
        var drives = File.listRoots();

        max.set(1);
        //progress.setValue(0);
        updateProgress(0, 1);
        current.set(0);

        //System.out.println(progress.get());
        //Queue<File> results = new ConcurrentLinkedQueue<>();
        Queue<File> directorys = new ConcurrentLinkedQueue<>();
        Arrays.stream(drives).parallel().forEach(drive -> {
            var first = new File(drive.getPath()).listFiles(directoryAndNotHidden);
            if (first == null)
                return;
            for (var d : first) {
                var second = new File(d.getPath()).listFiles(directoryAndNotHidden);
                if (second == null)
                    continue;
                if (d.getPath().contains("$") || d.getPath().contains("Windows"))
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
                    var third = new File(s.getPath()).listFiles(directoryAndNotHidden);
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
        //System.out.println(max.get());
        //progress.setValue((double) current.get() / (double) max.get());
        updateProgress(current.get(), max.get());
        directorys.stream().parallel().forEach(this::searchDriveForWowDirectorys);


        return null;
    }

    private class Filter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            if (pathname.isFile() || pathname.isHidden())
                return false;
            return true;
        }
    }

    private final Filter directoryAndNotHidden = new Filter();
}
