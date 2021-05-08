package com.stuypulse.rocket.rocket.rockets;

import java.awt.Color;
import com.stuypulse.rocket.rocket.Rocket;
import com.stuypulse.stuylib.control.PIDController;

public class a extends Rocket {

    // constants
    private final int FLIP_HEIGHT;
    private final int LANDING_HEIGHT; 

    private final double FLIP_VY_AIM;
    private final double LANDING_VY_AIM;
    private final double TAKEOFF_VY_AIM;
    private final double FALLING_THRUST;

    private final int FALLING_A_CUTOFF;
    private final int FALLING_X_CUTOFF;

    /*
        P - proportion of I - acceleration
        I - error
        D - proportion of derivative (rate of change over time) to add
    */
    private double tP = 1.0;
    private double tI = 0;
    private double tD = 0.1;

    private double aP = 0.01;
    private double aI = 0;
    private double aD = 0.01;

    
    private double vAim = 30;
    private double aAim  = 0;
    private double thrustOut;
    private double angleOut;
    private boolean takenOff = false;
    private boolean doneFlipping = false;

    PIDController thrust = new PIDController(tP, tI, tD);
    PIDController angle  = new PIDController(aP, aI, aD);

    /**
     * Constructor
     * 
     * @param FLIP_HEIGHT      Height to start flipping.
     * @param LANDING_HEIGHT   Below this height, rocket will start landing.
     * @param FLIP_VY_AIM      Y velocity aim while flipping.
     * @param LANDING_VY_AIM   Y velocity aim while landing.
     * @param TAKEOFF_VY_AIM   Y velocity aim while taking off.
     * @param FALLING_THRUST   Thrust to set while falling after paramaters are not met.
     * @param FALLING_A_CUTOFF Above this angle (neg or pos), thrust downgrades to FALLING_THRUST.
     * @param FALLING_X_CUTOFF Above this x velocity, thrust downgrades to FALLING_THRUST.
     */
    public a(int FLIP_HEIGHT, int LANDING_HEIGHT, double FLIP_VY_AIM, double LANDING_VY_AIM,
              double TAKEOFF_VY_AIM, double FALLING_THRUST, int FALLING_A_CUTOFF, int FALLING_X_CUTOFF) {
        this.FLIP_HEIGHT = FLIP_HEIGHT;
        this.LANDING_HEIGHT = LANDING_HEIGHT;
        this.FLIP_VY_AIM = FLIP_VY_AIM;
        this.LANDING_VY_AIM = LANDING_VY_AIM;
        this.TAKEOFF_VY_AIM = TAKEOFF_VY_AIM;
        this.FALLING_THRUST = FALLING_THRUST;
        this.FALLING_A_CUTOFF = FALLING_A_CUTOFF;
        this.FALLING_X_CUTOFF = FALLING_X_CUTOFF;
    }
    public a() {
        this(110, 85, 30, -3, 30, 0.7, 180, 10);
    }

    public String getAuthor() {
        return "a";
    }

    protected void execute() {
        double y =  getState().getPosition().y;
        double vx = getState().getVelocity().x;
        double vy = getState().getVelocity().y;
        double a =  getState().getAngle().toDegrees();

        // landing
        if (y < LANDING_HEIGHT && takenOff) {
            aP = 0.01;
            doneFlipping = false;

            aAim = 0 + vx/2;
            vAim = LANDING_VY_AIM;

        // takeoff
        } else if (y < FLIP_HEIGHT) {
            aP = 0.05;
            aAim = 0;
            vAim = TAKEOFF_VY_AIM;

        // flip
        } else {
            takenOff = true;
            if (!doneFlipping) {
                aAim -= 2;
            } else {
                aP = 0.01;
            }
            if (Math.round(a) < -175) {
                aAim = 180 - Math.abs(aAim - a) * 1.25;
            }
            // prevents over-flipping
            if (Math.round(a) > 0 && Math.round(a) < 20) {
                doneFlipping = true;
                aAim = 0;
            }
            // falling/normal thrust
            if (Math.abs(a + getThrustAngle().toDegrees()) % 180 < FALLING_A_CUTOFF && Math.abs(vx) < FALLING_X_CUTOFF) {
                vAim = FLIP_VY_AIM;
            } else {
                // METHOD TO SPECIFY REAL THRUST OUTPUT
                vAim = vy + (-1 + FALLING_THRUST);
            }
        }


        // setting thrust and angle based on PID Controllers
        thrustOut = thrust.update((vAim + 1) - vy);
        angleOut  =  angle.update(aAim - a);
        
        if (thrustOut > 1)
            thrustOut = 1;
        if (thrustOut < 0)
            thrustOut = 0;
        if (angleOut > 1)
            angleOut = 1;
        if (angleOut < -1)
            angleOut = -1;

        setThrust(thrustOut);
        setThrustAngle(-angleOut);
    }

    public Color getColor() {
        return Color.BLACK;
    }

}
