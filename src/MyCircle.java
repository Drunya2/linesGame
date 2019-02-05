import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by Andrey on 05.02.2019.
 */
public class MyCircle extends Circle {
    private int i;
    private int j;
    private Color color;

    public MyCircle(double centerX, double centerY, double radius, int i, int j, Color color) {
        super(centerX, centerY, radius);
        this.i = i;
        this.j = j;
        this.color = color;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public Color getColor() {
        return color;
    }
}
