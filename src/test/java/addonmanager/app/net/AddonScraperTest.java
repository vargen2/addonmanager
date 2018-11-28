package addonmanager.app.net;

import addonmanager.app.Util;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddonScraperTest {

    @Test
    void find() {

        AddonScraper.scrape(1).forEach(addon -> System.out.println(addon.toString() + Util.LINE));
        assertEquals("", AddonScraper.scrape(1));

    }
}