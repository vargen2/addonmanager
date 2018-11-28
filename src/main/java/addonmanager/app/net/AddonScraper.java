package addonmanager.app.net;

import addonmanager.app.CurseAddon;
import addonmanager.app.Util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddonScraper {

    public static void scrapeAndSaveToFile() {

        var addons = scrape(1);
        saveToFile(addons);
    }

    public static void saveToFile(List<CurseAddon> addons) {

    }


    public static List<CurseAddon> scrape(int pages) {
        List<CurseAddon> addons = new LinkedList<>();
        for (int i = 1; i <= pages; i++) {

            String page = find(i);
            addons.addAll(parsePage(page));
        }

        return addons;
    }

    public static String find(int page) {
        String body = "";
        HttpClient httpClient = HttpClient.newHttpClient();


        try {

            URI uri = URI.create("https://www.curseforge.com/wow/addons" + "?page=" + page);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                //App.LOG.fine("not found " + uri.toString());
                System.out.println("not found " + uri.toString());
                return body;
            }
            if (response.body() != null)
                body = response.body();


        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            //App.LOG.severe(" " + e.getMessage());
            System.out.println(" " + e.getMessage());
        }
        return body;
    }

    public static List<CurseAddon> parsePage(String page) {
        List<CurseAddon> addons = new LinkedList<>();
        int index1 = page.indexOf("<section class=\"project-list\">");
        int index2 = page.substring(index1).indexOf("</section>");
        String data = page.substring(index1, index1 + index2);


        Pattern pattern = Pattern.compile("<li class=\"project-list-item\">");
        Matcher matcher = pattern.matcher(data);


        while (matcher.find()) {

            String subString = data.substring(matcher.start());

            String url = "https://www.curseforge.com/wow/addons" + Util.parse(subString, "<a href=\"/wow/addons", "\">").strip();
            String title = Util.parse(subString, "<h2 class=\"list-item__title strong mg-b-05\">", "</h2>");
            String description = Util.parse(subString, "<p title=\"", "</p>");
            addons.add(new CurseAddon(url, title, description));
        }
        return addons;
    }
}
