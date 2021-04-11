package graphics;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class TriangleTile extends Polygon {
    public static final double R = 30;
    public static final double HEIGHT = R * 3;
    public static final double SIDE = 6 * R / Math.sqrt(3);

    public TriangleTile(double x, double y, boolean upwards, Color color) {
        if (upwards) {
            getPoints().addAll(
                    x, y,
                    x - SIDE / 2, y + HEIGHT,
                    x + SIDE / 2, y + HEIGHT
            );
        } else {
            getPoints().addAll(
                    x, y,
                    x + SIDE, y,
                    x + SIDE / 2, y + HEIGHT
            );
        }

        setFill(color);
        setStrokeWidth(1);
        setStroke(Color.BLACK);
    }
}
