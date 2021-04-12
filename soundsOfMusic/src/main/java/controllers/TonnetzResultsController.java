package controllers;

import graphics.TriangleTile;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

public class TonnetzResultsController {
    private static final double rows = 3;
    private static final double tilesPerRow = 8;

    private final String[] tonnetzChords = new String[] {"F#", "A#m", "C#", "Fm", "G#", "Cm", "D#", "Gm",
                                                    "A#", "Dm", "F", "Am", "C", "Em", "G", "Bm",
                                                    "D", "F#m", "A", "C#m", "E", "G#m", "B", "D#m"};

    @FXML
    AnchorPane tonnetz;

    public void initialize() {
        int xStartOffset = 15;
        int yStartOffset = 150;

        boolean upwards = false;
        int count = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < tilesPerRow; x++) {
                double xCoord = x * TriangleTile.SIDE / 2 + xStartOffset + y * TriangleTile.SIDE / 2;
                double yCoord = y * TriangleTile.HEIGHT + yStartOffset;

                // TODO: change color intensity based on chord frequency
                Polygon tile = new TriangleTile(xCoord, yCoord, upwards, Color.ANTIQUEWHITE);
                upwards = !upwards;
                final StackPane stack = new StackPane();
                final Text text = new Text(tonnetzChords[count]);
                ++count;
                stack.getChildren().addAll(tile, text);
                stack.setLayoutX(xCoord);
                stack.setLayoutY(yCoord);
                tonnetz.getChildren().add(stack);
            }
        }
    }
}
