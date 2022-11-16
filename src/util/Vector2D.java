package util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Vector2D {
    private double x = 0;
    private double y = 0;

    public Vector2D() {}

    public Vector2D(double x, double y) {
        setX(x);
        setY(y);
    }

    public Vector2D(int direction, double magnitude) {
        setX(magnitude * Math.cos(direction)); // Vx = |V|.cos(~)
        setY(magnitude * Math.sin(direction)); // Vy = |V|.sin(~)
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y); // |V| = (x^2 + y^2)^1/2
    }

    public double getDirection() {
        double angle = Math.toDegrees(Math.atan2(y,x)) * -1;

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public void createVector(double magnitude, double angle) {
        setX(magnitude * Math.cos(Math.toRadians(angle))); // Vx = |V|.cos(angle)
        setY(magnitude * Math.sin(Math.toRadians(angle))); // Vy = |V|.sin(angle)
    }

    public void draw(double tailX, double tailY, Color color, double lineWidth, GraphicsContext gc) {
        gc.setStroke(color);
        gc.setLineWidth(lineWidth);
        gc.strokeLine(tailX, tailY, tailX + getX(), tailY + getY());
    }
}