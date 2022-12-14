package world;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import design.ColorPalette;

public class MountainManager {
    private ArrayList<Double> mountainXPoints = new ArrayList<Double>();
    private ArrayList<Double> mountainYPoints = new ArrayList<Double>();

    private double xStep;
    private double maxShiftMagnitude;

    private double groundY;
    private double windowWidth;

    private ColorPalette palette;

    public MountainManager(double windowWidth, double xStep, double maxShiftMagnitude, double groundY, ColorPalette palette) {
        this.xStep = xStep;
        this.maxShiftMagnitude = maxShiftMagnitude;
        this.windowWidth = windowWidth;
        this.groundY = groundY;
        this.palette = palette;
        fillPoints(20);
    }

    public ArrayList<Double> getMountainXPoints() {
        return mountainXPoints;
    }

    public ArrayList<Double> getMountainYPoints() {
        return mountainYPoints;
    }

    public double getxStep() {
        return xStep;
    }

    public void setxStep(double xStep) {
        this.xStep = xStep;
    }

    public double getMaxShiftMagnitude() {
        return maxShiftMagnitude;
    }

    public void setMaxShiftMagnitude(double maxShiftMagnitude) {
        this.maxShiftMagnitude = maxShiftMagnitude;
    }

    private double getGroundY() {
        return groundY;
    }

    private double getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }

    private double getNextY(double lastY) {
        return (lastY - maxShiftMagnitude) + Math.random() * (2 * maxShiftMagnitude);
    }

    private ColorPalette getPalette() {
        return this.palette;
    }

    public void setPalette(ColorPalette palette) {
        this.palette = palette;
    }

    private void fillPoints(int initialGenSize) {
        for (int i = 0; i < initialGenSize; i++) {
            getMountainXPoints().add(i * xStep);
        }

        double baseY = getGroundY() - 300;

        for (int j = 0; j < initialGenSize; j++) {
            double lastY = j == 0 ? baseY : getMountainYPoints().get(j - 1);

            double nextY = getNextY(lastY);

            if (nextY > baseY) {
                nextY = baseY;
            }

            getMountainYPoints().add(nextY);
        }
    }

    private void fillViewingWindow(double canvasLeftX, double margin) {
        if (getMountainXPoints().size() == 0 || getMountainYPoints().size() == 0) {
            System.out.println("still loading mountains");
        } else {
            if (getMountainXPoints().get(0) > canvasLeftX - margin) {
                double leftMountainX = getMountainXPoints().get(0);
                getMountainXPoints().add(0, leftMountainX - xStep);

                double leftMountainY = getMountainYPoints().get(0);
                getMountainYPoints().add(0, getNextY(leftMountainY));
            }

            double canvasRightX = canvasLeftX + getWindowWidth();
            if (getMountainXPoints().get(getMountainXPoints().size() - 1) < canvasRightX + margin) {
                double rightMountainX = getMountainXPoints().get(getMountainXPoints().size() - 1);
                double rightMountainY = getMountainYPoints().get(getMountainYPoints().size() - 1);

                getMountainXPoints().add(rightMountainX + xStep);
                getMountainYPoints().add(getNextY(rightMountainY));
            }
        }
    }

    public void draw(GraphicsContext gc) {
        fillViewingWindow(-gc.getTransform().getTx(), 500);
        gc.setFill(getPalette().getMountainColor());

        for (int i = 0; i < getMountainXPoints().size() - 1; i++) {
            gc.fillPolygon(
                new double [] {
                    getMountainXPoints().get(i),
                    getMountainXPoints().get(i),
                    getMountainXPoints().get(i+1) + 1,
                    getMountainXPoints().get(i+1) + 1,
                    getMountainXPoints().get(i),
                },
                new double [] {
                    getMountainYPoints().get(i),
                    getMountainYPoints().get(i),
                    getMountainYPoints().get(i+1),
                    groundY,
                    groundY
                },
                5
            );
            gc.setStroke(getPalette().getMountainColor().darker());
            gc.setLineWidth(3);
            gc.strokeLine(getMountainXPoints().get(i), getMountainYPoints().get(i),
                            getMountainXPoints().get(i+1), getMountainYPoints().get(i+1));
        }
    }
}