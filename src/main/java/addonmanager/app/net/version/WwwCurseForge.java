package addonmanager.app.net.version;

import addonmanager.app.Updateable;
import addonmanager.app.Addon;
import addonmanager.app.Download;
import addonmanager.app.net.Util;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WwwCurseForge extends DownloadVersions {

    public WwwCurseForge(Addon addon) {
        super(addon);
    }

    @Override
    public List<Download> getDownloads() {
        Updateable updateable = addon.getUpdateable();
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
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (response.statusCode() != 200) {
                System.err.println("DL fail " + anUrlName + " foldername: " + addon.getFolderName() + " title:" + addon.getTitle());
                retrying += ".";
                updateable.updateMessage(retrying);
                continue;

            } else {
                input = response.body();
                System.out.println("FOUND "+addon.getProjectUrl() +" "+ input.length());
                break;
            }

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
        Pattern pattern = Pattern.compile("<tr class=\"project-file-list__item\">");
        Matcher matcher = pattern.matcher(data);


        while (matcher.find()) {

            String subString = data.substring(matcher.start());
            String temp = Util.parse(subString, "<td class=\"project-file__release-type\">", "</td>");
            String release = Util.parse(temp, "title=\"", "\"></span>");
            String title = Util.parse(subString, "<td class=\"project-file__name\" title=\"", "\">");
            String fileSize = Util.parse(subString, "<span class=\"table__content file__size\">", "</span>").trim();
            String a = Util.parse(subString, "data-epoch=\"", "\"");
            LocalDateTime fileDateUploaded = LocalDateTime.ofEpochSecond(Integer.parseInt(a), 0, OffsetDateTime.now().getOffset());
            String gameVersion = Util.parse(subString, "<span class=\"table__content version__label\">", "</span>");
            long dls = Long.valueOf(Util.parse(subString, "span class=\"table__content file__download\">", "</span>").replaceAll(",", ""));
            String downloadLink = Util.parse(subString, " href=\"", "\"");
            Download download = new Download(release, title, fileSize, fileDateUploaded, gameVersion, dls, downloadLink);

            downloads.add(download);
        }
        updateable.updateProgress(1.0, 1.0);
        updateable.updateMessage("done");
        return downloads;
    }


}
