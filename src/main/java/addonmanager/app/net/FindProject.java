package addonmanager.app.net;

import addonmanager.app.Addon;
import addonmanager.app.App;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class FindProject {

    public static Map<String, List<String>> addonProjectNames = new LinkedHashMap<>();

    static {
        addonProjectNames.put("bigwigs", new ArrayList<>(List.of("big-wigs")));
        addonProjectNames.put("dbm-core", new ArrayList<>(List.of("deadly-boss-mods")));
        addonProjectNames.put("omnicc", new ArrayList<>(List.of("omni-cc")));
        addonProjectNames.put("omen", new ArrayList<>(List.of("omen-threat-meter")));
    }

    public static String find(Addon addon) {
        List<String> urlNames = new LinkedList<>(List.of(addon.getTitle().replaceAll(" ", "-"), addon.getFolderName(), addon.getTitle()));
        var projectNames = addonProjectNames.get(addon.getFolderName().toLowerCase());
        if (projectNames != null)
            urlNames.addAll(0, projectNames);


        for (String anUrlName : urlNames) {
            try {
                HttpClient httpClient = HttpClient.newHttpClient();
                URI uri = URI.create("https://www.curseforge.com/wow/addons/" + anUrlName);
                HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    App.LOG.info("Find projectURL fail " + anUrlName + " foldername: " + addon.getFolderName() + " title:" + addon.getTitle());
                    continue;
                }
                String input = response.body();
                int index1 = input.indexOf("<p class=\"infobox__cta\"");
                int index2 = input.substring(index1).indexOf("</p>");
                String data = input.substring(index1, index1 + index2);
                return Util.parse(data, "<a href=\"", "\">");
            } catch (IOException | InterruptedException | IllegalArgumentException e) {
                App.LOG.severe("Find project " + addon.getFolderName() + " " + e.getMessage());
            }
        }

        App.LOG.info("Fallback to https://www.curseforge.com/wow/addons/ , foldername: " + addon.getFolderName());
        return "https://www.curseforge.com/wow/addons/";


//        int index1 = input.indexOf("<p class=\"infobox__cta\"");
//        int index2 = input.substring(index1).indexOf("</p>");
//        String data = input.substring(index1, index1 + index2);
//        String result = Util.parse(data, "<a href=\"", "\">");
//
//
//        return result;
    }
}
