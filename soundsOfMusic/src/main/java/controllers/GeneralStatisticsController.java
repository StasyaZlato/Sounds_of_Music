package controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pojo.CompositionChord;

public class GeneralStatisticsController {
    @FXML TableColumn<CompositionChord, String> chordColumn;
    @FXML TableColumn<CompositionChord, Number> frequencyColumn;
    @FXML TableView<CompositionChord> frequencyTable;
    ObservableList<CompositionChord> rows = FXCollections.observableArrayList();

    public void initialize() {
        frequencyTable.setEditable(true);
        chordColumn.prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));
        frequencyColumn.prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));

        chordColumn.setResizable(false);
        frequencyColumn.setResizable(false);

        chordColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().chord));
        frequencyColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().frequency));
        rows.add(new CompositionChord(0, "A"));


        frequencyTable.setItems(rows);
    }

    public void onFileSelected(Object selectedFile) {
        System.out.println(selectedFile);
        CompositionChord element = new CompositionChord(0, "A");
        //rows = FXCollections.observableArrayList();
        frequencyTable.getItems().add(element);
        frequencyTable.setItems(rows);
    }
}
