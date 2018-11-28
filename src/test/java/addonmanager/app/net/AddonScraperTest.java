package addonmanager.app.net;

import addonmanager.app.CurseAddon;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddonScraperTest {

    @Disabled
    @Test
    void find() {

        List<CurseAddon> addons = AddonScraper.scrape(1, 1);

        assertEquals(20, addons.size());

    }

    @Disabled
    @Test
    void find2() {

        List<CurseAddon> addons = AddonScraper.scrape(1, 2);

        assertEquals(40, addons.size());

    }

    @Disabled
    @Test
    void scrape() {

        AddonScraper.scrapeAndSaveToFile(329, 329);


    }
}