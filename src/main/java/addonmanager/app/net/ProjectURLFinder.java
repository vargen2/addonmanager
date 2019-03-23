package addonmanager.app.net;

import addonmanager.app.Addon;
import addonmanager.app.App;
import addonmanager.app.CurseAddon;
import addonmanager.app.Util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;
import java.util.*;

class ProjectURLFinder {

    private final static Map<String, List<String>> ADDON_PROJECT_NAMES = new LinkedHashMap<>();

    static {
        ADDON_PROJECT_NAMES.put("bigwigs", new ArrayList<>(List.of("big-wigs")));
        ADDON_PROJECT_NAMES.put("dbm-core", new ArrayList<>(List.of("deadly-boss-mods")));
        ADDON_PROJECT_NAMES.put("omnicc", new ArrayList<>(List.of("omni-cc")));
        ADDON_PROJECT_NAMES.put("omen", new ArrayList<>(List.of("omen-threat-meter")));
        ADDON_PROJECT_NAMES.put("littlewigs", new ArrayList<>(List.of("little-wigs")));
        ADDON_PROJECT_NAMES.put("elvui_sle", new ArrayList<>(List.of("elvui-shadow-light")));
        ADDON_PROJECT_NAMES.put("atlasloot", new ArrayList<>(List.of("atlasloot-enhanced")));
        ADDON_PROJECT_NAMES.put("healbot", new ArrayList<>(List.of("heal-bot-continued")));
        ADDON_PROJECT_NAMES.put("tradeskillmaster", new ArrayList<>(List.of("tradeskill-master")));
    }

    private final Addon addon;

    ProjectURLFinder(Addon addon) {
        this.addon = addon;
    }

    String find() {
        List<String> urlNames = new LinkedList<>(List.of(addon.getTitle().replaceAll(" ", "-"), addon.getFolderName(), addon.getTitle()));


        var projectNames = ADDON_PROJECT_NAMES.get(addon.getFolderName().toLowerCase());
        if (projectNames != null)
            urlNames.addAll(0, projectNames);


        for (String anUrlName : urlNames) {
            for (int i = 0; i < 3; i++) {
                try {
                    SSLParameters sslParameters = SSLContext.getDefault().getDefaultSSLParameters();
                    sslParameters.setProtocols(new String[]{"TLSv1.2"});
                    HttpClient httpClient = HttpClient.newBuilder().sslParameters(sslParameters).build();
                    URI uri = URI.create("https://www.curseforge.com/wow/addons/" + anUrlName);
                    HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() != 200) {
                        App.LOG.fine("Find project URL fail " + anUrlName + " foldername: " + addon.getFolderName() + " title:" + addon.getTitle());
                        continue;
                    }
                    String input = response.body();
                    int index1 = input.indexOf("<p class=\"infobox__cta\"");
                    int index2 = input.substring(index1).indexOf("</p>");
                    String data = input.substring(index1, index1 + index2);
                    return Util.parse(data, "<a href=\"", "\">");
                } catch (IOException | InterruptedException | IllegalArgumentException | NoSuchAlgorithmException e) {
                    if (e.getCause() instanceof SSLException) {
                        App.LOG.severe(getClass().getName() + " SLEEP " + " " + e.getMessage());
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        App.LOG.severe(getClass().getName() + " Find project " + addon.getFolderName() + " " + e.getMessage());
                    }
                }
            }
        }

        App.LOG.fine("Fallback to https://www.curseforge.com/wow/addons/ , foldername: " + addon.getFolderName());
        return "https://www.curseforge.com/wow/addons/";

    }

    static String find(CurseAddon curseAddon) {
        for (int i = 0; i < 3; i++) {
            try {
                HttpClient httpClient = HttpClient.newHttpClient();
                URI uri = URI.create("https://www.curseforge.com/wow/addons/" + curseAddon.getAddonURL());
                HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    App.LOG.fine("Find project URL fail " + curseAddon.getAddonURL());
                    continue;
                }
                String input = response.body();
                int index1 = input.indexOf("<p class=\"infobox__cta\"");
                int index2 = input.substring(index1).indexOf("</p>");
                String data = input.substring(index1, index1 + index2);
                return Util.parse(data, "<a href=\"", "\">");
            } catch (IOException | InterruptedException | IllegalArgumentException e) {
                if (e.getCause() instanceof SSLException) {
                    App.LOG.severe(ProjectURLFinder.class.getName() + " STATIC SLEEP " + e.getMessage());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    App.LOG.severe(ProjectURLFinder.class.getName() + " Find project " + curseAddon.getAddonURL() + " " + e.getMessage());
                }
            }
        }
        return "";
    }
}
