package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseFilesController {

    public ListView filesChosenLst;
    public ScrollPane scrollFiles;
    ObservableList<String> chosenFiles = FXCollections.observableArrayList();

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
//            filesChosenLst.setCellFactory(new Callback<ListView<String>, ListCell<String>>()
//            {
//                @Override
//                public ListCell<String> call(ListView<String> listView)
//                {
//                    return new ListViewCell();
//                }
//            });
        }
    }

    private void saveFilesList(MouseEvent mouseEvent) {
        Node node = (Node) mouseEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/general_statistics.fxml"));
            Parent root = loader.load();

            GeneralStatisticsController controller = loader.getController();
            controller.setFilesList(chosenFiles);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    public void proceed(MouseEvent mouseEvent) {
        saveFilesList(mouseEvent);
    }
}
