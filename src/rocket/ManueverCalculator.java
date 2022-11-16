package rocket;

import world.World;

public class ManueverCalculator {
    private Rocket rocket;
    private double groundY;

    public ManueverCalculator() {}

    public ManueverCalculator(Rocket rocket, double groundY) {
        this.rocket = rocket;
        this.groundY = groundY;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }

    public double getGroundY() {
        return groundY;
    }

    public void setGroundY(double groundY) {
        this.groundY = groundY;
    }

    public boolean shouldBurn(double targetHeight) {
        boolean crossedBurnHeight = calculateBurnHeight(targetHeight) >= calculateAltitude();
        double yVelocityThreshold = 20;

        if (crossedBurnHeight && !(getRocket().getVelocity().getY() <= yVelocityThreshold)) {
            return true;
        } else {
            return false;
        }
    }

    public double calculateAltitude() {
        return getGroundY() - (rocket.getY() + rocket.getHeight());
    }

    public double calculateTotalThrustPower() {
        double sum = 0;

        for (RocketEngine engine : rocket.getEngines()) {
            sum += engine.getThrustPower();
        }
        return sum;
    }

    public double calculateBurnHeight(double safetyMargin) {
        double thrustYAccel = (calculateTotalThrustPower() * Math.sin(Math.toRadians(rocket.getDirection()))) - World.GRAVITY;

        if (thrustYAccel < 0) {
            return 0;
        } else {
            double timeToCounterVelocity = Math.abs(
                rocket.getVelocity().getY() / thrustYAccel);

            double burnYTravel = ((-0.5) * thrustYAccel * (Math.pow(timeToCounterVelocity, 2))) + rocket.getVelocity().getY() * timeToCounterVelocity;

            return burnYTravel + safetyMargin;
        }
    }
}