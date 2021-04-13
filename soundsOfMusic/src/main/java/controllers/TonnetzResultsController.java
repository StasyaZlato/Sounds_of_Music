package controllers;

import graphics.TriangleTile;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pojo.CompositionChord;

import java.util.List;

public class TonnetzResultsController {
    private static List<CompositionChord> chords = FXCollections.observableArrayList();

    public static SimpleBooleanProperty watcher = new SimpleBooleanProperty();

    private static final int rows = 3;
    private static final int tilesPerRow = 8;

    private static final String[] tonnetzChords = new String[] {"F#", "A#m", "C#", "Fm", "G#", "Cm", "D#", "Gm",
                                                    "A#", "Dm", "F", "Am", "C", "Em", "G", "Bm",
                                                    "D", "F#m", "A", "C#m", "E", "G#m", "B", "D#m"};

    private static final Color baseColor = Color.RED;

    private static final double baseRed = baseColor.getRed();
    private static final double baseGreen = baseColor.getGreen();
    private static final double baseBlue = baseColor.getBlue();
    public Label description;

    private TriangleTile[] tiles = new TriangleTile[tonnetzChords.length];

    @FXML
    AnchorPane tonnetz;

    public void initialize() {
        description.setWrapText(true);
        watcher.addListener((obs, oldVal, newVal) -> {
            colorTonnetz();
        });
        drawTonnetzTemplate();
    }

    private void drawTonnetzTemplate() {
        int xStartOffset = 15;
        int yStartOffset = 150;
        boolean upwards = false;
        int count = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < tilesPerRow; x++) {
                double xCoord = x * TriangleTile.SIDE / 2 + xStartOffset + y * TriangleTile.SIDE / 2;
                double yCoord = y * TriangleTile.HEIGHT + yStartOffset;

                TriangleTile tile = new TriangleTile(xCoord, yCoord, upwards, Color.ANTIQUEWHITE);
                upwards = !upwards;
                tiles[count] = tile;
                ++count;
            }
        }
        setTiles();
    }

    private void setTiles() {
        int xStartOffset = 15;
        int yStartOffset = 150;
        int count = 0;
        tonnetz.getChildren().clear();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < tilesPerRow; x++) {
                double xCoord = x * TriangleTile.SIDE / 2 + xStartOffset + y * TriangleTile.SIDE / 2;
                double yCoord = y * TriangleTile.HEIGHT + yStartOffset;

                final StackPane stack = new StackPane();
                final Text text = new Text(tonnetzChords[count]);
                stack.getChildren().addAll(tiles[count], text);
                ++count;
                stack.setLayoutX(xCoord);
                stack.setLayoutY(yCoord);
                tonnetz.getChildren().add(stack);
            }
        }
    }

    private void colorTonnetz() {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < tonnetzChords.length; ++i) {
            final int index = i;
            CompositionChord currChord = chords
                                        .stream()
                                        .filter(c -> c.chord.equals(tonnetzChords[index].toLowerCase()))
                                        .findFirst().orElse(null);
            if (currChord != null) {
                double opacity = currChord.frequency * 3;
                if (opacity > 1) {
                    opacity = 1;
                }
                tiles[i].setFill(Color.color(baseRed, baseGreen, baseBlue, opacity));
            }
            else {
                tiles[i].setFill(Color.WHITE);
            }
        }
        setTiles();
    }

    public static void setChords(List<CompositionChord> data) {
        chords = data;
    }
}