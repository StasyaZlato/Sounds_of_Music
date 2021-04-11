package controllers;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pojo.CompositionChord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.util.List;

public class MainMenuController {
    public ScrollPane scrollFiles;
    public ListView<String> files;
    public Button infoTab;
    public Button tonnetzTab;
    public Button tdaTab;
    public Button scrollPaneEnableBtn;
    public Button graphsTab;

    int current = 0;

    ObservableList<String> loadedFiles = FXCollections.observableArrayList();

    public static Node generalStatisticsScene;
    public static Node graphsScene;
    public static Node tonnetzScene;
    public static Node tdaScene;

    public void initialize() {
        buttonDesignChange(0);

        scrollPaneEnableBtn.setMaxHeight(Double.MAX_VALUE);

        files.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            String selectedFile = files.getSelectionModel().getSelectedItem();
            int id = files.getSelectionModel().getSelectedIndex();
            String pathToComposition = ChooseFilesController.response.getById(id).getFilename();
            Path folderPath = Path.of("generated_images", getFileNameWithoutExtension(pathToComposition));
            if (current == 0) {
                GeneralStatisticsController.rows.clear();

                List<CompositionChord> data = ChooseFilesController.response.getById(id).getChords();
                GeneralStatisticsController.rows.addAll(data);

                Path histogramPath = Path.of(folderPath.toString(), "histogram.png").toAbsolutePath();
                GeneralStatisticsController.pathToHistogram.set(histogramPath.toString());
            } else if (current == 1) {
                Path waveplotPath = Path.of(folderPath.toString(), "waveplot.png").toAbsolutePath();
                Path chromagramPath = Path.of(folderPath.toString(), "chromagram.png").toAbsolutePath();

                GeneralGraphsController.pathToChromagram.set(chromagramPath.toString());
                GeneralGraphsController.pathToWaveplot.set(waveplotPath.toString());
            }
        });

        try {
            generalStatisticsScene = FXMLLoader.load(getClass().getResource("/fxml/general_statistics.fxml"));
            graphsScene = FXMLLoader.load(getClass().getResource("/fxml/general_graphs.fxml"));
            tonnetzScene = FXMLLoader.load(getClass().getResource("/fxml/tonnetz_results.fxml"));
            tdaScene = FXMLLoader.load(getClass().getResource("/fxml/tda_results.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Произошла ошибка во время загрузки сцены! Возможно, архив " +
                    "приложения был поврежден.");
            alert.show();
        }
    }

    public String getFileNameWithoutExtension(String path) {
        String fileName = Paths.get(path).getFileName().toString();

        String[] parts = fileName.split("\\.");

        return parts[0];
    }

    public void setFilesList(ObservableList<String> filesLst) {
        loadedFiles = filesLst;
        files.setItems(loadedFiles);
        scrollFiles.setManaged(true);
    }

    public void openNextTab(MouseEvent mouseEvent) throws IOException {
        Button b = (Button) (mouseEvent.getSource());

        switch (b.getId()) {
            case "infoTab":
                buttonDesignChange(0);
                openGeneralStatisticsScene();
                break;
            case "graphsTab":
                buttonDesignChange(1);
                openGraphsScene();
                break;
            case "tonnetzTab":
                buttonDesignChange(2);
                openTonnetzResultsScene();
                break;
            case "tdaTab":
                buttonDesignChange(3);
                openTADResultsScene();
                break;
        }
    }

    public void openGeneralStatisticsScene() {
        ((BorderPane) ChooseFilesController.root).setCenter(generalStatisticsScene);
    }

    public void openGraphsScene() {
        ((BorderPane) ChooseFilesController.root).setCenter(graphsScene);
    }

    public void openTonnetzResultsScene() {
        ((BorderPane) ChooseFilesController.root).setCenter(tonnetzScene);
    }

    public void openTADResultsScene() {
        ((BorderPane) ChooseFilesController.root).setCenter(tdaScene);
    }

    private void buttonDesignChange(int id) {
        Button[] buttons = {infoTab, graphsTab, tonnetzTab, tdaTab};
        VBox.setVgrow(buttons[id], Priority.ALWAYS);
        buttons[id].setMaxHeight(Double.MAX_VALUE);
        buttons[id].getStyleClass().clear();
        buttons[id].getStyleClass().add("active");
        buttons[id].getStyleClass().add("button");
        current = id;

        for (int i = 0; i < 4; i++) {
            if (i != id) {
                VBox.setVgrow(buttons[i], Priority.NEVER);
                buttons[i].getStyleClass().clear();
                buttons[i].getStyleClass().add("passive");
                buttons[i].getStyleClass().add("button");
            }
        }
    }

    public void manageListView(MouseEvent e) {
        ImageView img;
        if (scrollFiles.isManaged()) {
            img = new ImageView("/images/left-arrow-white.png");
        } else {
            img = new ImageView("/images/right-arrow-white.png");
        }
        img.setFitWidth(10);
        img.setPickOnBounds(true);
        img.setPreserveRatio(true);

        scrollPaneEnableBtn.setGraphic(img);

        scrollFiles.setManaged(!scrollFiles.isManaged());

        if (scrollFiles.isManaged()) {
            GeneralGraphsController.maxWidth.set(500);
            GeneralStatisticsController.maxWidth.set(500);
        }
        else {
            GeneralGraphsController.maxWidth.set(900);
            GeneralStatisticsController.maxWidth.set(900);
        }
    }
}
