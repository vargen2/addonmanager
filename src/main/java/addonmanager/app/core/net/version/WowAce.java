package addonmanager.app.core.net.version;

import addonmanager.app.core.Addon;
import addonmanager.app.core.Download;
import addonmanager.app.core.net.Util;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WowAce extends DownloadVersions {

    public WowAce(Addon addon, Consumer<String> updateMessage, BiConsumer<Double, Double> updateProgress) {
        super(addon, updateMessage, updateProgress);
    }

    @Override
    public List<Download> getDownloads() {
        List<Download> downloads = new ArrayList<>();

        String filePage = "";
        if (page != 0)
            filePage = "?page=" + page;
        //updateProgress(0, 1);
        String retrying = "retrying";
        String input = "";

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri;
        try {
            uri = URI.create(addon.getProjectUrl() + "/files"+filePage);

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

            updateMessage.accept("DL fail " + addon.getProjectUrl());
            return downloads;

        } else {
            input = response.body();

        }


        if (input.length() == 0) {
            updateMessage.accept("failed...");
            //System.out.println("failed.." + addon.getProjectUrl());
            return downloads;
        }
        updateMessage.accept("parsing...");
        updateProgress.accept(0.7, 1.0);

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
            String tempDL = Util.parse(subString, "<td class=\"project-file-downloads\">", "</td>").replaceAll(",", "").trim();
            download.downloads = Long.valueOf(tempDL);
            download.downloadLink = Util.parse(subString, " href=\"", "\"");
            downloads.add(download);
        }
        updateProgress.accept(1.0, 1.0);
        updateMessage.accept("done");
        return downloads;
    }


}
