package addonmanager.net.version;

import addonmanager.core.Addon;
import addonmanager.core.Download;
import addonmanager.net.Util;
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

public class WwwCurseForge extends DownloadVersions {

    public WwwCurseForge(Addon addon, Consumer<String> updateMessage, BiConsumer<Double, Double> updateProgress) {
        super(addon, updateMessage, updateProgress);
    }

    @Override
    public List<Download> getDownloads() {
        List<Download> downloads = new ArrayList<>();

        String[] urlNames = new String[3];
        urlNames[0] = addon.getTitle().replaceAll(" ", "-");
        urlNames[1] = addon.getFolderName();
        urlNames[2] = addon.getTitle();
        //updateProgress(0, 1);
        String retrying = "retrying";
        String input = "";
        for (String anUrlName : urlNames) {
            HttpClient httpClient = HttpClient.newHttpClient();
            URI uri;
            try {
                uri = URI.create("https://www.curseforge.com/wow/addons/" + anUrlName + "/files");

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
                System.out.println("DL fail " + anUrlName + " foldername: " + addon.getFolderName() + " title:" + addon.getTitle());
                retrying += ".";
                updateMessage.accept(retrying);
                continue;

            } else {
                input = response.body();
                System.out.println("FOUND "+addon.getProjectUrl() +" "+ input.length());
                break;
            }

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
        Pattern pattern = Pattern.compile("<tr class=\"project-file-list__item\">");
        Matcher matcher = pattern.matcher(data);


        while (matcher.find()) {
            Download download = new Download();
            String subString = data.substring(matcher.start());
            String temp = Util.parse(subString, "<td class=\"project-file__release-type\">", "</td>");
            download.release = Util.parse(temp, "title=\"", "\"></span>");
            download.title = Util.parse(subString, "<td class=\"project-file__name\" title=\"", "\">");
            download.fileSize = Util.parse(subString, "<span class=\"table__content file__size\">", "</span>").trim();
            String a = Util.parse(subString, "data-epoch=\"", "\"");
            download.fileDateUploaded = LocalDateTime.ofEpochSecond(Integer.parseInt(a), 0, OffsetDateTime.now().getOffset());
            download.gameVersion = Util.parse(subString, "<span class=\"table__content version__label\">", "</span>");
            download.downloads = Long.valueOf(Util.parse(subString, "span class=\"table__content file__download\">", "</span>").replaceAll(",", ""));
            download.downloadLink = Util.parse(subString, " href=\"", "\"");
            downloads.add(download);
        }
        updateProgress.accept(1.0, 1.0);
        updateMessage.accept("done");
        return downloads;
    }


}
