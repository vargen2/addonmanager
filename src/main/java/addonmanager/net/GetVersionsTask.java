package addonmanager.net;

import addonmanager.core.Addon;
import addonmanager.core.Download;
import addonmanager.net.version.DownloadVersions;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class GetVersionsTask extends Task<List<Download>> {

    private Addon addon;
    //private String[] urlNames = new String[3];

    public GetVersionsTask(Addon addon) {
        super();
        this.addon = addon;
//        urlNames[0] = addon.getTitle().replaceAll(" ", "-");
//        urlNames[1] = addon.getFolderName();
//        urlNames[2] = addon.getTitle();
        setOnScheduled(x -> {
            updateMessage("connecting...");
            // updateProgress(0, 1);
//            Status status = new Status();
//            status.setFolderName(addon.getFolderName());
//            status.setNewVersionsTask(this);
//            addon.setStatus(status);
            addon.setNewVersionsTask(this);
        });
//        setOnSucceeded(x -> {
//            updateMessage("done");
//            updateProgress(1, 1);
//        });
//        setOnCancelled();
//        setOnFailed();
    }

    @Override
    protected List<Download> call() throws Exception {
        List<Download> downloads = new ArrayList<>();

        if (addon.getProjectUrl() == null)
            addon.setProjectUrl(FindProject.find(addon));
        String projectUrl = addon.getProjectUrl();


        DownloadVersions downloadVersions = DownloadVersions.createDownloadVersion(addon, this::updateMessage, this::updateProgress);

        downloads = downloadVersions.getDownloads();
        if (downloads.isEmpty())
            return downloads;
        int page = 2;
        while (downloads.stream().noneMatch(x -> x.release.equalsIgnoreCase(Addon.ReleaseType.RELEASE.toString()))) {
            DownloadVersions moreDownloadversions = DownloadVersions.createDownloadVersion(addon, this::updateMessage, this::updateProgress);
            moreDownloadversions.setPage(page);
            downloads.addAll(moreDownloadversions.getDownloads());
            page++;
        }
        return downloads;
        //updateProgress(0, 1);
//        String retrying = "retrying";
//        String input = "";
//        for (String anUrlName : urlNames) {
//            HttpClient httpClient = HttpClient.newHttpClient();
//            URI uri;
//            try {
//                String dl = "https://www.curseforge.com/wow/addons/" + anUrlName;
//                if (!projectUrl.equals("https://www.curseforge.com/wow/addons/"))
//                    dl = projectUrl;
//
//                uri = URI.create(dl + "/files");
//
//            } catch (IllegalArgumentException e) {
//                continue;
//            }
//
//            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
//            HttpResponse<String> response = null;
//            try {
//                response = httpClient.send(request, HttpResponse.BodyHandler.asString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if (response.statusCode() != 200) {
//                System.out.println("DL fail " + anUrlName + " foldername: " + addon.getFolderName() + " title:" + addon.getTitle());
//                retrying += ".";
//                updateMessage(retrying);
//                continue;
//
//            } else {
//                input = response.body();
//                System.out.println("FOUND "+addon.getProjectUrl() +" "+ input.length());
//                break;
//            }
//
//        }
//        if (input.length() == 0) {
//            updateMessage("failed...");
//            System.out.println("failed.." + addon.getProjectUrl());
//            return downloads;
//        }
//        updateMessage("parsing...");
//        updateProgress(0.7, 1);
//
//        int index1 = input.indexOf("<div class=\"listing-body\">");
//        int index2 = input.substring(index1).indexOf("</table>");
//        String data = input.substring(index1, index1 + index2);
//        Pattern pattern = Pattern.compile("<tr class=\"project-file-list__item\">");
//        Matcher matcher = pattern.matcher(data);
//
//
//        while (matcher.find()) {
//            Download download = new Download();
//            String subString = data.substring(matcher.start());
//            String temp = parse(subString, "<td class=\"project-file__release-type\">", "</td>");
//            download.release = parse(temp, "title=\"", "\"></span>");
//            download.title = parse(subString, "<td class=\"project-file__name\" title=\"", "\">");
//            download.fileSize = parse(subString, "<span class=\"table__content file__size\">", "</span>");
//            String a = parse(subString, "data-epoch=\"", "\"");
//            download.fileDateUploaded = LocalDateTime.ofEpochSecond(Integer.parseInt(a), 0, OffsetDateTime.now().getOffset());
//            download.gameVersion = parse(subString, "<span class=\"table__content version__label\">", "</span>");
//            download.downloads = Long.valueOf(parse(subString, "span class=\"table__content file__download\">", "</span>").replaceAll(",", ""));
//            download.downloadLink = parse(subString, " href=\"", "\"");
//            downloads.add(download);
//        }
//        updateProgress(1, 1);
//        updateMessage("done");
//        return downloads;

    }

    public static String parse(String input, String start, String end) {
        int startI = input.indexOf(start) + start.length();
        String mid = input.substring(startI);
        return mid.substring(0, mid.indexOf(end)).trim();
    }


}
