import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("start");
        experiment();

    }

    public static void experiment() {
        HttpClient httpClient=HttpClient.newHttpClient();
        URI uri = URI.create("https://wow.curseforge.com/api/game/versions");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                //.timeout(Duration.ofMinutes(1))
                //.header("Content-Type", "application/json")
                .header("X-Api-Token","b6164a56-2080-4b24-ac3a-a47b16a8b553")
                //.POST(BodyPublisher.fromFile(Paths.get("file.json")))
                .build();

        HttpResponse<String> response =
                null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandler.asString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(response.statusCode());
        System.out.println(response.body());
        Gson gson = new Gson();
        //TypeToken<ArrayList<GameVersion>> gameVersions = new TypeToken<ArrayList<GameVersion>>();
        List<GameVersion> gameVersionList=gson.fromJson(response.body(),TypeToken.getParameterized(ArrayList.class, GameVersion.class).getType());

        gameVersionList.forEach(System.out::println);

















        uri = URI.create("https://wow.curseforge.com/projects/weakauras-2");

        request = HttpRequest.newBuilder()
                .uri(uri)
                //.timeout(Duration.ofMinutes(1))
                //.header("Content-Type", "application/json")
                .header("X-Api-Token","b6164a56-2080-4b24-ac3a-a47b16a8b553")
                //.POST(BodyPublisher.fromFile(Paths.get("file.json")))
                .build();

         response =
                null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandler.asString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(response.statusCode());
        System.out.println(response.body());
        //Gson gson = new Gson();
        //TypeToken<ArrayList<GameVersion>> gameVersions = new TypeToken<ArrayList<GameVersion>>();
        //List<GameVersion> gameVersionList=gson.fromJson(response.body(),TypeToken.getParameterized(ArrayList.class, GameVersion.class).getType());

        //gameVersionList.forEach(System.out::println);

    }

}
