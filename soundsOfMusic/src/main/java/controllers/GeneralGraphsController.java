package controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;

public class GeneralGraphsController {
    public static SimpleStringProperty pathToWaveplot = new SimpleStringProperty();
    public static SimpleStringProperty pathToChromagram = new SimpleStringProperty();
    public static SimpleDoubleProperty maxWidth = new SimpleDoubleProperty();
    public ImageView waveplotImageView;
    public ImageView chromagramImageView;
    public Label descrWaveplot;
    public Label descrChromagram;
    public VBox graphVB;

    public void initialize() {
        pathToWaveplot.addListener((obs, oldVal, newVal) -> {
            waveplotImageView.setImage(new Image(new File(newVal).toURI().toString()));
            waveplotImageView.setVisible(true);
            descrWaveplot.setVisible(true);
            System.out.println("image set");
        });
        pathToChromagram.addListener((obs, oldVal, newVal) -> {
            chromagramImageView.setImage(new Image(new File(newVal).toURI().toString()));
            chromagramImageView.setVisible(true);
            descrChromagram.setVisible(true);
        });
        maxWidth.addListener((obs, oldVal, newVal) -> {
            chromagramImageView.setFitWidth(newVal.doubleValue());
            waveplotImageView.setFitWidth(newVal.doubleValue());
        });
    }
}
