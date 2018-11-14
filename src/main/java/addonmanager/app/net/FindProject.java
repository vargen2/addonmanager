package addonmanager.app.net;

import addonmanager.app.Addon;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class FindProject {

    public static Map<String, List<String>> addonProjectNames = new LinkedHashMap<>();

    static {
        addonProjectNames.put("bigwigs", new ArrayList<>(List.of("big-wigs")));
    }

    public static String find(Addon addon) {
        List<String> urlNames = new LinkedList<>(List.of(addon.getTitle().replaceAll(" ", "-"), addon.getFolderName(), addon.getTitle()));
        var projectNames = addonProjectNames.get(addon.getFolderName().toLowerCase());
        if (projectNames != null)
            urlNames.addAll(0, projectNames);

        String input = "";
        for (String anUrlName : urlNames) {
            HttpClient httpClient = HttpClient.newHttpClient();
            URI uri;
            try {
                uri = URI.create("https://www.curseforge.com/wow/addons/" + anUrlName);
            } catch (IllegalArgumentException e) {
                continue;
            }

            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            HttpResponse<String> response = null;
            try {
                response = httpClient.send(request, HttpResponse.BodyHandler.asString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (response.statusCode() != 200) {
                System.err.println("project find fail " + anUrlName + " foldername: " + addon.getFolderName() + " title:" + addon.getTitle());

                continue;

            } else {
                input = response.body();
                break;
            }

        }
        if (input.length() == 0) {
            System.err.println("Fallback to https://www.curseforge.com/wow/addons/ , foldername: " + addon.getFolderName());
            return "https://www.curseforge.com/wow/addons/";
        }


        int index1 = input.indexOf("<p class=\"infobox__cta\"");
        int index2 = input.substring(index1).indexOf("</p>");
        String data = input.substring(index1, index1 + index2);
        String result = Util.parse(data, "<a href=\"", "\">");


        return result;
    }
}
