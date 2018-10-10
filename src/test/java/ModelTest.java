import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void searchForWowDirectorys() {
        var results = Model.searchForWowDirectorys();
        StringBuilder sb = new StringBuilder();
        results.forEach(x -> sb.append(x.getPath()));
        assertEquals("F:\\Program\\World of Warcraft Public TestF:\\Program\\World of Warcraft BetaD:\\Program (x86)\\World of Warcraft", sb.toString());
    }
}