package addonmanager.app.file;

import addonmanager.app.Updateable;
import addonmanager.app.App;
import addonmanager.app.Game;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class FindGames {

    private static final FileFilter DIRECTORY_AND_NOT_HIDDEN_FILTER = pathname -> !pathname.isFile() && !pathname.isHidden();
    private static final FileFilter EXE_FILTER = pathname -> pathname.isFile() && pathname.getName().contains(".exe");
    private static final String[] COMMON_DIRECTORY_NAMES = {"wow", "world", "warcraft"};
    private final boolean mustHaveExe;
    private int max;
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
        max = 1;
        current.set(0);
        updateable.updateProgress(0, 1);

        List<File> directories = new ArrayList<>();
        Arrays.stream(drives).forEach(drive -> {
            var first = new File(drive.getPath()).listFiles(DIRECTORY_AND_NOT_HIDDEN_FILTER);
            if (first == null)
                return;
            for (var d : first) {
                var second = new File(d.getPath()).listFiles(DIRECTORY_AND_NOT_HIDDEN_FILTER);
                if (second == null)
                    continue;
                if (d.getPath().contains("$") || d.getPath().contains("Windows"))
                    continue;

                fastCheck(d);
                check(d);

                for (var s : second) {
                    var third = new File(s.getPath()).listFiles(DIRECTORY_AND_NOT_HIDDEN_FILTER);
                    if (third == null)
                        continue;
                    if (s.getPath().contains("$"))
                        continue;

                    if (!(fastCheck(s) || check(s)))
                        directories.add(s);
                }
            }
        });
        max = directories.size();
        updateable.updateProgress(current.get(), max);
        directories.stream().parallel().forEach(this::searchDriveForWowDirectories);
        return games;
    }


    private void searchDriveForWowDirectories(File parent) {
        var directories = new File(parent.getPath()).listFiles(DIRECTORY_AND_NOT_HIDDEN_FILTER);
        if (directories == null) {
            int c = current.addAndGet(1);
            updateable.updateProgress(c, max);
            return;
        }
        Arrays.stream(directories).forEach(finder);
        int c = current.addAndGet(1);
        updateable.updateProgress(c, max);
    }

    private void searchForWowDirectories(File parent) {
        var directories = new File(parent.getPath()).listFiles(DIRECTORY_AND_NOT_HIDDEN_FILTER);
        if (directories == null)
            return;
        Arrays.stream(directories).forEach(finder);
    }

    private Consumer<File> finder = (child) -> {
        if (child.getPath().contains("$"))
            return;
        if (!check(child)) {
            searchForWowDirectories(child);
        }
    };

    private boolean check(File dir) {
        if (dir.getName().equals("Interface")) {
            var children = dir.listFiles(DIRECTORY_AND_NOT_HIDDEN_FILTER);
            if (children == null)
                return false;

            if (Arrays.stream(children).noneMatch(x -> x.getName().equals("AddOns")))
                return false;

            var parent = dir.getParentFile();
            if (mustHaveExe) {

                var exes = parent.listFiles(EXE_FILTER);
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
                if (dir.getPath().contains("Wow")) {
                    Game game = App.getFactory().createGame(parent.getName(), parent.getPath(), File.separator + "Interface" + File.separator + "AddOns");
                    consumer.accept(game);
                    games.add(game);
                }
                return true;
            }
        }
        return false;
    }

    private boolean fastCheck(File dir) {
        for (var wowName : COMMON_DIRECTORY_NAMES) {
            if (dir.getName().toLowerCase().contains(wowName)) {
                var children = dir.listFiles(DIRECTORY_AND_NOT_HIDDEN_FILTER);
                if (children == null)
                    continue;
                return Arrays.stream(children).anyMatch(this::check);
            }
        }
        return false;
    }


}
