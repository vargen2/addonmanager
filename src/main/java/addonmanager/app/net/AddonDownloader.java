package addonmanager.app.net;

import addonmanager.app.Addon;
import addonmanager.app.Download;
import addonmanager.app.Updateable;
import addonmanager.app.file.FileOperations;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.*;

class AddonDownloader {

    private final Addon addon;
    private final Download download;

    AddonDownloader(Addon addon, Download download) {
        this.addon = addon;
        this.download = download;
    }

    File downLoad(double from, double to) {


        if (!FileOperations.directoriesExists())
            return null;
        Updateable updateable = addon.getUpdateable();
        updateable.updateMessage("downloading...");
        String addonName = addon.getFolderName();
        //Download download = addon.getLatestDownload();
        double multiplier = 0;
        if (download.getFileSize().contains("MB")) {
            multiplier = 1000000;
        } else if (download.getFileSize().contains("KB")) {
            multiplier = 1000;
        }
        String temp = download.getFileSize().replaceAll("\\p{IsAlphabetic}", "").trim();
        double fileSize = Double.parseDouble(temp.replace(",", ".")) * multiplier;
        String firstUrl = addon.getProjectUrl() + download.getDownloadLink().substring(download.getDownloadLink().indexOf("/files"));
        ///String firstUrl = "https://www.curseforge.com"+download.downloadlink+ "/file";
        CloseableHttpClient client = HttpClients.custom().disableCookieManagement().setRedirectStrategy(new RedirectStrategy()).build();
        HttpContext httpContext = new BasicHttpContext();
        HttpGet get = new HttpGet(firstUrl);
        try (CloseableHttpResponse response = client.execute(get, httpContext)) {
            byte[] buffer = new byte[16384];
            try (InputStream input = response.getEntity().getContent();
                 OutputStream output = new FileOutputStream("temp" + File.separator + addonName + "-" + download.getTitle() + ".zip")) {
                double downloaded = 0;
                for (int length; (length = input.read(buffer)) > 0; ) {
                    output.write(buffer, 0, length);
                    downloaded += length;
                    //updateable.updateProgress(from+(to-from)barMax * (downloaded / fileSize), 1.0);
                    updateable.updateProgress(from + (to - from) * (downloaded / fileSize), 1.0);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File("temp" + File.separator + addonName + "-" + download.getTitle() + ".zip");

    }
//
//    File download2(double from, double to) {
//        if (!FileOperations.directoriesExists())
//            return null;
//        Updateable updateable = addon.getUpdateable();
//        updateable.updateMessage("downloading...");
//        String addonName = addon.getFolderName();
//        //Download download = addon.getLatestDownload();
//        double multiplier = 0;
//        if (download.getFileSize().contains("MB")) {
//            multiplier = 1000000;
//        } else if (download.getFileSize().contains("KB")) {
//            multiplier = 1000;
//        }
//        String temp = download.getFileSize().replaceAll("\\p{IsAlphabetic}", "").trim();
//        double fileSize = Double.parseDouble(temp.replace(",", ".")) * multiplier;
//        String firstUrl = addon.getProjectUrl() + download.getDownloadLink().substring(download.getDownloadLink().indexOf("/files"));
//
//        HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
//        System.out.println("dl1");
//
//        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(firstUrl)).build();
//        System.out.println("dl2");
//        try {
//            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofFileDownload(Path.of("temp" + File.separator)));
//
//            System.out.println(response.statusCode());
//            return response.body().toFile();
//        } catch (IOException | InterruptedException e) {
//            App.LOG.severe(e.getMessage());
//        }
//        System.out.println("dl3");
//        return null;
//    }
//
//        HttpContext httpContext = new BasicHttpContext();
//        HttpGet get = new HttpGet(firstUrl);
//        try (CloseableHttpResponse response = client.execute(get, httpContext)) {
//            byte[] buffer = new byte[16384];
//            try (InputStream input = response.getEntity().getContent();
//                 OutputStream output = new FileOutputStream("temp" + File.separator + addonName + "-" + download.getTitle() + ".zip")) {
//                double downloaded = 0;
//                for (int length; (length = input.read(buffer)) > 0; ) {
//                    output.write(buffer, 0, length);
//                    downloaded += length;
//                    //updateable.updateProgress(from+(to-from)barMax * (downloaded / fileSize), 1.0);
//                    updateable.updateProgress(from + (to - from) * (downloaded / fileSize), 1.0);
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return new File("temp" + File.separator + addonName + "-" + download.getTitle() + ".zip");



}
