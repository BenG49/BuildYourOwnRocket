package com.stuypulse.rocket.rocket;

import javax.lang.model.util.ElementScanner6;

public class RockController {

    public RockController() {}

    /**
     * PID Controller?
     * 
     * @param angleAim angle that controller aims for
     * @param velocityAim y velocity that controller aims for
     * @param rock input rocket
     * @param deadThrust thrust to use when facing downwards
     * @return 2 long double array {thrust output, angle output}
     */
    public static double[] getCorrection(double angleAim, double velocityAim, Rocket rock, float deadThrust, int maxDownV) {
        // constants
        int COEFFICIENT_INCREASE_CUTOFF = 10;
        int MAX_DOWN_V = maxDownV;
        float DEAD_THRUST = deadThrust;

        double outThrust = 0;
        double outAngle = 0;
        double coefficient;
        double vxCoefficient;

        double a  = rock.getState().getAngle().toDegrees();
        double vx = rock.getState().getVelocity().x;
        double vy = rock.getState().getVelocity().y;
        double y  = rock.getState().getPosition().y;
        
        // -------------ANGLE COEFFICIENT------------- //
        // multiplier for angle, larger as angle is farther from aim
        // higher divisor = higher angle
        coefficient = Math.abs(angleAim - a)/1500;

        // imagine being able to write exponential curves, couldnt be me
        if (Math.abs(angleAim - a) > COEFFICIENT_INCREASE_CUTOFF && vy > -MAX_DOWN_V)
            coefficient *= 2;
        
        // -------------VX COEFFICIENT------------- //
        if (Math.abs(vx) > 15)
            vxCoefficient = Math.abs(a);
        else if (Math.abs(vx) < 5)
            vxCoefficient = Math.abs(vx) * 1.25;
        else
            vxCoefficient = Math.abs(vx);
        
        // -------------ANGLE------------- //
        // special case, to get out of takeoff?
        if (a == 0) {
            if (a > angleAim)
                outAngle = 0.01;
            else if (a < angleAim)
                outAngle = -0.01;
            else
                outAngle = 0;
        } else if (angleAim == a)
            outAngle = 0;
        else {
            // conditional vx, make it weigh less as its higher, weigh more as its lower
            outAngle = coefficient * (vxCoefficient + Math.abs(a)/2);

            if (a < angleAim)
                outAngle *= -1;
        }
        // angle correction
        if (outAngle > 1)
            outAngle = 1;
        else if (outAngle < -1)
            outAngle = -1;

        // -------------THRUST------------- //
        // if angled downwards (pretty much don't do this)
        if (a > 90 || a < -90) {
            if (vy > -MAX_DOWN_V) {
                outThrust = DEAD_THRUST;
            } else 
                outThrust = DEAD_THRUST * 0.6666;
        // if angled in a sensible orientation
        } else
            outThrust = (velocityAim - vy) * 1.5;
        // thrust correction
        if (outThrust > 1)
            outThrust = 1;
        else if (outThrust < 0)
            outThrust = 0;
        
        return new double[] {outThrust, outAngle};
    }
}