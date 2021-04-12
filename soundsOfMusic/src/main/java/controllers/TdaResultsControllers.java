package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class TdaResultsControllers {
    public static boolean isFirst;
    public static SimpleStringProperty labelText = new SimpleStringProperty("Анализ не был произведен.");

    public static SimpleStringProperty pathToImage = new SimpleStringProperty();

    public ImageView persistenceDiagramImage;
    public Label descrPersistenceDiagram;

    public void initialize() {
        isFirst = true;
        descrPersistenceDiagram.textProperty().bind(labelText);

        pathToImage.addListener((ob, oldVal, newVal) -> {
            labelText.set("Persistence diagram");
            descrPersistenceDiagram.setVisible(true);
            persistenceDiagramImage.setImage(new Image(new File(newVal).toURI().toString()));
            persistenceDiagramImage.setVisible(true);
        });
    }
}
