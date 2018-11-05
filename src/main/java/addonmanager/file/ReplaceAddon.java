package addonmanager.file;

import addonmanager.Updateable;
import addonmanager.core.Addon;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReplaceAddon {

    private File zipFile;
    private File tempWorkingDir;
    private File addonDir;
    private File[] addonFolders;

    public ReplaceAddon(Addon addon, File zipFile) {
        this.zipFile = zipFile;
        tempWorkingDir = new File("temp" + File.separator + addon.getFolderName());
        addonDir = new File(addon.getAbsolutePath().replace(addon.getFolderName(), ""));
    }

    public static boolean directoriesExists() {
        boolean returnValue = true;
        if (!Files.isDirectory(Paths.get("temp"))) {
            try {
                FileUtils.forceMkdir(new File("temp"));
            } catch (IOException e) {
                e.printStackTrace();
                returnValue = false;
            }
        }
        if (!Files.isDirectory(Paths.get("backup"))) {
            try {
                FileUtils.forceMkdir(new File("backup"));
            } catch (IOException e) {
                e.printStackTrace();
                returnValue = false;
            }
        }
        return returnValue;
    }

    public boolean replace() {
        return replace(Updateable.EMPTY_UPDATEABLE);
    }

    public boolean replace(Updateable updateable) {
        updateable.updateMessage("unzipping...");
        updateable.updateProgress(0.8, 1.0);
        if (!unpack())
            return false;
        updateable.updateMessage("removing old...");
        updateable.updateProgress(0.85, 1.0);
        if (!moveOldToBackup())
            return false;
        updateable.updateMessage("moving new...");
        updateable.updateProgress(0.9, 1.0);
        if (!moveTempToAddOns())
            return false;
        updateable.updateMessage("clean...");
        updateable.updateProgress(0.95, 1.0);
        return clean();
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
        return true;
    }

    private boolean moveOldToBackup() {
        for (File addonFolder : addonFolders) {
            File dir = new File(addonDir + File.separator + addonFolder.getName());
            if (!dir.exists())
                continue;
            System.out.println(dir.getPath());
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
