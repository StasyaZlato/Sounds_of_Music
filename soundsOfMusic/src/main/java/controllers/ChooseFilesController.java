package controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pojo.CompositionsResponse;
import tasks.AnnTask;
import tasks.BasePreprocessingTask;
import tasks.FourierTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseFilesController {

    public static CompositionsResponse response;
    public ListView filesChosenLst;
    public ScrollPane scrollFiles;
    public CheckBox fourier;
    public CheckBox ann;
    public Label lbl;
    public Slider chordDurationSlider;
    public Button proceedBtn;
    ObservableList<String> chosenFiles = FXCollections.observableArrayList();

    public static Parent root;

    public void initialize() {
        proceedBtn.disableProperty().bind(Bindings.size(chosenFiles).lessThan(1));
    }

    public void openFilePicker(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Choose audios", "*.wav");
        fileChooser.getExtensionFilters().add(extFilter);
        List<File> chosenFile = fileChooser.showOpenMultipleDialog(((Node) actionEvent.getSource()).getScene().getWindow());

        List<String> paths = chosenFile.stream().map(File::getAbsolutePath).collect(Collectors.toList());

        chosenFiles.clear();

        if (!chosenFile.isEmpty()) {
            chosenFiles.addAll(paths);

            filesChosenLst.setItems(chosenFiles);
            scrollFiles.setManaged(true);
        }
    }

    private void saveFilesList(MouseEvent mouseEvent) {
        double chordDuration = chordDurationSlider.getValue();
        Stage stage = new Stage();

        BasePreprocessingTask task;
        try {
            if (ann.isSelected()) {
                task = new AnnTask(new ArrayList<>(chosenFiles), chordDuration);
            } else {
                task = new FourierTask(new ArrayList<>(chosenFiles), chordDuration);
            }

            FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/fxml/loading_dialog.fxml"));
            Parent parent = dialogLoader.load();

            Scene dialogScene = new Scene(parent, 300, 200);
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(dialogScene);

            task.setOnRunning((e) -> dialogStage.show());

            task.setOnSucceeded(event -> {
                dialogStage.close();
                response = task.getValue();
                System.out.println(response);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                try {
                    root = loader.load();

                    MainMenuController controller = loader.getController();
                    controller.setFilesList(chosenFiles);

                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            new Thread(task).start();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    public void proceed(MouseEvent mouseEvent) {
        saveFilesList(mouseEvent);
    }

    public void fourierCheckBoxHandler(ActionEvent actionEvent) {
        ann.selectedProperty().setValue(!ann.isSelected());
    }

    public void annCheckBoxHandler(ActionEvent actionEvent) {
        fourier.selectedProperty().setValue(!fourier.isSelected());
    }
}
