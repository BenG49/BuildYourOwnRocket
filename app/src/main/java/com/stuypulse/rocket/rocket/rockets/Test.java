package com.stuypulse.rocket.rocket.rockets;

import java.awt.Color;
import com.stuypulse.rocket.rocket.Rocket;
import com.stuypulse.rocket.rocket.RockController;

public class Test extends Rocket {

    // RockController r = new RockController();
    private int aAim = -5;
    private int vAim = 10;


    public Test() {}

    public String getAuthor() {
        return "";
    }

    protected void execute() {
        double x = getState().getPosition().x;
        double y = getState().getPosition().y;
        double vx = getState().getVelocity().x;
        double vy = getState().getVelocity().y;
        double a = getState().getAngle().toDegrees();

        // if (y > 200)// && aAim <= 90)
        //     aAim++;

        // double[] c = RockController.getCorrection(aAim, vAim, this, 0.7F);
        // setThrust(c[0]);
        // setThrustAngle(c[1]);

        // System.out.printf("velocity: %d\tvelocity aim: %d\tthrust: %f\tangle difference: %f\n", Math.round(vy), vAim, c[0], Math.abs(aAim-a));
    }

    public Color getColor() {
        return Color.ORANGE;
    }
}