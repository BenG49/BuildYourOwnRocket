package com.stuypulse.rocket.rocket.rockets;

import com.stuypulse.rocket.rocket.Rocket;
import com.stuypulse.rocket.rocket.RockController;
import java.awt.Color;

public class Flip extends Rocket {

    private int aAim = 0;
    private int vAim = 20;
    private int counter = 0;
    private boolean doneFlipping = false;

    // constants
    private final int   HEIGHT;
    private final float HEIGHT_MOD;
    private final float DEAD_THRUST;
    private final int   LANDING_V_AIM;
    private final int   TAKEOFF_V_AIM;
    private final int   FLIP_V_AIM;
    private final int   MAX_DOWN_V;

    /**
     * Constructor
     * 
     * @param HEIGHT        height to start flipping (not accurate, add 100 for delay)
     * @param HEIGHT_MOD    height modifier to start landing sequence
     * @param DEAD_THRUST   thrust to use when flipping/facing downwards
     * @param LANDING_V_AIM y velocity to aim for when landing
     * @param TAKEOFF_V_AIM y velocity to aim for when taking off
     * @param FLIP_V_AIM    y velocity to aim for when flipping
     * @param MAX_DOWN_V    if falling faster than this velocity, will use lower thrust
     */
    public Flip(int HEIGHT, float HEIGHT_MOD, float DEAD_THRUST, int LANDING_V_AIM, int TAKEOFF_V_AIM, int FLIP_V_AIM, int MAX_DOWN_V) {
        this.HEIGHT = HEIGHT;
        this.HEIGHT_MOD = HEIGHT_MOD;
        this.DEAD_THRUST = DEAD_THRUST;
        this.LANDING_V_AIM = LANDING_V_AIM;
        this.TAKEOFF_V_AIM = TAKEOFF_V_AIM;
        this.FLIP_V_AIM = FLIP_V_AIM;
        this.MAX_DOWN_V = MAX_DOWN_V;
    }
    public Flip() {
        this(65, 1.2F, 0.7F, -3, 30, 35, 40);
    }

    public String getAuthor() {
        return("");
    }

    public Color getColor() {
        return Color.DARK_GRAY;
    }

    protected void execute() {
        double x  = getState().getPosition().x;
        double y  = getState().getPosition().y;
        double vx = getState().getVelocity().x;
        double vy = getState().getVelocity().y;
        double a  = getState().getAngle().toDegrees();

        counter++;

        // basically steadyboi with 20 vAim takeoff
        // if (y > 82) {
            // vAim = 0;
        // }

        // -- flip -- //
        // landing
        if (y < HEIGHT * HEIGHT_MOD && counter > 500 && vy < 5) {
            aAim = 0;
            // cutoff point for overcompensating, average of vx and vy
            if (y > 25) {
                // aAim += vx/1.5;
                aAim += vx/1.5;
            } else if (a > 10 || a < -10) {
                if (a > 0)
                    aAim -= 20;
                if (a < 0)
                    aAim += 20;
            }
                
            vAim = LANDING_V_AIM;
            if (y < 5 && a < -5 && a > 5)
                vAim = 10;
            doneFlipping = false;
        // takeoff
        } else if (y < HEIGHT) {
            aAim = 0;
            vAim = TAKEOFF_V_AIM;
        // flip
        } else {
            if (!doneFlipping)
                aAim--;
            if (Math.round(a) == -179)
                aAim = 155;
            // prevents over-flipping
            if (Math.round(a) > 0 && Math.round(a) < 20) {
                doneFlipping = true;
                aAim = 0;
            }
            vAim = FLIP_V_AIM;
        }

        double thrust = RockController.getCorrection(aAim, vAim, this, DEAD_THRUST, MAX_DOWN_V)[0];
        double angle  = RockController.getCorrection(aAim, vAim, this, DEAD_THRUST, MAX_DOWN_V)[1];

        setThrust(thrust);
        setThrustAngle(angle);
    }
}