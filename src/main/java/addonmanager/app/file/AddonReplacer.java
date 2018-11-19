package addonmanager.app.file;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Download;
import addonmanager.app.Updateable;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

class AddonReplacer {

    private Addon addon;
    private Download download;
    private File zipFile;
    private File tempWorkingDir;
    private File addonDir;
    private File[] addonFolders;

    AddonReplacer(Addon addon, Download download, File zipFile) {
        this.addon = addon;
        this.download = download;
        this.zipFile = zipFile;
        tempWorkingDir = new File("temp" + File.separator + addon.getFolderName());
        addonDir = new File(addon.getAbsolutePath().replace(addon.getFolderName(), ""));
    }

    boolean replace(double from, double to) {
        if (!FileOperations.directoriesExists())
            return false;
        Updateable updateable = addon.getUpdateable();
        updateable.updateMessage("unzipping...");
        updateable.updateProgress(from + (to - from) * 0.0, to);
        if (!unpack())
            return false;

        updateable.updateMessage("removing old...");
        updateable.updateProgress(from + (to - from) * 0.25, to);
        if (!moveOldToBackup())
            return false;

        updateable.updateMessage("moving new...");
        updateable.updateProgress(from + (to - from) * 0.5, to);
        if (!moveTempToAddOns())
            return false;

        updateable.updateMessage("clean...");
        updateable.updateProgress(from + (to - from) * 0.75, to);
        if (!clean())
            return false;

        addon.setLatestUpdate(download);
        return true;
    }

    private boolean unpack() {
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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        addonFolders = tempWorkingDir.listFiles(File::isDirectory);
        if (addonFolders != null)
            addon.setExtraFolders(Arrays.stream(addonFolders).filter(x -> !x.getName().equals(addon.getFolderName())).collect(Collectors.toList()));
        return true;
    }

    private boolean moveOldToBackup() {
        if(addonFolders==null)
            return false;
        for (File addonFolder : addonFolders) {
            File dir = new File(addonDir + File.separator + addonFolder.getName());
            if (!dir.exists())
                continue;
            App.LOG.fine(dir.getPath());
            File destDir = new File("backup");
            try {
                FileUtils.moveDirectoryToDirectory(dir, destDir, !destDir.exists());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean moveTempToAddOns() {
        if(addonFolders==null)
            return false;
        for (File addonFolder : addonFolders) {
            File tempAddon = new File(tempWorkingDir.getPath() + File.separator + addonFolder.getName());
            try {
                FileUtils.moveDirectoryToDirectory(tempAddon, addonDir, false);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean clean() {
        FileUtils.deleteQuietly(zipFile);
        FileUtils.deleteQuietly(tempWorkingDir);
        for (File addonFolder : addonFolders) {
            FileUtils.deleteQuietly(new File("backup" + File.separator + addonFolder.getName()));
        }
        return true;
    }

}
