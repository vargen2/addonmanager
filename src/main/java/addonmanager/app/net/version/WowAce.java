package addonmanager.app.net.version;

import addonmanager.app.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class WowAce extends VersionDownloader {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    WowAce(Addon addon) {
        super(addon);
    }

    @Override
    public List<Download> getDownloads() {
        Updateable updateable = addon.getUpdateable();
        List<Download> downloads = new ArrayList<>();

        String filePage = "";
        if (page != 0)
            filePage = "?page=" + page;

        String input = "";

        //HttpClient httpClient = HttpClient.newHttpClient();
        URI uri;
        try {
            uri = URI.create(addon.getProjectUrl() + "/files" + filePage);
            System.out.println(uri);

        } catch (IllegalArgumentException e) {
            return downloads;
        }

        HttpRequest request = HttpRequest.newBuilder().header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36").uri(uri).build();
        HttpResponse<String> response = null;

        try {
            int i = 0;
            do {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    String msg = "DL " + i + " fail " + response.statusCode() + " " + addon.getProjectUrl();
                    App.LOG.info(msg);
                    updateable.updateMessage(msg);
                }
                i++;
            } while (i < 5 && response.statusCode() != 200);
            System.out.println(response.headers());
            if (response.sslSession().isPresent()) {
                for (String valueName : response.sslSession().get().getValueNames()) {
                    System.out.println(valueName);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response.statusCode() != 200) {
            App.LOG.info("DL fail " + response.statusCode() + " " + addon.getProjectUrl());
            updateable.updateMessage("DL fail " + addon.getProjectUrl());
            return downloads;

        } else {
            input = response.body();

        }


        if (input.length() == 0) {
            updateable.updateMessage("failed...");
            //System.out.println("failed.." + addon.getProjectUrl());
            return downloads;
        }
        updateable.updateMessage("parsing...");
        //  updateable.updateProgress(0.7, 1.0);

        int index1 = input.indexOf("<div class=\"listing-body\">");
        int index2 = input.substring(index1).indexOf("</table>");
        String data = input.substring(index1, index1 + index2);
        Pattern pattern = Pattern.compile("<tr class=\"project-file-list-item\">");
        Matcher matcher = pattern.matcher(data);


        while (matcher.find()) {

            String subString = data.substring(matcher.start());

            String temp = Util.parse(subString, "<td class=\"project-file-release-type\">", "</td>");
            String release = Util.parse(temp, "title=\"", "\"></div>");

            String title = Util.parse(subString, "data-name=\"", "\">");
            String fileSize = Util.parse(subString, "<td class=\"project-file-size\">", "</td>").trim();

            String a = Util.parse(subString, "data-epoch=\"", "\"");
            LocalDateTime fileDateUploaded = LocalDateTime.ofEpochSecond(Integer.parseInt(a), 0, OffsetDateTime.now().getOffset());

            String gameVersion = Util.parse(subString, "<span class=\"version-label\">", "</span>");

            String tempDL = Util.parse(subString, "<td class=\"project-file-downloads\">", "</td>").replaceAll(",", "").trim();

            long dls = Long.valueOf(tempDL);
            String downloadLink = Util.parse(subString, " href=\"", "\"");

            Download download = new Download(release, title, fileSize, fileDateUploaded, gameVersion, dls, downloadLink);
            downloads.add(download);
        }
        //updateable.updateProgress(0.9, 1.0);
        updateable.updateMessage("done");
        return downloads;
    }


}
