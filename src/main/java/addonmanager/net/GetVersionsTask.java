package addonmanager.net;

import addonmanager.core.Addon;
import addonmanager.core.Download;
import addonmanager.core.Status;
import javafx.concurrent.Task;
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

public class GetVersionsTask extends Task<List<Download>> {

    private Addon addon;
    private String addonName;

    public GetVersionsTask(Addon addon, String addonName) {
        super();
        this.addon = addon;
        this.addonName = addonName;

        setOnScheduled(x -> {
            updateProgress(0, 1);
            Status status =new Status();
            status.setNewVersionsTask(this);
            addon.setStatus(status);
        });
        setOnSucceeded(x -> updateProgress(1, 1));
//        setOnCancelled();
//        setOnFailed();
    }

    @Override
    protected List<Download> call() throws Exception {
        List<Download> downloads = new ArrayList<>();

        updateProgress(0, 1);
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("https://www.curseforge.com/wow/addons/" + addonName + "/files");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandler.asString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (response.statusCode() != 200)
            return downloads;
        String input = response.body();


        updateProgress(0.5, 1);

        int index1 = input.indexOf("<div class=\"listing-body\">");
        int index2 = input.substring(index1).indexOf("</table>");
        String data = input.substring(index1, index1 + index2);
        Pattern pattern = Pattern.compile("<tr class=\"project-file-list__item\">");
        Matcher matcher = pattern.matcher(data);

        //System.out.println(addonName+" "+input.length());

        while (matcher.find()) {
            Download download = new Download();
            String subString = data.substring(matcher.start());
            download.release = parse(subString, "<span class=\"file-phase--release\" title=\"", "\"></span>");
            download.title = parse(subString, "<td class=\"project-file__name\" title=\"", "\">");
            download.fileSize = parse(subString, "<span class=\"table__content file__size\">", "</span>");
            String a = parse(subString, "data-epoch=\"", "\"");
            download.fileDateUploaded = LocalDateTime.ofEpochSecond(Integer.parseInt(a), 0, OffsetDateTime.now().getOffset());
            download.gameVersion = parse(subString, "<span class=\"table__content version__label\">", "</span>");
            download.downloads = Long.valueOf(parse(subString, "span class=\"table__content file__download\">", "</span>").replaceAll(",", ""));
            download.downloadLink = parse(subString, " href=\"", "\"");
            downloads.add(download);
        }
        updateProgress(1, 1);
        return downloads;

    }

    private String parse(String input, String start, String end) {
        int startI = input.indexOf(start) + start.length();
        String mid = input.substring(startI);
        return mid.substring(0, mid.indexOf(end));
    }
}
