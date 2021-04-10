package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;

public class GeneralStatisticsController {
    @FXML ListView files; // ?
    @FXML TableView frequencyTable;

    @FXML ScrollPane scrollFiles;
    ObservableList<String> loadedFiles = FXCollections.observableArrayList();

    public void setFilesList(ObservableList<String> filesLst) {
        loadedFiles = filesLst;
        files.setItems(loadedFiles);
        scrollFiles.setManaged(true);
        System.out.println("setFilesList called");
        System.out.println("files length is " + filesLst.size());
    }

    public void initialize() {
        //files.setItems(loadedFiles);
        System.out.println("initialize called");
    }
}
