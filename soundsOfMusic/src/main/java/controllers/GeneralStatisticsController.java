package controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pojo.CompositionChord;

import java.io.File;
import java.text.DecimalFormat;

public class GeneralStatisticsController {
    public static ObservableList<CompositionChord> rows = FXCollections.observableArrayList();
    public static Image histogramImage;

    public static SimpleStringProperty pathToHistogram = new SimpleStringProperty();
    public static SimpleDoubleProperty maxWidth = new SimpleDoubleProperty();
    public Label description;

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
        description.setWrapText(true);
        frequencyTable.setEditable(true);
        chordColumn.prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));
        frequencyColumn.prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));

        chordColumn.setResizable(false);
        frequencyColumn.setResizable(false);

        chordColumn.setCellValueFactory(c -> {
            String chord = c.getValue().chord;
            String firstCapitalized = Character.toString(chord.charAt(0)).toUpperCase();
            return new SimpleStringProperty(firstCapitalized + chord.substring(1));
        });

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
