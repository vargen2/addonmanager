package addonmanager.core;

import addonmanager.net.GetVersionsTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Game {

    private String name;
    private String directory;
    private String addonDirectory;
    public final ObservableList<Addon> addons = FXCollections.observableArrayList();

    public Game(String name, String directory, String addonDirectory) {
        this.name = name;
        this.directory = directory;
        this.addonDirectory = addonDirectory;
    }

    public void refresh() {
        File[] directories = new File(directory + addonDirectory).listFiles(File::isDirectory);
        for (var d : directories) {
            if (addons.parallelStream().anyMatch(x -> (x.getFolderName().equals(d.getName()))))
                continue;
            var tocFile = d.listFiles((dir, name) -> name.toLowerCase().endsWith(".toc"));
            if (tocFile == null || tocFile[0] == null)
                continue;
            List<String> lines = null;

            try {
                lines = Files.readAllLines(tocFile[0].toPath());
            } catch (MalformedInputException e) {
                // System.out.println(use);

                // System.out.println(tocFile[0].getPath());
                // System.out.println(d.getPath());
                // e.printStackTrace();

                try {
                    lines = Files.readAllLines(tocFile[0].toPath(), Charset.forName("ISO-8859-1"));
                } catch (IOException e1) {
                    //    System.out.println(use);
                    //   System.out.println(tocFile[0].getPath());
                    //   System.out.println(d.getPath());
                    e1.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            if (lines == null)
                continue;
            Addon addon = new Addon(d.getName());
            boolean abort = false;
            for (var line : lines) {
                if (line.contains("Interface:")) {
                    addon.setGameVersion(line.substring(line.indexOf("Interface:") + 10).trim());
                } else if (line.contains("Version:")) {
                    addon.setVersion(line.substring(line.indexOf("Version:") + 8).trim());
                } else if (line.contains("Title:")) {
                    //addon.setTitle(d.getName());
                     addon.setTitle(line.substring(line.indexOf("Title:") + 6).replaceAll("\\|c[a-zA-Z_0-9]{8}", "").replaceAll("\\|r", "").trim());
                } else if (line.contains("Dependencies") || line.contains("RequiredDeps")) {
                    abort = true;
                }
            }
            if (abort)
                continue;
            addons.add(addon);
        }

    }

    public void refreshFromNet() {
        Thread t = new Thread(new Task<>() {
            @Override
            protected Object call() throws Exception {
                addons.parallelStream().forEach(addon -> {

                    var task = new GetVersionsTask(addon);

                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();
                    List<Download> downloads = null;
                    try {
                        downloads = task.get();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        System.out.println("addon: " + addon.getFolderName() + " " + e.getMessage());
                    }
                    if (downloads != null && downloads.size() > 0) {
                        addon.setDownloads(downloads);


                        Status status = new Status();
                        status.setLatestVersion(downloads.get(0).title);
                        status.setFolderName(addon.getFolderName());
                        System.out.println("addonfolder: " + addon.getFolderName()+" addon.title.replace: "+ addon.getTitle().replaceAll(" ","-")+" version:"+addon.getVersion() + " downloads.title: " + downloads.get(0).title);
                        addon.setStatus(status);

                    } else {
                        Status status = new Status();
                        status.setFolderName(addon.getFolderName());
                        addon.setStatus(status);
                    }
                });

                return null;
            }
        });
        t.setDaemon(true);
        t.start();

    }

    public String getDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return name + " " + directory;
    }
}
