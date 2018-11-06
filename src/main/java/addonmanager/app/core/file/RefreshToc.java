package addonmanager.app.core.file;

import addonmanager.app.core.Addon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.List;

class RefreshToc {
    static boolean refresh(Addon addon){

        var tocFile = new File(addon.getAbsolutePath()).listFiles((dir, name) -> name.toLowerCase().endsWith(".toc"));
        if (tocFile == null || tocFile[0] == null)
           return false;
        List<String> lines = null;



        try {
            lines = Files.readAllLines(tocFile[0].toPath());
        } catch (MalformedInputException e) {
            // System.out.println(use);

            // System.out.println(tocFile[0].getPath());
            // System.out.println(d.getPath());
            // e.printStackTrace();

            try {
                lines = Files.readAllLines(tocFile[0].toPath(), Charset.forName("ISO-8859-1"));
            } catch (IOException e1) {
                //    System.out.println(use);
                //   System.out.println(tocFile[0].getPath());
                //   System.out.println(d.getPath());
                e1.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lines == null)
           return false;

        boolean abort = false;
        for (var line : lines) {
            if (line.contains("Interface:")) {
                addon.setGameVersion(line.substring(line.indexOf("Interface:") + 10).trim());
            } else if (line.contains("Version:")) {
                addon.setVersion(line.substring(line.indexOf("Version:") + 8).trim());
            } else if (line.contains("Title:")) {
                //addon.setTitle(d.getName());
                addon.setTitle(line.substring(line.indexOf("Title:") + 6).replaceAll("\\|c[a-zA-Z_0-9]{8}", "").replaceAll("\\|r", "").trim());
            } else if (line.contains("Dependencies") || line.contains("RequiredDeps")) {
                abort = true;
            }
        }
        if (abort)
            return false;
        return true;
    }
}
