package addonmanager.app.net.version;

import addonmanager.app.Updateable;
import addonmanager.app.Addon;
import addonmanager.app.Download;
import addonmanager.app.net.Util;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WowCurseForge extends DownloadVersions {

    public WowCurseForge(Addon addon) {
        super(addon);
    }

    @Override
    public List<Download> getDownloads() {
        Updateable updateable = addon.getUpdateable();
        List<Download> downloads = new ArrayList<>();


        //updateProgress(0, 1);
        String retrying = "retrying";
        String input = "";

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri;
        try {
            uri = URI.create(addon.getProjectUrl() + "/files");

        } catch (IllegalArgumentException e) {
            return downloads;
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
            System.out.println("DL fail " + addon.getProjectUrl());

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
        updateable.updateProgress(0.7, 1.0);

        int index1 = input.indexOf("<div class=\"listing-body\">");
        int index2 = input.substring(index1).indexOf("</table>");
        String data = input.substring(index1, index1 + index2);
        Pattern pattern = Pattern.compile("<tr class=\"project-file-list-item\">");
        Matcher matcher = pattern.matcher(data);


        while (matcher.find()) {
            Download download = new Download();
            String subString = data.substring(matcher.start());
            String temp = Util.parse(subString, "<td class=\"project-file-release-type\">", "</td>");
            download.release = Util.parse(temp, "title=\"", "\"></div>");
            download.title = Util.parse(subString, "data-name=\"", "\">");
            download.fileSize = Util.parse(subString, "<td class=\"project-file-size\">", "</td>").trim();
            String a = Util.parse(subString, "data-epoch=\"", "\"");
            download.fileDateUploaded = LocalDateTime.ofEpochSecond(Integer.parseInt(a), 0, OffsetDateTime.now().getOffset());
            download.gameVersion = Util.parse(subString, "<span class=\"version-label\">", "</span>");
            download.downloads = Long.valueOf(Util.parse(subString, "<td class=\"project-file-downloads\">", "</td>").replaceAll(",", "").trim());
            download.downloadLink = Util.parse(subString, " href=\"", "\"");
            downloads.add(download);
        }
        updateable.updateProgress(1.0, 1.0);
        updateable.updateMessage("done");
        return downloads;
    }


}
