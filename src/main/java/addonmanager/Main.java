package addonmanager;

import addonmanager.app.App;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;

public class Main extends Application {

    // TODO: 2018-11-05 Logga alla exceptions och eller visa dom till något statusmessage

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../gui.fxml"));
        //primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setTitle("Addon Manager");
        primaryStage.setScene(new Scene(root, 960, 540));
        primaryStage.setY(0);
        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            t.consume();
            Platform.exit();
        });

        App.setFileLoggingLevel(Level.INFO);
        App.setConsoleLoggingLevel(Level.INFO);

    }

    public static void main(String[] args) {
        launch(args);
    }

//    static String hmm(String url) {
//        HttpClient httpClient = HttpClient.newBuilder().build();
//        URI uri = URI.create(url);
//        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
//        HttpResponse<Path> response = null;
//        System.out.println("hit1");
//        try {
//            OpenOption[] options =
//                    new OpenOption[]{StandardOpenOption.CREATE};
//            Path path = FileSystems.getDefault().getPath(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "aaaa");
//            response = httpClient.send(request, HttpResponse.BodyHandler.asFile(path));
//
//        } catch (InterruptedException | IOException e) {
//            e.printStackTrace();
//        }
//        //System.out.println(response.statusCode());
//        //System.out.println(response.headers());
//        String headers = response.headers().toString();
//
//        String newUrl = parse(headers, "location=[", "],");
//
//        return newUrl.replaceAll(" ", "+");
//    }

//    static String hmm2(String url) {
//        HttpClient httpClient = HttpClient.newBuilder().build();
//        URI uri = URI.create(url);
//        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
//        HttpResponse<Path> response = null;
//        System.out.println("hit4");
//        try {
//            OpenOption[] options =
//                    new OpenOption[]{StandardOpenOption.CREATE};
//            Path path = FileSystems.getDefault().getPath(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "aaaa");
//            response = httpClient.send(request, HttpResponse.BodyHandler.asFile(path));
//
//        } catch (InterruptedException | IOException e) {
//            e.printStackTrace();
//        }
//        //System.out.println(response.statusCode());
//        //System.out.println(response.headers());
//        String headers = response.headers().toString();
//
//        //String newUrl = parse(headers, "location=[", "],");
//        System.out.println(headers);
//        return "";
//    }

//    static void experiment4(String addonName) {
//        String result = readFromUrl("https://www.curseforge.com/wow/addons/" + addonName + "/files");
//        List<Download> downloads = parseToDownloads(result);
//
//
//        String firstUrl = "https://www.curseforge.com" + downloads.get(0).downloadLink + "/file";
//        String s = hmm(firstUrl);
//
//        System.out.println(s);
//        String s2 = hmm(s);
//        s2 = s2.replaceAll("edge", "media");
//        System.out.println(s2);
//        System.out.println("Connecting...");
//        CloseableHttpClient client = HttpClients.createDefault();
//        //HttpGet get = new HttpGet("http://apache.mirrors.spacedump.net//httpcomponents/httpclient/binary/httpcomponents-client-5.0-beta1-bin.zip");
//        HttpGet get = new HttpGet(s2);
//        CloseableHttpResponse response = null;
//        try {
//            response = client.execute(get);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        InputStream input = null;
//        OutputStream output = null;
//        byte[] buffer = new byte[1024];
//
//        try {
//            System.out.println("Downloading file...");
//            input = response.getEntity().getContent();
//            output = new FileOutputStream(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "httpcomponents-client-4.0.1-bin.zip");
//            for (int length; (length = input.read(buffer)) > 0; ) {
//                output.write(buffer, 0, length);
//            }
//            System.out.println("File successfully downloaded!");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (output != null) try {
//                output.close();
//            } catch (IOException logOrIgnore) {
//            }
//            if (input != null) try {
//                input.close();
//            } catch (IOException logOrIgnore) {
//            }
//        }
//    }

//    static void experiment3() {
//        CloseableHttpClient httpclient = HttpClients.custom().setRedirectStrategy(LaxRedirectStrategy.INSTANCE).build();
//
//        HttpGet httpGet = new HttpGet("https://addons.cursecdn.com/files/2622/939/OmniCC+8.0.8.zip");
//        CloseableHttpResponse response = null;
//
//        try {
//            response = httpclient.execute(httpGet);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // EntityUtils.consume(entity1);
//        for (Header head : response.getAllHeaders())
//            System.out.println(head);
//
//        HttpEntity entity = response.getEntity();
//        if (entity != null) {
//            String name = response.getFirstHeader("Content-Disposition").getValue();
//            //System.out.println(name);
//            String fileName = name.replaceFirst("(?i)^.*filename=\"([^\"]+)\".*$", "$1");
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream("C:\\" + fileName);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            try {
//                entity.writeTo(fos);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public static void experiment2(String addonName) {
//        String result = readFromUrl("https://www.curseforge.com/wow/addons/" + addonName + "/files");
//        List<Download> downloads = parseToDownloads(result);
//
//
//        HttpClient httpClient = HttpClient.newBuilder().build();
//        URI uri = URI.create("https://www.curseforge.com" + downloads.get(0).downloadLink + "/file");
//        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
//        HttpResponse<Path> response = null;
//        System.out.println("hit1");
//        try {
//            OpenOption[] options =
//                    new OpenOption[]{StandardOpenOption.CREATE};
//            Path path = FileSystems.getDefault().getPath(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "aaaa");
//            response = httpClient.send(request, HttpResponse.BodyHandler.asFile(path));
//
//        } catch (InterruptedException | IOException e) {
//            e.printStackTrace();
//        }
//        //System.out.println(response.statusCode());
//        //System.out.println(response.headers());
//        String headers = response.headers().toString();
//
//        String newUrl = parse(headers, "location=[", "],");
//        //System.out.println(newUrl);
//
//        HttpClient httpClient2 = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
//        //URI uri2 = URI.create(newUrl.replaceAll(" ", "+"));
//        URI uri2 = URI.create("https://media.forgecdn.net/files/2622/939/OmniCC+8.0.8.zip");
//        HttpRequest request2 = HttpRequest.newBuilder().
//                uri(uri2)
//                .header("Content-Disposition", "attachment; filename=cccc")
//                .header("Accept-Encoding", "gzip, compress, br")
//                .header("Accept", "application/zip")
//                .header("Content-Type", "application/zip")
//                //.header("Content-Transfer-Encoding", "Binary")
//                //.header("Content-Length",".filesize($attachment_location)")
//                //.header("Content-Disposition", "attachment")//; filename=filePath")
//                //.header("Content-Disposition", "attachment; filename=cccc")
//                .build();
//
//        HttpResponse<Path> response2 = null;
//
//
//        try {
//            OpenOption[] options =
//                    new OpenOption[]{StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE};
//            Path path = FileSystems.getDefault().getPath(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "dddd");
//            response2 = httpClient2.send(request2, HttpResponse.BodyHandler.asFileDownload(path, options));
//
//        } catch (InterruptedException | IOException e) {
//            e.printStackTrace();
//        } finally {
//            //System.out.println(response2.headers());
//        }
//        //File bbb = response.body().toFile();
//        System.out.println(response2.headers());
//
//        System.out.println("hit2");
//        //response.;
//    }

//    private static String parse(String input, String start, String end) {
//        int startI = input.indexOf(start) + start.length();
//        String mid = input.substring(startI);
//        return mid.substring(0, mid.indexOf(end));
//    }

//    static List<Download> parseToDownloads(String input) {
//        int index1 = input.indexOf("<div class=\"listing-body\">");
//        int index2 = input.substring(index1).indexOf("</table>");
//        String data = input.substring(index1, index1 + index2);
//        Pattern pattern = Pattern.compile("<tr class=\"project-file-list__item\">");
//        Matcher matcher = pattern.matcher(data);
//
//        List<Download> downloads = new ArrayList<>();
//        while (matcher.find()) {
//            Download download = new Download();
//            String subString = data.substring(matcher.start());
//            download.release = parse(subString, "<span class=\"file-phase--release\" title=\"", "\"></span>");
//            download.title = parse(subString, "<td class=\"project-file__name\" title=\"", "\">");
//            download.fileSize = parse(subString, "<span class=\"table__content file__size\">", "</span>");
//            String a = parse(subString, "data-epoch=\"", "\"");
//            download.fileDateUploaded = LocalDateTime.ofEpochSecond(Integer.parseInt(a), 0, OffsetDateTime.now().getOffset());
//            download.gameVersion = parse(subString, "<span class=\"table__content version__label\">", "</span>");
//            download.downloads = Long.valueOf(parse(subString, "span class=\"table__content file__download\">", "</span>").replaceAll(",", ""));
//            download.downloadLink = parse(subString, " href=\"", "\"");
//            downloads.add(download);
//        }
//        return downloads;
//    }

//    static String readFromUrl(String url) {
//        HttpClient httpClient = HttpClient.newHttpClient();
//        URI uri = URI.create(url);
//        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
//        HttpResponse<String> response = null;
//        try {
//            response = httpClient.send(request, HttpResponse.BodyHandler.asString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return response.body();
//    }

//    public static void experiment() {
//        HttpClient httpClient = HttpClient.newHttpClient();
//        URI uri = URI.create("https://wow.curseforge.com/api/game/versions");
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(uri)
//                //.timeout(Duration.ofMinutes(1))
//                //.header("Content-Type", "application/json")
//                .header("X-Api-Token", "b6164a56-2080-4b24-ac3a-a47b16a8b553")
//                //.POST(BodyPublisher.fromFile(Paths.get("file.json")))
//                .build();
//
//        HttpResponse<String> response =
//                null;
//        try {
//            response = httpClient.send(request, HttpResponse.BodyHandler.asString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(response.statusCode());
//        System.out.println(response.body());
//        Gson gson = new Gson();
//        //TypeToken<ArrayList<addonmanager.old.GameVersion>> gameVersions = new TypeToken<ArrayList<addonmanager.old.GameVersion>>();
//        List<GameVersion> gameVersionList = gson.fromJson(response.body(), TypeToken.getParameterized(ArrayList.class, GameVersion.class).getType());
//
//        gameVersionList.forEach(System.out::println);
//
//
//        uri = URI.create("https://wow.curseforge.com/projects/weakauras-2");
//
//        request = HttpRequest.newBuilder()
//                .uri(uri)
//                //.timeout(Duration.ofMinutes(1))
//                //.header("Content-Type", "application/json")
//                .header("X-Api-Token", "b6164a56-2080-4b24-ac3a-a47b16a8b553")
//                //.POST(BodyPublisher.fromFile(Paths.get("file.json")))
//                .build();
//
//        response =
//                null;
//        try {
//            response = httpClient.send(request, HttpResponse.BodyHandler.asString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(response.statusCode());
//        System.out.println(response.body());
//        //Gson gson = new Gson();
//        //TypeToken<ArrayList<addonmanager.old.GameVersion>> gameVersions = new TypeToken<ArrayList<addonmanager.old.GameVersion>>();
//        //List<addonmanager.old.GameVersion> gameVersionList=gson.fromJson(response.body(),TypeToken.getParameterized(ArrayList.class, addonmanager.old.GameVersion.class).getType());
//
//        //gameVersionList.forEach(System.out::println);
//
//    }


}
