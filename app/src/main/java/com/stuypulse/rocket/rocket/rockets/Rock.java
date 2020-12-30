package com.stuypulse.rocket.rocket.rockets;

import com.stuypulse.rocket.rocket.Rocket;
import java.awt.Color;

public class Rock extends Rocket {

    private double angle;
    private double thrust;

    // height to start flipping and cut thrust
    private final int HEIGHT;
    // max down velocity to use TURNING_THRUST instead of TURNING_FALLING_THRUST
    private final int MAX_DOWN_V;

    //---ANGLE CORRECTION---//
    // angle bounds where rocket will try to reach a=0
    private final int CORRECTION_ANGLE_BOUNDS;
    // thrust angle to set when correcting/landing - RELATES TO AMOUNT OF DOWNWARDS LANDING THRUST
    private final double CORRECTION_ANGLE;
    // multiplier for height to start angle correction (default 1.7)
    private final float CORRECTION_ANGLE_MULT;

    //---TURNING THRUST---//
    // thrust while turning and not falling
    private final double TURNING_THRUST;
    // thrust while turning and falling
    private final double TURNING_FALLING_THRUST;

    //---LANDING---//
    // max downwards velocity to start lower burn
    private final int MAX_DOWN_V_LANDING;
    // angle bounds to start lower burn
    private final int LANDING_ANGLE_BOUNDS;
    // landing thrust (default 0.47)
    private final double LANDING_THRUST;
    // landing height
    private final int LANDING_HEIGHT_DIVISOR;

    public Rock(int HEIGHT, int MAX_DOWN_V, int CORRECTION_ANGLE_BOUNDS, double CORRECTION_ANGLE, float CORRECTION_ANGLE_MULT, double TURNING_THRUST,
                double TURNING_FALLING_THRUST, int MAX_DOWN_V_LANDING, int LANDING_ANGLE_BOUNDS, double LANDING_THRUST, int LANDING_HEIGHT_DIVISOR) {
        this.HEIGHT = HEIGHT;
        this.MAX_DOWN_V = MAX_DOWN_V;

        this.CORRECTION_ANGLE_BOUNDS = CORRECTION_ANGLE_BOUNDS;
        this.CORRECTION_ANGLE = CORRECTION_ANGLE;
        this.CORRECTION_ANGLE_MULT = CORRECTION_ANGLE_MULT;

        this.TURNING_THRUST = TURNING_THRUST;
        this.TURNING_FALLING_THRUST = TURNING_FALLING_THRUST;

        this.MAX_DOWN_V_LANDING = MAX_DOWN_V_LANDING;
        this.LANDING_ANGLE_BOUNDS = LANDING_ANGLE_BOUNDS;
        this.LANDING_THRUST = LANDING_THRUST;
        this.LANDING_HEIGHT_DIVISOR = LANDING_HEIGHT_DIVISOR;
    }

    public Rock() {
        // this(130, 37, 60, 0.02, 1.7F, 1.0, 0.6, 6, 15, 0.5, 1);

        this(130, 37,        // height, max downwards V
             60, 0.02, 1.7F, // angle correction bounds, angle, height multiplier
             1.0, 0.6,       // turning thrust, falling thrust
             6, 15, 0.5, 1); // landing max V, angle bounds, thrust, height divisor    */
    }

    public String getAuthor() {
        return "Ben";
    }

    public Color getColor() {
        return Color.BLUE;
    }

    protected void execute() {
        //double x = getState().getPosition().x;
        double y = getState().getPosition().y;
        double vy = getState().getVelocity().y;
        double vx = getState().getVelocity().x;
        double a = getState().getAngle().toDegrees();


        //---THRUST----//

        // <--if below threshold-->
        if (y < HEIGHT) {

            /* <--thrust shutoff in order to land-->
             * y velocity low and neg   AND
             * angle in landing bounds  AND
             * y value is low
             */
            if (vy < 0 && vy > -MAX_DOWN_V_LANDING && a > -LANDING_ANGLE_BOUNDS && a < LANDING_ANGLE_BOUNDS && y < HEIGHT/LANDING_HEIGHT_DIVISOR)
                thrust = LANDING_THRUST;

            /* <--liftoff & landing burn-->
             * liftoff:
             *      angle is 0  AND (liftoff #2 is not exact)
             *      rocket is not falling
             * 
             * landing burn:
             *      rocket is falling   AND
             *      angle is within -50 and 50
             */
            else if ((a < 1.0 && a > -1.0 && vy >= 0) || (vy < 0 && a > -50 && a < 50))
                thrust = 1;

            // other (landed?)
            else
                thrust = 0;

            // <--if above threshold-->
            // NOTE: at peak sometimes rocket does not start falling before going neg angle
            /* <--turning thrust-->
             * falling slower than MAX_DOWN_V   AND
             * angle != 0                       AND
             */
        } else if (vy > -MAX_DOWN_V && a != 0)
            thrust = TURNING_THRUST;
            // if still accelerating/landing burn
        else if (a < 25 && a > -25)
            thrust = 1;
            // else lower thrust
        else
            thrust = TURNING_FALLING_THRUST;


        //---THRUST ANGLE---//

        // landed
        if (Math.round(y) == 10)
            angle = 0;

        else if (vy >= 0 && y < HEIGHT && a > -CORRECTION_ANGLE_BOUNDS/2 && a < CORRECTION_ANGLE_BOUNDS/2) {
            if (a == 0)
                angle = 0;
            else if (a > 0)
                angle = 0.05;
            else if (a < 0)
                angle = -0.05;
        }

        /* <--if above a certain point, start turning-->
         * rocket above height  AND
         * rocket not falling faster than MAX_DOWN_V
         */
        else if (y > HEIGHT && vy > -MAX_DOWN_V)
            angle += 0.5;

        // todo- have some sort of equation that uses both angle and x velocity
        // 
        /**<--straightening out angle when angle is closer to 0-->
         * rocket below height * multiplier AND
         * angle more than
         * 
         */
        else if (y < HEIGHT * CORRECTION_ANGLE_MULT && a > -CORRECTION_ANGLE_BOUNDS && a < CORRECTION_ANGLE_BOUNDS) {
            // landing
            if (a < 5 && a > -5)
                angle = 0;
            else if (a > 0) // facing left (pos) (neg x velocity)
                // angle = correction angle * average of x velocity and angle
                angle = CORRECTION_ANGLE * (Math.abs(vx) + Math.abs(a)/2);
            else if (a < 0) // facing right (neg) (pos x velocity)
                // angle = -(correction angle * average of x velocity and angle)
                angle = -(CORRECTION_ANGLE * (Math.abs(vx) + Math.abs(a))/2);
        }


        // <--because for some reason if (angle < 1) doesnt work-->
        if (angle > 1)
            angle = 1;
        else if (angle < -1)
            angle = -1;
        setThrustAngle(angle);

        // <--same for thrust-->
        if (thrust > 1)
            thrust = 1;
        else if (thrust < -1)
            thrust = -1;
        setThrust(thrust);
    }
}
