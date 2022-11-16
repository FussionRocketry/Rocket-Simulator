package world;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import util.Entity;
import rocket.Rocket;
import rocket.UserControlledRocket;
import design.ColorPalette;


public class World {
    private ArrayList<Entity> objects = new ArrayList<Entity>();

    public static final double GRAVITY = 100;
    private double groundHeight = 100;
    private double groundY;

    private double windowWidth;
    private double windowHeight;

    private boolean centerOnRocketHorizontally = false;
    private boolean centerOnRocketVertically = false;

    private MountainManager mountainManager;

    private Rocket primaryRocket;

    private ColorPalette palette;

    World() {}

    public World(double windowWidth, double windowHeight, ColorPalette palette) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.groundY = windowHeight - getGroundHeight();
        this.mountainManager = new MountainManager(
            windowWidth, 100, 100, groundY, palette
        );
        this.palette = palette;
    }

    private double getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }

    public ArrayList<Entity> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Entity> objects) {
        this.objects = objects;
    }

    private MountainManager getMountainManager() {
        return mountainManager;
    }

    public void setMountainManager(MountainManager mountainManager) {
        this.mountainManager = mountainManager;
    }

    public double getGroundY() {
        return groundY;
    }

    public void setGroundY(double groundY) {
        this.groundY = groundY;
    }

    public double getGroundHeight() {
        return groundHeight;
    }

    public void setGroundHeight(double groundHeight) {
        this.groundHeight = groundHeight;
    }

    public boolean centerOnRocketHorizontally() {
        return centerOnRocketHorizontally;
    }

    public void setCenterOnRocketHorizontally(boolean centerOnRocketHorizontally) {
        this.centerOnRocketHorizontally = centerOnRocketHorizontally;
    }

    public boolean centerOnRocketVertically() {
        return centerOnRocketVertically;
    }

    public void setCenterOnRocketVertically(boolean centerOnRocketVertically) {
        this.centerOnRocketVertically = centerOnRocketVertically;
    }

    public boolean rocketTouchingGround(Rocket rocket) {
        return rocket.getY() + rocket.getHeight() >= getGroundY();
    }

    public Rocket getPrimaryRocket() {
        return this.primaryRocket;
    }

    public void setPrimaryRocket(Rocket primaryRocket) {
        this.primaryRocketü = primaryRocket;
    }

    private ColorPalette getPalette() {
        return this.palette;
    }

    public void setPalette(ColorPalette palette) {
        this.palette = palette;
        getMountainManager().setPalette(palette);
    }

    public void tick(double timeElapsed) {
        if (rocketTouchingGround(getPrimaryRocket())) {
            getPrimaryRocket().stop();
        }

        for (Entity entity: getObjects()) {
            entity.tick(timeElapsed);
        }
    }

    public void drawSky(GraphicsContext gc) {
        gc.setFill(getPalette().getSkyColor());

        double leftX = -gc.getTransform().getTx();
        double topY = -gc.getTransform().getTy();

        gc.fillRect(leftX, topY, getWindowWidth(), getWindowHeight());
    }

    public void drawGround(GraphicsContext gc) {
        gc.setFill(getPalette().getGroundColor());
        double leftX = -gc.getTransform().getTx();
        double topY = getWindowHeight() - getGroundHeight();

        double height = topY + getGroundHeight() - gc.getTransform().getTy();
        gc.fillRect(leftX, topY, getWindowWidth(), height);
    }

    public void centerOnRocketHorizontally(GraphicsContext gc, Rocket center) {
        double xTranslate = centerOnRocketHorizontally() ? -center.getX() - gc.getTransform().getTx() + getWindowWidth() / 2 : 0;
        gc.translate(xTranslate, 0);
    }

    public void centerOnRocketVertically(GraphicsContext gc, Rocket center) {
        double yTranslate = centerOnRocketVertically() ? -center.getY() + center.getHeight() / 2
        -gc.getTransform().getTy() + getWindowHeight() / 2 : 0;

        gc.translate(0, yTranslate);
    }

    public void alignGraphicsContext(GraphicsContext gc) {
        if (centerOnRocketHorizontally()) {
            centerOnRocketHorizontally(gc, getPrimaryRocket());
        }

        if (centerOnRocketVertically()) {
            centerOnRocketVertically(gc, getPrimaryRocket());
        }
    }

    public void draw(GraphicsContext gc) {
        alignGraphicsContext(gc);

        drawSky(gc);

        getMountainManager().draw(gc);

        for (Entity entity : getObjects()) {
            entity.draw(gc);
        }

        drawGround(gc);
    }
}