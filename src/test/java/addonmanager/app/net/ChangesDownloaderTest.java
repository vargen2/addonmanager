package addonmanager.app.net;

import addonmanager.app.Addon;
import addonmanager.app.App;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangesDownloaderTest {

    @Test
    @Disabled
    void find() {
        Addon littleWigs = App.DEFAULT_FACTORY.createAddon(null, "LittleWigs", null);
        littleWigs.setTitle("Li");
        ChangesDownloader changesDownloader = new ChangesDownloader(littleWigs);


        assertEquals("asd", changesDownloader.find());
    }
}