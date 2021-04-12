package controllers;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pojo.CompositionChord;
import tasks.TdaPreprocessingTask;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainMenuController {
    public static Node generalStatisticsScene;
    public static Node graphsScene;
    public static Node tonnetzScene;
    public static Node tdaScene;
    public ScrollPane scrollFiles;
    public ListView<String> files;
    public Button infoTab;
    public Button tonnetzTab;
    public Button tdaTab;
    public Button scrollPaneEnableBtn;
    public Button graphsTab;
    int current = 0;
    ObservableList<String> loadedFiles = FXCollections.observableArrayList();

    public void initialize() {
        buttonDesignChange(0);

        scrollPaneEnableBtn.setMaxHeight(Double.MAX_VALUE);

        files.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            String selectedFile = files.getSelectionModel().getSelectedItem();
            int id = files.getSelectionModel().getSelectedIndex();
            String pathToComposition = ChooseFilesController.response.getById(id).getFilename();
            Path folderPath = Path.of("generated_images", getFileNameWithoutExtension(pathToComposition));
            List<CompositionChord> data = ChooseFilesController.response.getById(id).getChords();
            switch (current) {
                case 0:
                    GeneralStatisticsController.rows.clear();
                    GeneralStatisticsController.rows.addAll(data);

                    Path histogramPath = Path.of(folderPath.toString(), "histogram.png").toAbsolutePath();
                    GeneralStatisticsController.pathToHistogram.set(histogramPath.toString());
                    break;
                case 1:
                    Path waveplotPath = Path.of(folderPath.toString(), "waveplot.png").toAbsolutePath();
                    Path chromagramPath = Path.of(folderPath.toString(), "chromagram.png").toAbsolutePath();

                    GeneralGraphsController.pathToChromagram.set(chromagramPath.toString());
                    GeneralGraphsController.pathToWaveplot.set(waveplotPath.toString());
                    break;
                case 2:
                    TonnetzResultsController.setChords(data);
                    TonnetzResultsController.watcher.set(true);
                    break;
                case 3:
                    Path persistenceDiagramPath = Path.of(folderPath.toString(), "persistence_diagram.png").toAbsolutePath();
                    TdaResultsControllers.pathToImage.set(persistenceDiagramPath.toString());
                    break;
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
        files.getSelectionModel().clearSelection();


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
                openTDAResultsScene();
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

    public void openTDAResultsScene() throws IOException {
        ((BorderPane) ChooseFilesController.root).setCenter(tdaScene);

        if (TdaResultsControllers.isFirst) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Выполнить анализ?");
            alert.setHeaderText("Вы уверены, что хотите запустить анализ? Это может занять некоторое время.");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.isEmpty()) {
                TdaResultsControllers.labelText.setValue("Анализ не был произведен");
            } else if (option.get() == ButtonType.OK) {
//                TdaResultsControllers.labelText.set("Выполняется анализ");

                TdaPreprocessingTask task = new TdaPreprocessingTask(new ArrayList<>(loadedFiles));

                FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/fxml/loading_dialog.fxml"));
                Parent parent = dialogLoader.load();

                Scene dialogScene = new Scene(parent, 300, 200);
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(dialogScene);

                task.setOnRunning((e) -> dialogStage.show());

                task.setOnSucceeded(event -> {
                    dialogStage.close();
                    TdaResultsControllers.labelText.setValue("Выберите файл");
                });

                new Thread(task).start();

                TdaResultsControllers.isFirst = false;
            } else if (option.get() == ButtonType.CANCEL) {
                TdaResultsControllers.labelText.setValue("Анализ не был произведен");
            }
        }
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
        } else {
            GeneralGraphsController.maxWidth.set(900);
            GeneralStatisticsController.maxWidth.set(900);
        }
    }
}
