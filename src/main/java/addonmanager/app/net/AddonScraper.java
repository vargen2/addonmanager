package addonmanager.app.net;

import addonmanager.app.CurseAddon;
import addonmanager.app.Util;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddonScraper {

    public static void scrapeAndSaveToFile(int from, int to) {

        var addons = scrape(from, to);
        saveToFile(addons, from, to);
    }

    public static void saveToFile(List<CurseAddon> addons, int from, int to) {
        var gson = new GsonBuilder().setPrettyPrinting().create();
        var json = gson.toJson(addons);
        try {
            Files.writeString(Path.of("curseaddons" + from + "" + to + ".txt"), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<CurseAddon> scrape(int from, int to) {
        List<CurseAddon> addons = new LinkedList<>();
        for (int i = from; i <= to; i++) {
            Util.sleep(1000);
            String page = find(i);
            if (page == null || page.isEmpty()) {
                System.out.println("MISSED page: " + i);
            } else {
                addons.addAll(parsePage(page));
                System.out.println("page " + i);
            }
        }

        return addons;
    }

    public static String find(int page) {
        String body = "";
        HttpClient httpClient = HttpClient.newHttpClient();

        boolean done = false;
        while (!done) {
            try {

                URI uri = URI.create("https://www.curseforge.com/wow/addons" + "?page=" + page);
                HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    //App.LOG.fine("not found " + uri.toString());
                    System.out.println("not found " + uri.toString());
                    return body;
                }
                if (response.body() != null) {
                    body = response.body();
                    done = true;
                }
            } catch (IOException | InterruptedException | IllegalArgumentException e) {
                //App.LOG.severe(" " + e.getMessage());
                System.out.println(" " + e.getMessage());
            }
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

            String url = Util.parse(subString, "<a href=\"/wow/addons/", "\">").strip();
            String title = Util.parse(subString, "<h2 class=\"list-item__title strong mg-b-05\">", "</h2>");
            String description = Util.parse(subString, "<p title=\"", "\">");

            String tempDL = Util.parse(subString, "<span class=\"has--icon count--download\">", "</span>").replaceAll(",", "").trim();
            long downloads = Long.valueOf(tempDL);

            String updatedSub = Util.parse(subString, "<span class=\"has--icon date--updated\">", "</abbr></span>");
            String updatedEpoch = Util.parse(updatedSub, "data-epoch=\"", "\">");

            String createdSub = Util.parse(subString, "<span class=\"has--icon date--created\">", "</abbr></span>");
            String createdEpoch = Util.parse(createdSub, "data-epoch=\"", "\">");


            addons.add(new CurseAddon(url, title, description, downloads, updatedEpoch, createdEpoch));
        }
        return addons;
    }
}
