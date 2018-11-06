package addonmanager.app.core;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

    public final ObservableList<Game> games = FXCollections.observableArrayList();
    public final Property<Game> selectedGame = new SimpleObjectProperty<Game>();

    public void setReleaseTypeSelectedGame(Addon.ReleaseType releaseType) {
        if (selectedGame.getValue() == null)
            return;
        selectedGame.getValue().addons.forEach(x -> x.setReleaseType(releaseType));
    }
//    public static Queue<File> searchForWowDirectorys() {
//        var drives = File.listRoots();
//
//        max.set(0);
//        progress.setValue(0);
//        current.set(0);
//
//        //System.out.println(progress.get());
//        Queue<File> results = new ConcurrentLinkedQueue<>();
//        Arrays.stream(drives).parallel().forEach(drive -> searchDriveForWowDirectorys(results, drive));
//        //results.stream().forEach(x -> System.out.println(x.getPath()));
//        return results;
//    }

//    private static void searchDriveForWowDirectorys(Queue<File> results, File parent) {
//        var directorys = new File(parent.getPath()).listFiles(File::isDirectory);
//        if (directorys == null)
//            return;
////        for(var d:directorys){
////            //System.out.println(d.canRead());
////            var subdirectorys = new File(d.getPath()).listFiles(File::isDirectory);
////            if(subdirectorys==null)
////                continue;
////            if(d.getPath().contains("$"))
////                continue;
////            max.addAndGet(1);
////        }
//max.addAndGet(directorys.length);
//        progress.setValue((double)current.get()/(double)max.get());
//       // System.out.println("max: "+max);
//        Arrays.stream(directorys).parallel().forEach(child -> finder2.accept(results, child));
//
//    }


//    private static void searchForWowDirectorysWithCounter(Queue<File> results, File parent) {
//        var directorys = new File(parent.getPath()).listFiles(File::isDirectory);
//        if (directorys == null) {
//            current.addAndGet(1);
//            progress.setValue((double)current.get()/(double)max.get());
//            return;
//        }
//        Arrays.stream(directorys).parallel().forEach(child -> finder.accept(results, child));
//        current.addAndGet(1);
//        progress.setValue((double)current.get()/(double)max.get());
//        //System.out.println(progress.get());
//    }

//    private static void searchForWowDirectorys(Queue<File> results, File parent) {
//        var directorys = new File(parent.getPath()).listFiles(File::isDirectory);
//        if (directorys == null)
//            return;
//        Arrays.stream(directorys).parallel().forEach(child -> finder.accept(results, child));
//    }

//    private static BiConsumer<Queue<File>, File> finder2 = (results, child) -> {
//        if (child.getPath().contains("$")) {
//            current.addAndGet(1);
//            progress.setValue((double)current.get()/(double)max.get());
//            return;
//        }if (child.getPath().contains("World of Warcraft")) {
//            var valid = new File(child.getPath() + File.separator + "Interface" + File.separator + "AddOns");
//            if (valid.isDirectory()) {
//                results.add(child);
//            }
//        } else {
//            searchForWowDirectorysWithCounter(results, child);
//        }
//    };

//    private static BiConsumer<Queue<File>, File> finder = (results, child) -> {
//        if (child.getPath().contains("$"))
//            return;
//        if (child.getPath().contains("World of Warcraft")) {
//            var valid = new File(child.getPath() + File.separator + "Interface" + File.separator + "AddOns");
//            if (valid.isDirectory()) {
//                results.add(child);
//            }
//        } else {
//            searchForWowDirectorys(results, child);
//        }
//    };
}
