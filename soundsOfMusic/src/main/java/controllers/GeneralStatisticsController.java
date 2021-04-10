package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;

public class GeneralStatisticsController {
    @FXML TableView frequencyTable;
//    ObservableList<String> loadedFiles = FXCollections.observableArrayList();


    public void initialize() {
        //files.setItems(loadedFiles);
        System.out.println("initialize called");
    }
}
