package addonmanager.gui;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Icon {
    public static Glyph create(FontAwesome.Glyph glyph) {
        try {
            Path p = Paths.get(Icon.class.getResource("../../fa-solid-900.ttf").toURI());
            FontAwesome fontAwesome = new FontAwesome(Files.newInputStream(p));
            return fontAwesome.create(glyph);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;

    }
}
