import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Experi {

    public void experimentRedirect(String addonName) {
        String result = Main.readFromUrl("https://www.curseforge.com/wow/addons/" + addonName + "/files");
        List<Download> downloads = Main.parseToDownloads(result);

        //System.out.println(downloads);

        Download download = downloads.get(0);
        String firstUrl = "https://www.curseforge.com" + download.downloadLink + "/file";

        System.out.println("Connecting...");
        CloseableHttpClient client = HttpClients.custom().disableCookieManagement().setRedirectStrategy(new MyRedirect()).build();
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
            System.out.println("Downloading file...");
            input = response.getEntity().getContent();
//            for (Header a : response.getAllHeaders()) {
//                System.out.println(a.getName() + " :::::: "+ a.getValue());
//                //Arrays.stream(a.getElements()).forEach(System.out::println);
//            }
            //System.out.println("experi: " + tempFilename);
            //System.out.println(httpContext.toString());

            output = new FileOutputStream(System.getProperty("user.home") + File.separator + "Documents" + File.separator + addonName + "-" + download.title + ".zip");
            for (int length; (length = input.read(buffer)) > 0; ) {
                output.write(buffer, 0, length);
            }
            System.out.println("File successfully downloaded!");
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
    }
}
