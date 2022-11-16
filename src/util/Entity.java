package util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Entity {

    private double x;
    private double y;
    private double width = 0;
    private double height = 0;
    private double xOffset = 0;
    private double yOffset = 0;
    private double direction = 90;
    private Color color = Color.WHITE;

    private Vector2D velocity = new Vector2D();
    private Vector2D acceleration = new Vector2D();

    private boolean visible = true;

    public Entity() {}


    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Entity(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Entity(double x, double y, Color color, double xOffset, double yOffset) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public Entity(double x, double y, double width, double height, Color color, double xOffset, double yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(GraphicsContext gc);

    public abstract void tick(double timeElapsed);

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

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getxOffset() {
        return xOffset;
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public double getyOffset() {
        return yOffset;
    }

    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2D acceleration) {
        this.acceleration = acceleration;
    }

    public void applyVelocity(double timeElapsed) {
        setX(getX() + getVelocity().getX() * timeElapsed); // Rx = X0 + Vx * t;
        setY(getY() + getVelocity().getY() * timeElapsed); // Ry = Y0 + Vy * t;
    }

    public void applyAcceleration(double timeElapsed) {
        getVelocity().setX(getVelocity().getX() + getAcceleration().getX() * timeElapsed); // Vx = Vx0 + Ax * t
        getVelocity().setY(getVelocity().getY() + getAcceleration().getY() * timeElapsed); // Vy = Vy0 + Ay * t
    }

    public void applyForces(double timeElapsed) {
        applyVelocity(timeElapsed);
        applyAcceleration(timeElapsed);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void alignWith(Entity target) {
        setX(target.getX() + getxOffset());
        setY(target.getY() + getyOffset());
        setVelocity(target.getVelocity());
        setDirection(target.getDirection());
    }

    public void alignWith(double x, double y) {
        setX(x + getxOffset());
        setY(y + getyOffset());
    }
}