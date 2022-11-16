package rocket;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import world.World;
import util.Entity;


public class Rocket extends Entity {

    // Physics Variables
    private double width = 40;
    private double centerTankWidth = width / 2.5;
    private double height = 100;
    private double noseConeHeight = 30;
    private double engineConeHeight = 10;
    private double centerTankHeight = height - noseConeHeight - engineConeHeight;
    private double finHeight = 20;
    private double engineConeWidth = centerTankWidth;
    //private double turnRate = 60; // degress per second
    private double turnRate = 40; // degress per second

    // Flight attributes
    private boolean airborne = true;
    private double fuel;
    private RocketEngine[] engines;
    private ParticleEmitter[] rcsThrusters;

    private ManueverCalculator manueverCalculator;

    private double landingAngleMargin = 10;
    private double acceptableLandingVelocity = 100;
    private double landingVelocity;

    Rocket() {}

    public Rocket(double x, double y, double fuel, double groundY) {
        super(x, y);
        this.fuel = fuel;
        this.manueverCalculator = new ManueverCalculator(this, groundY);
        this.engines = new RocketEngine[] {
            new RocketEngine(groundY, getEngineConeWidth(), getEngineConeHeight(),
                            0, getHeight() - getEngineConeHeight())
        };

        double rcsYoffset = getNoseConeHeight() + getCenterTankHeight() / 2 - getFinHeight();
        double rcsXOffset = getCenterTankWidth() / 2 + 4 / 2;
        this.rcsThrusters = new ParticleEmitter[] {
            new ParticleEmitter(4, 8, groundY,
                        new Color[] {Color.WHITE}, -rcsXOffset, rcsYoffset, -90, Color.RED),
            new ParticleEmitter(4, 8, groundY,
                        new Color[] {Color.WHITE}, rcsXOffset, rcsYoffset, 90, Color.RED)
        };
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

	public double getCenterTankWidth() {
		return centerTankWidth;
	}

	public void setCenterTankWidth(double centerTankWidth) {
		this.centerTankWidth = centerTankWidth;
	}

	public double getCenterTankHeight() {
		return centerTankHeight;
	}

	public void setCenterTankHeight(double centerTankHeight) {
		this.centerTankHeight = centerTankHeight;
	}

	public double getFinHeight() {
		return finHeight;
	}

	public void setFinHeight(double finHeight) {
		this.finHeight = finHeight;
	}

	public double getNoseConeHeight() {
		return noseConeHeight;
	}

	public void setNoseConeHeight(double noseConeHeight) {
		this.noseConeHeight = noseConeHeight;
	}

	public double getEngineConeWidth() {
		return engineConeWidth;
	}

	public void setEngineConeWidth(double engineConeWidth) {
		this.engineConeWidth = engineConeWidth;
	}

	public double getEngineConeHeight() {
		return engineConeHeight;
	}

	public void setEngineConeHeight(double engineConeHeight) {
		this.engineConeHeight = engineConeHeight;
	}

	public double getTurnRate() {
		return turnRate;
	}

	public void setTurnRate(double turnRate) {
		this.turnRate = turnRate;
	}

	public double getFuel() {
		return fuel;
	}

	public void setFuel(double fuel) {
		this.fuel = fuel;
	}

	public RocketEngine[] getEngines() {

		return engines;
	}

	public void setEngines(RocketEngine[] engines) {
		this.engines = engines;
	}

	public ParticleEmitter[] getRCSThrusters() {
		return rcsThrusters;
	}

	public void setRCSThrusters(ParticleEmitter[] rcsThrusters) {
		this.rcsThrusters = rcsThrusters;
	}

    public boolean isAirborne() {
		return airborne;
	}

	public void setAirborne(boolean airborne) {
		this.airborne = airborne;
	}

	public double getLandingAngleMargin() {
		return landingAngleMargin;
	}

	public void setLandingAngleMargin(double landingAngleMargin) {
		this.landingAngleMargin = landingAngleMargin;
	}

	public double getAcceptableLandingVelocity() {
		return acceptableLandingVelocity;
	}

	public void setAcceptableLandingVelocity(double acceptableLandingVelocity) {
		this.acceptableLandingVelocity = acceptableLandingVelocity;
	}

	public ManeuverCalculator getManeuverCalculator() {
		return maneuverCalculator;
	}

	public void setManeuverCalculator(ManeuverCalculator maneuverCalculator) {
		this.maneuverCalculator = maneuverCalculator;
	}

	public double getLandingVelocity() {
		return this.landingVelocity;
	}

	public void setLandingVelocity(double landingVelocity) {
		this.landingVelocity = landingVelocity;
	}

    public void applyGravity(double timeElapsed) {
        getVelocity().setY(getVelocity().getY() + World.GRAVITY * timeElapsed);
    }

    public void applyThrust(double timeElapsed) {
        if (getFuel() > 0) {
            for (RocketEngine engine : getEngines()) {
                if (engine.isOn()) {
                    getVelocity().setX(getVelocity().getX() + Math.cos(Math.toRadians(getDirection())) * engine.getThrustPower() * timeElapsed);

                    getVelocity().setY(getVelocity().getY() + Math.sin(Math.toRadians(getDirection())) * -1 * engine.getThrustPower() * timeElapsed);

                    setFuel(getFuel() -
                        engine.getFuelBurnRate() * timeElapsed);
                }
            }
        } else {
            setEnginesOn(false);
        }
    }

    protected void pointInDirection(double targetAngle, double timeElapsed) {
        double distanceToTargetAngle = targetAngle - getDirection();

        if (distanceToTargetAngle < 0 && getVelocity().getY() > 0) {
            getRCSThrusters()[0].setOn(true);
            getRCSThrusters()[1].setOn(true);
        } else if (distanceToTargetAngle != 0 && getVelocity().getY() > 0) {
            getRCSThrusters()[0].setOn(false);
            getRCSThrusters()[1].setOn(true);
        }

        if (Math.abs(distanceToTargetAngle) <= getTurnRate() * timeElapsed) {
            setDirection(targetAngle);
        } else if(getVelocity().getY() > 0) {
            if (distanceToTargetAngle < 0) {
                // left
                setDirection(getDirection() - getTurnRate() * timeElapsed);
            } else {
                // right
                setDirection(getDirection() + getTurnRate() * timeElapsed);
            }
        }
    }

    public void setEnginesOn(boolean state) {
        for (RocketEngine engine : getEngines()) {
            engine.setOn(state);
        }
    }

    public void stop() {
        if (isAirborne()) {
            setAirborne(false);
            setEnginesOn(false);

            for (ParticleEmitter rcsThrusters : getRCSThrusters()) {
                rcsThrusters.setOn(false);
            }

            setLandingVelocity(getVelocity().getMagnitude());

            if (
                getVelocity().getMagnitude() < getAcceptableLandingVelocity() &&
                Math.abs(getDirection() - 90) <= getLandingAngleMargin()
            ) {
                setDirection(90);
            }

            getVelocity().setX(0);
            getVelocity().setY(0);
        }
    }

    @Override
    public void tick(double timeElapsed) {
        if (isAirborne()) {
            double safetyMargin = 5;
            setEnginesOn(getManeuverCalculator().shouldBurn(safetyMargin));

            pointInDirection(getVelocity().getDirection() - 180, timeElapsed);

            applyThrust(timeElapsed);
            applyForces(timeElapsed);
        }

        for (RocketEngine engine : getEngines()) {
            engine.tick(timeElapsed);
        }

        for (ParticleEmitter rcsThrusters : getRCSThrusters()) {
            rcsThrusters.tick(timeElapsed);
        }
    }

    public void rotateGraphicsContext(GraphicsContext gc) {
        double pivotX = getX();
        double pivotY = getY() + (getHeight() / 2.0);
        Rotate rotate = new Rotate(90 - getDirection(), pivotX, pivotY);
        gc.transform(new Affine(rotate));
    }

    public void drawFins(GraphicsContext gc) {
        double[] fin1xPoints = new double[] {
            getX() - getCenterTankWidth() / 2,
            getX() - getCenterTankWidth() / 2,
            getX() - getWidth() / 2
        };

        double fin2xPoints = new double[] {
            getX() + getCenterTankWidth() / 2,
            getX() + getCenterTankWidth() / 2,
            getX() + getWidth() / 2
        };

        double finStartY = getY() + getNoseConeHeight() + getCenterTankHeight() - getFinHeight();
        double[] finyPoints = new double[] {
            finStartY,
            finStartY + getFinHeight(),
            finStartY + getFinHeight()
        };
        gc.setFill(Color.BLUE);
        gc.fillPolygon(fin1xPoints, finyPoints, finyPoints.length);
        gc.fillPolygon(fin2xPoints, finyPoints, finyPoints.length);
    }

    @Override
    public void draw(GraphicsContext gc){
        gc.save();

        rotateGraphicsContext(gc);

        for (RocketEngine engine : getEngines()) {
            engine.alignWith(this);
            engine.draw(gc);
        }

        for (ParticleEmitter thruster : getRCSThrusters()) {
            thruster.alignWith(this);
            thruster.draw(gc);
        }

        gc.setFill(getColor());

        gc.fillArc(getX() - getCenterTankWidth() / 2, getY(),
                    getCenterTankWidth(), getNoseConeHeight() * 2, 0, 180, ArcType.ROUND);


        gc.fillRect(getX() - getCenterTankWidth() / 2, getY() + getNoseConeHeight(), getCenterTankWidth(), getCenterTankHeight());

        drawFins(gc);

        gc.restore();
    }
}