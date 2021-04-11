package controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pojo.CompositionChord;

public class GeneralStatisticsController {
    public static ObservableList<CompositionChord> rows = FXCollections.observableArrayList();
    public static Image histogramImage;

    @FXML
    TableColumn<CompositionChord, String> chordColumn;
    @FXML
    TableColumn<CompositionChord, Number> frequencyColumn;
    @FXML
    TableView<CompositionChord> frequencyTable;
    @FXML
    ImageView histogram;

    public void initialize() {
        frequencyTable.setEditable(true);
        chordColumn.prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));
        frequencyColumn.prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));

        chordColumn.setResizable(false);
        frequencyColumn.setResizable(false);

        chordColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().chord));
        frequencyColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().frequency));

        frequencyTable.setItems(rows);

        histogram.setImage(histogramImage);
    }
}
