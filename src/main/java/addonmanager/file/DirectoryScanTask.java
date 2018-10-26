package addonmanager.file;

import addonmanager.app.ChoiceBoxItem;
import addonmanager.core.Game;
import addonmanager.core.Model;
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

public class DirectoryScanTask extends Task<Void> {

    private final AtomicInteger max = new AtomicInteger();
    private final AtomicInteger current = new AtomicInteger();
    private boolean mustHaveExe = true;
    private final ObservableSet<File> fileObservableList = FXCollections.synchronizedObservableSet(FXCollections.observableSet(new HashSet<File>()));

    public DirectoryScanTask(Model model, ChoiceBox cb, TaskProgressView taskProgressView, boolean mustHaveExe) {
        this.mustHaveExe = mustHaveExe;
        SetChangeListener<File> fileListener = change -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {

                    for (var g : model.games) {
                        if (g.getDirectory().equals(change.getElementAdded().getPath()))
                            return null;
                    }

                    Game game = new Game(change.getElementAdded().getName(), change.getElementAdded().getPath(), File.separator + "Interface" + File.separator + "AddOns");
                    Platform.runLater(() -> {
                        ChoiceBoxItem cbi = new ChoiceBoxItem(game);
                        if (model.selectedGame.getValue() == null) {


                            cb.setValue(cbi);
                        }
                        cb.getItems().add(0, cbi);

                        model.games.add(game);
                    });
                    Task<Void> refreshTask = new Task<>() {
                        @Override
                        protected Void call() {
                            game.refresh();
                            game.refreshFromNet();
                            return null;
                        }
                    };
                   // refreshTask.setOnSucceeded(x -> );
                    Thread t = new Thread(refreshTask);
                    t.setDaemon(true);
                    t.start();
                    return null;
                }
            };
           // Platform.runLater(() -> taskProgressView.getTasks().add(task));
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        };
        fileObservableList.addListener(fileListener);

        setOnScheduled(event1 -> {
            updateProgress(0, 1);
        });
        setOnSucceeded(workerStateEvent -> {
            fileObservableList.removeListener(fileListener);
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
        if (check(child)) {

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

                // if (d.getPath().contains("World of Warcraft")) {
                if (check(d)) {

                } else {
                    directorys.add(d);
                }
                for (var s : second) {
                    var third = new File(s.getPath()).listFiles(directoryAndNotHidden);
                    if (third == null)
                        continue;
                    if (s.getPath().contains("$"))
                        continue;

                    //if (s.getPath().contains("World of Warcraft")) {
                    if (check(s)) {

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


    private boolean check(File dir) {
        //if (dir.getPath().contains("Interface" + File.separator + "AddOns")) {
        if (dir.getName().equals("Interface")) {
            var childs = dir.listFiles(directoryAndNotHidden);
            if (childs == null)
                return false;

            if (Arrays.stream(childs).noneMatch(x -> x.getName().equals("AddOns")))
                return false;


            var parent = dir.getParentFile();
            if (mustHaveExe) {

                var exes = parent.listFiles(exeFilter);
                if (exes != null && exes.length > 0) {
                    fileObservableList.add(parent);
                    return true;
                }
            } else {
                //if(dir.getPath().contains("World of Warcraft Beta"))
                fileObservableList.add(parent);
                return true;
            }
        }
        return false;
    }

    private class Filter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            if (pathname.isFile() || pathname.isHidden())
                return false;
            return true;
        }
    }

    private final FileFilter directoryAndNotHidden = new Filter();

    private static class ExeFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            if (pathname.isFile() && pathname.getName().contains(".exe"))
                return true;
            return false;
        }
    }

    private static final FileFilter exeFilter = new ExeFilter();
}
