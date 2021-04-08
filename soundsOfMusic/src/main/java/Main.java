import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        Application.launch(MainLaunch.class);
    }

    public static class MainLaunch extends Application {
        @Override
        public void start(Stage stage) throws IOException {
            Parent open = FXMLLoader.load(MainLaunch.class.getResource("/fxml/choose_files.fxml"));

            stage.setTitle("Sounds of music");

            Scene scene = new Scene(open, 600, 400, Color.color(174.0/255, 174.0/255, 174.0/255));

            stage.setScene(scene);
            stage.show();
        }
    }
}