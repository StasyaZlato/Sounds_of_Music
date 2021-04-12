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
import javafx.scene.layout.VBox;
import pojo.CompositionChord;

import java.io.File;

public class GeneralStatisticsController {
    public static ObservableList<CompositionChord> rows = FXCollections.observableArrayList();
    public static Image histogramImage;

    public static SimpleStringProperty pathToHistogram = new SimpleStringProperty();
    public static SimpleDoubleProperty maxWidth = new SimpleDoubleProperty();

    @FXML
    VBox generalStatsVbox;
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

        pathToHistogram.addListener((obs, oldVal, newVal) -> {
            histogram.setImage(new Image(new File(newVal).toURI().toString()));
            histogram.setVisible(true);
        });

        maxWidth.addListener((obs, oldVal, newVal) -> {
            histogram.setFitWidth(newVal.doubleValue());
        });

        histogram.setImage(histogramImage);

        frequencyTable.prefHeightProperty().bind(generalStatsVbox.heightProperty().multiply(0.5));
    }
}
