package addonmanager;

import addonmanager.app.file.Saver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    // TODO: 2018-11-05 Logga alla exceptions och eller visa dom till något statusmessage

    @Override
    public void start(Stage primaryStage) throws Exception {
        //todo splitta upp i flera trådar load gui och app + filer
        Parent root = FXMLLoader.load(getClass().getResource("../gui.fxml"));
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setTitle("Addon Manager");
        //new JMetro(JMetro.Style.LIGHT).applyTheme(root);
        primaryStage.setScene(new Scene(root, 960, 727));
        primaryStage.setY(0);
        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            t.consume();
            Saver.exit();
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
