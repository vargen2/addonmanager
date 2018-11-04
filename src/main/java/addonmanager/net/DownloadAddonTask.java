package addonmanager.net;

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

public class DownloadAddonTask {

    public File downLoadFile(String addonName, Download download, Consumer<String> updateMessage, BiConsumer<Double, Double> updateProgress) {

        String firstUrl = "https://www.curseforge.com" + download.downloadLink + "/file";


        updateMessage.accept("connecting...");
        updateProgress.accept(0.1, 1.0);
        CloseableHttpClient client = HttpClients.custom().disableCookieManagement().setRedirectStrategy(new RedirectStrategy()).build();
        HttpContext httpContext = new BasicHttpContext();

        HttpGet get = new HttpGet(firstUrl);
        CloseableHttpResponse response = null;
        //todo closeable try instead?
        try {
            response = client.execute(get, httpContext);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream input = null;
        OutputStream output = null;
        byte[] buffer = new byte[4096];

        try {
            updateProgress.accept(0.2, 1.0);
            updateMessage.accept("downloading file...");
            input = response.getEntity().getContent();

            //output = new FileOutputStream(System.getProperty("user.home") + File.separator + "Documents" + File.separator + addonName + "-" + download.title + ".zip");
            output = new FileOutputStream("temp" + File.separator + addonName + "-" + download.title + ".zip");
            for (int length; (length = input.read(buffer)) > 0; ) {
                output.write(buffer, 0, length);
            }

            updateProgress.accept(0.3, 1.0);
            updateMessage.accept("file downloaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) try {
                output.close();
            } catch (IOException logOrIgnore) {
            }
            if (input != null) try {
                input.close();
            } catch (IOException logOrIgnore) {
            }
        }
        return new File("temp" + File.separator + addonName + "-" + download.title + ".zip");
    }


}
