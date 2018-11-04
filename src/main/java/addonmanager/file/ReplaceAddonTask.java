package addonmanager.file;

import addonmanager.core.Addon;
import addonmanager.core.Game;
import addonmanager.net.DownloadAddonTask;
import javafx.concurrent.Task;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReplaceAddonTask extends Task<String> {

    private Addon addon;

    public ReplaceAddonTask(Addon addon) {
        super();
        this.addon = addon;
        setOnScheduled(x -> {
            updateProgress(0, 1);
            updateMessage("initializing...");
            addon.setReplaceAddonTask(this);
        });
        setOnCancelled(event -> {
            updateMessage("canceled");
            addon.setStatus(Addon.Status.NONE);
        });

        setOnSucceeded(x -> {
            updateMessage("done");
            updateProgress(1, 1);
            addon.refreshToc();
            addon.setStatus(Addon.Status.UP_TO_DATE);
        });
    }

    @Override
    protected String call() throws Exception {
        File tempWorkingDir = new File("temp" + File.separator + addon.getFolderName());
        //dl zip file to /temp 0.3
        DownloadAddonTask downloadAddonTask = new DownloadAddonTask();
        File zipFile = downloadAddonTask.downLoadFile(addon.getFolderName(), addon.getLatestDownload(), this::updateMessage, this::updateProgress);

        if(!zipFile.exists())
            cancel();
        System.out.println("zip exists");
        //check if zip file downloaded

        //unzip dl filed to /temp
        //File targetDir = new File("temp/");
        updateMessage("unzipping...");
        updateProgress(0.35, 1.0);
        try (InputStream inputStream = Files.newInputStream(zipFile.toPath())) {
            ArchiveInputStream i = new ZipArchiveInputStream(new BufferedInputStream(inputStream));
            ArchiveEntry entry = null;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
                    // log something?
                    continue;
                }

                File f = new File(tempWorkingDir.getPath() + File.separator + entry);
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException("failed to create directory " + f);
                    }
                } else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                }
            }
        }
        updateProgress(0.4, 1.0);

        //check if ok
        if(!tempWorkingDir.exists())
            cancel();

        System.out.println("tempworkdir exists");
        //if ok move old addon to /backup
        File addonDir = new File(addon.getAbsolutePath().replace(addon.getFolderName(), ""));
        updateMessage("removing old");
        File[] addonFolders = tempWorkingDir.listFiles(File::isDirectory);
        for(File addonFolder:addonFolders){
            File dir = new File(addonDir+File.separator+addonFolder.getName());
            if(!dir.exists())
                continue;
            System.out.println(dir.getPath());
            File destDir = new File("backup");
            FileUtils.moveDirectoryToDirectory(dir, destDir, !destDir.exists());
        }

        updateProgress(0.5, 1.0);

        //move from /temp to /addons
        updateMessage("moving new");

        for(File addonFolder:addonFolders) {
            File tempAddon = new File(tempWorkingDir.getPath() + File.separator + addonFolder.getName());
            FileUtils.moveDirectoryToDirectory(tempAddon, addonDir, false);
        }
        updateProgress(0.6, 1.0);


        //if not ok rollback

        //if ok, delete from /backup and zip
        updateMessage("cleanup...");
        FileUtils.deleteQuietly(zipFile);
        FileUtils.deleteQuietly(tempWorkingDir);
        for(File addonFolder:addonFolders) {
            FileUtils.deleteQuietly(new File("backup" + File.separator + addonFolder.getName()));
        }
        updateProgress(0.8, 1.0);

        return "done";
    }
}
