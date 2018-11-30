package addonmanager.app.file;

import addonmanager.app.Addon;
import addonmanager.app.CurseAddon;
import addonmanager.app.Download;
import addonmanager.app.Updateable;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

class AddonInstaller {

    private Addon addon;
    private Download download;
    private File zipFile;
    private File tempWorkingDir;
    private File addonDir;
    private File[] addonFolders;


    AddonInstaller(Addon addon, CurseAddon curseAddon, Download download, File zipFile) {
        this.addon = addon;
        this.download = download;
        this.zipFile = zipFile;
        tempWorkingDir = new File("temp" + File.separator + addon.getFolderName());
        addonDir = new File(addon.getGame().getDirectory() + addon.getGame().getAddonDirectory());
    }

    /**
     * @param from
     * @param to
     * @param updateable
     * @return empty list = failed
     */
    List<File> install(double from, double to, Updateable updateable) {
        if (!FileOperations.directoriesExists())
            return List.of();
        updateable.updateMessage("unzipping...");
        updateable.updateProgress(from + (to - from) * 0.0, 1);
        if (!unpack())
            return List.of();

        updateable.updateMessage("moving new...");
        updateable.updateProgress(from + (to - from) * 0.5, 1);
        if (!moveTempToAddOns())
            return List.of();
        updateable.updateMessage("clean...");
        updateable.updateProgress(from + (to - from) * 0.75, 1);
        if (!clean())
            return List.of();
        return List.of(addonFolders);
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


    private boolean moveTempToAddOns() {
        if (addonFolders == null)
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

        FileUtils.deleteQuietly(tempWorkingDir);

        return true;
    }

}
