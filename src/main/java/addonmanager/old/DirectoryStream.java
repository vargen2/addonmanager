package addonmanager.old;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class DirectoryStream {

    public final ObservableList<Path> dirs = FXCollections.observableArrayList();
    private final File[] root;
    private final String find;
    private static final Object lock=new Object();
    public static long counter;

    public DirectoryStream(File[] root, String find) {
        this.root = root;
        this.find = find;
    }

    public void getDirs() {

        Arrays.stream(root).parallel().forEach(x->drives(x.toPath()));



    }

    private void drives(Path path) {

        if (path.toString().contains(find)) {
            synchronized (lock){
                dirs.add(path);
            }
            return;
        }
        try {
            var ds = Files.newDirectoryStream(path, entry ->{
                if(entry.toFile().isDirectory())
                    return true;
                return false;
            });
            for (Path d : ds) {
                new Thread(() -> driveFolder(d)).start();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void driveFolder(Path path) {

        if (path.toString().contains(find)) {
            synchronized (lock){
                dirs.add(path);
            }
            return;
        }
        try {
            var ds = Files.newDirectoryStream(path, entry ->{
                if(entry.toFile().isDirectory())
                    return true;
                return false;
            });
            for (Path d : ds) {
                new Thread(() -> search(d)).start();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    private void search(Path path) {
        if (path.toString().contains(find)) {
            synchronized (lock){
                dirs.add(path);
            }
            return;
        }
        try {
            var ds = Files.newDirectoryStream(path, entry ->{
                if(entry.toFile().isDirectory())
                    return true;
               return false;
            });
            for (Path d : ds) {
                search(d);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
