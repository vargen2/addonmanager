package addonmanager.gui;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

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

    public static void setIcon(Button button, FontAwesome.Glyph fGlyph, Color color){
        Glyph glyph=Icon.create(fGlyph);
        if(glyph==null)
            return;
        button.setGraphic(glyph.size(20).color(color));
        button.setText("");
        button.setPrefHeight(26);
        button.setMinHeight(26);
        button.setMaxHeight(26);
    }
}
