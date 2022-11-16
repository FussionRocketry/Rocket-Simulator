package rocket;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uitl.Entity;

public class Particle extends Entity {
    private double radius;

    private double initialLifetime = 5;
    private double lifetime = initialLifetime;

    private double groundY;

    public Particle() {}

    public Particle(double x, double y, Color color, double groundY) {
        super(x,y,color);
        this.groundY = groundY;
    }

    public Particle(double x, double y, double radius, Color color,
                    double minAngle, double maxAngle, double rocketSpeed,
                    double initialSpeed, double groundY) {
                        super(x, y, color);
                        this.radius = radius;
                        double angle = getRandomAngle(minAngle, maxAngle);
                        getVelocity().createVector(rocketSpeed + initialSpeed, angle);
                        getVelocity().setX(getVelocity().getX() * -1);
                        this.groundY = groundY;
    }

    public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getInitialLifetime() {
		return initialLifetime;
	}

	public void setInitialLifetime(double initialLifetime) {
		this.initialLifetime = initialLifetime;
	}

	public double getLifetime() {
		return lifetime;
	}

	public void setLifetime(double lifetime) {
		this.lifetime = lifetime;
	}

	public void decreaseLifetime(double timeElapsed) {

		if (getLifetime() - timeElapsed <= 0) {

			setLifetime(0);

		} else {
			setLifetime(getLifetime() - timeElapsed);
		}

	}

	public void fade() {
		double newOpacity = (getLifetime() / getInitialLifetime()) *
				getColor().getOpacity();

		setColor(new Color(getColor().getRed(), getColor().getGreen(),
				getColor().getBlue(), newOpacity));
	}


	public static double getRandomAngle(double minAngle, double maxAngle) {
		return Math.random() * (maxAngle - minAngle) + minAngle;
	}

    public void bounceoffGround(double timeElapsed) {
        double yDistanceToBeTraveled = getVelocity().getY() * timeElapsed;

        if (getY() >= groundY || getY() + yDistanceToBeTraveled >= groundY) {
            setY(groundY);
            getVelocity().setY(getVelocity().getY() * -1);
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(getColor());
        gc.fillOval(getX() - getRadius(), getY(), getRadius() * 2, getRadius() * 2);
    }

    @Override
    public void tick(double timeElapsed) {
        fade();
        setLifetime(getLifetime() - timeElapsed);
        bounceoffGround(timeElapsed);
        applyForces(timeElapsed);
    }
}