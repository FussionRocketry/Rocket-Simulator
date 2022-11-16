package rocket;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Rotate;
import util.Entity;


public class RocketEngine extends Entity {
    private double width = 15;
    private double height = 10;
    private boolean on;
    private double fuelBurnRate = 1;
    private double thrustPower = 200;

    private ParticleEmitter emitter;

    public RocketEngine(double groundY) {
        super();
        setColor(Color.GRAY);
        this.emitter = new ParticleEmitter(groundY, new Color[] {Color.RED, Color.ORANGE},
                                            0, getHeight());
    }

    public RocketEngine(double groundY, double width, double height, double xOffset, double yOffset) {
        super();
        setColor(Color.GRAY);
        this.emitter = new ParticleEmitter(groundY, new Color[] {Color.RED, Color.ORANGE},
                                            0, getHeight());
        this.width = width;
        this.height = height;
        setxOffset(xOffset);
        setyOffset(yOffset);
    }


    public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getFuelBurnRate() {
		return fuelBurnRate;
	}

	public void setFuelBurnRate(double fuelBurnRate) {
		this.fuelBurnRate = fuelBurnRate;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public double getThrustPower() {
		return thrustPower;
	}

	public void setThrustPower(double thrustPower) {
		this.thrustPower = thrustPower;
	}

	public ParticleEmitter getEmitter() {
		return emitter;
	}

	public void setEmitter(ParticleEmitter emitter) {
		this.emitter = emitter;
	}

    @Override
    public void draw(GraphicsContext gc) {
        emitter.draw(gc);

        gc.setFill(getColor());
        gc.fillArc(getX() - getWidth() / 2, getY(), getWidth(), getHeight() * 2, 0, 180, ArcType.ROUND);
    }

    public void draw(GraphicsContext gc, double angle) {
        emitter.draw(gc);

        gc.setFill(getColor());
        gc.fillArc(getX() - getWidth() / 2, getY(), getWidth(), getHeight() * 2, 0, 180, ArcType.ROUND);
        gc.rotate(angle);
    }

    @Override
    public void tick(double timeElapsed) {
        getEmitter().alignWith(this);
        getEmitter().setOn(this.isOn());
        getEmitter().tick(timeElapsed);
    }
}