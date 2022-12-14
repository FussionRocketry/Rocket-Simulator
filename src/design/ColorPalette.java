package design;

import javafx.scene.paint.Color;

public class ColorPalette {
    private String name;
    private Color skyColor;
    private Color groundColor;
    private Color mountainColor;

    public ColorPalette(String name, Color skyColor, Color groundColor, Color mountainColor) {
        this.name = name;
        this.skyColor = skyColor;
        this.groundColor = groundColor;
        this.mountainColor = mountainColor;
    }

    public static final ColorPalette EARTH = new ColorPalette("Earth", Color.DEEPSKYBLUE, Color.DARKGREEN, Color.SADDLEBROWN);

    public static final ColorPalette MARS = new ColorPalette("Mars", Color.SALMON, Color.DARKORANGE, Color.BROWN);

    public static final ColorPalette NIGHT = new ColorPalette("Night", Color.DARKSLATEGRAY, Color.DARKGREEN, Color.SADDLEBROWN);

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getSkyColor() {
        return this.skyColor;
    }

    public void setSkyColor(Color skyColor) {
        this.skyColor = skyColor;
    }

    public Color getGroundColor() {
        return this.groundColor;
    }

    public void setGroundColor(Color groundColor) {
        this.groundColor = groundColor;
    }

    public Color getMountainColor() {
        return this.mountainColor;
    }

    public void setMountainColor(Color mountainColor) {
        this.mountainColor = mountainColor;
    }

    @Override
    public String toString() {
        return name;
    }
}