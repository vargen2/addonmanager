package addonmanager.app.core.file;

import addonmanager.Updateable;
import addonmanager.app.core.App;
import addonmanager.app.core.Game;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class FindGames {
    private final boolean mustHaveExe;
    private final AtomicInteger max = new AtomicInteger();
    private final AtomicInteger current = new AtomicInteger();
    private final Updateable updateable;
    private final Consumer<Game> consumer;
    private final List<Game> games = Collections.synchronizedList(new ArrayList<>());

    FindGames(Updateable updateable, Consumer<Game> consumer, boolean mustHaveExe) {
        this.updateable = updateable;
        this.consumer = consumer;
        this.mustHaveExe = mustHaveExe;
    }

    List<Game> find() {
        var drives = File.listRoots();
        max.set(1);
        current.set(0);
        updateable.updateProgress(0, 1);

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
        updateable.updateProgress(current.get(), max.get());
        directorys.stream().parallel().forEach(this::searchDriveForWowDirectorys);

        return games;
    }


    private void searchDriveForWowDirectorys(File parent) {
        var directories = new File(parent.getPath()).listFiles(directoryAndNotHidden);
        if (directories == null) {
            current.addAndGet(1);
            //progress.setValue((double) current.get() / (double) max.get());
            updateable.updateProgress(current.get(), max.get());
            return;
        }
        Arrays.stream(directories).parallel().forEach(finder);
        current.addAndGet(1);
        //progress.setValue((double) current.get() / (double) max.get());
        updateable.updateProgress(current.get(), max.get());
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
                    Game game = App.getFactory().createGame(parent.getName(), parent.getPath(), File.separator + "Interface" + File.separator + "AddOns");
                    consumer.accept(game);
                    games.add(game);
                    return true;
                }
            } else {
                //System.out.println(parent.getPath());
                //laptop /stationär
                //if (dir.getPath().contains("World of Warcraft Beta")){
                Game game = App.getFactory().createGame(parent.getName(), parent.getPath(), File.separator + "Interface" + File.separator + "AddOns");
                consumer.accept(game);
                games.add(game);
                //}
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
