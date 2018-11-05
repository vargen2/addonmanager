package addonmanager.net;

import addonmanager.Updateable;
import addonmanager.core.Addon;
import addonmanager.core.Download;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DownloadAddon {

    public static File downLoadFile(Addon addon) {
        return downLoadFile(addon, Updateable.EMPTY_UPDATEABLE);
    }

    public static File downLoadFile(Addon addon, Updateable updateable) {
        return downLoadFile(addon, 1, updateable);
    }

    public static File downLoadFile(Addon addon, double barMax, Updateable updateable) {
        updateable.updateMessage("downloading...");
        String addonName = addon.getFolderName();
        Download download = addon.getLatestDownload();
        double multiplier = 0;
        if (download.fileSize.contains("MB")) {
            multiplier = 1000000;
        } else if (download.fileSize.contains("KB")) {
            multiplier = 1000;
        }
        String temp = download.fileSize.replaceAll("\\p{IsAlphabetic}", "").trim();
        double fileSize = Double.parseDouble(temp.replace(",", ".")) * multiplier;
        String firstUrl = addon.getProjectUrl() + download.downloadLink.substring(download.downloadLink.indexOf("/files"));
        ///String firstUrl = "https://www.curseforge.com"+download.downloadlink+ "/file";
        CloseableHttpClient client = HttpClients.custom().disableCookieManagement().setRedirectStrategy(new RedirectStrategy()).build();
        HttpContext httpContext = new BasicHttpContext();
        HttpGet get = new HttpGet(firstUrl);
        try (CloseableHttpResponse response = client.execute(get, httpContext)) {
            byte[] buffer = new byte[16384];
            try (InputStream input = response.getEntity().getContent();
                 OutputStream output = new FileOutputStream("temp" + File.separator + addonName + "-" + download.title + ".zip")) {
                double downloaded = 0;
                for (int length; (length = input.read(buffer)) > 0; ) {
                    output.write(buffer, 0, length);
                    downloaded += length;
                    updateable.updateProgress(barMax * (downloaded / fileSize), 1.0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File("temp" + File.separator + addonName + "-" + download.title + ".zip");

    }


}
