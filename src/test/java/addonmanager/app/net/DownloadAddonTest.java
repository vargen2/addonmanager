package addonmanager.app.net;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.Download;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DownloadAddonTest {

    @Test
    void downLoadFile() {
        Addon addon = App.getFactory().createAddon(App.getFactory().createGame("", "", ""), "Details", "");
        addon.setProjectUrl("https://wow.curseforge.com/projects/details");
        Download download = new Download("release", "v8.0.1.6600.135", "4.15 MB", null, null, 0, "https://wow.curseforge.com/projects/details/files/2629656/download");
        List<Download> downloads = new ArrayList<>();
        downloads.add(download);
        addon.setDownloads(downloads);
        File zipFile = NetOperations.downLoadFile(addon, addon.getLatestDownload());

        assertTrue(zipFile.exists());

        try {
            assertEquals(4351416, Files.size(zipFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}