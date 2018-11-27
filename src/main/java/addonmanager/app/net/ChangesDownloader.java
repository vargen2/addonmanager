package addonmanager.app.net;

import addonmanager.app.Addon;
import addonmanager.app.App;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

class ChangesDownloader {

    private final static Map<String, List<String>> ADDON_PROJECT_NAMES = new LinkedHashMap<>();

    static {
        ADDON_PROJECT_NAMES.put("bigwigs", new ArrayList<>(List.of("big-wigs")));
        ADDON_PROJECT_NAMES.put("dbm-core", new ArrayList<>(List.of("deadly-boss-mods")));
        ADDON_PROJECT_NAMES.put("omnicc", new ArrayList<>(List.of("omni-cc")));
        ADDON_PROJECT_NAMES.put("omen", new ArrayList<>(List.of("omen-threat-meter")));
        ADDON_PROJECT_NAMES.put("littlewigs", new ArrayList<>(List.of("little-wigs")));
    }

    private final Addon addon;

    ChangesDownloader(Addon addon) {
        this.addon = addon;
    }

    String find() {
        List<String> urlNames = new LinkedList<>(List.of(addon.getTitle().replaceAll(" ", "-"), addon.getFolderName(), addon.getTitle()));
        var projectNames = ADDON_PROJECT_NAMES.get(addon.getFolderName().toLowerCase());
        if (projectNames != null)
            urlNames.addAll(0, projectNames);

        String page = "";
        for (String anUrlName : urlNames) {
            try {
                HttpClient httpClient = HttpClient.newHttpClient();
                URI uri = URI.create("https://www.curseforge.com/wow/addons/" + anUrlName + "/changes");
                HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    App.LOG.fine("Find changes fail " + anUrlName + " foldername: " + addon.getFolderName() + " title:" + addon.getTitle());
                    continue;
                }
                page = response.body();
                if (page != null && !page.isEmpty())
                    break;
                else
                    return "didn't find changes";

            } catch (IOException | InterruptedException | IllegalArgumentException e) {
                App.LOG.severe("Find project " + addon.getFolderName() + " " + e.getMessage());
            }
        }
        int index1 = page.indexOf("<section class=\"project-content");
        int index2 = page.substring(index1).indexOf("</section>");
        String changes = page.substring(index1, index1 + index2);
        changes = changes.replaceAll("<section.+>", "");
        changes = changes.replaceAll("<a href=\"", "");
        changes = changes.replaceAll("\" rel=\"nofollow\">", "");
        changes = changes.replaceAll("<.{1,2}>", "");
        changes = changes.replaceAll("</.{1,2}>", "");
        return changes.strip();
    }
}
