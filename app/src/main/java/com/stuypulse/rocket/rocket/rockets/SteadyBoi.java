package com.stuypulse.rocket.rocket.rockets;

import java.awt.Color;
import com.stuypulse.rocket.rocket.Rocket;
import com.stuypulse.rocket.rocket.RocketState;
import com.stuypulse.stuylib.math.Angle;

/**
 * This is my shot at a robot that hovers around y = 100
 * 
 * The reason the code looks like this is so that you guys dont steal it. This
 * is a pretty good example of what the rocket should do, but I don't want you
 * to copy it.
 * 
 * @author Sam Belliveau (sam.belliveau@gmail.com)
 */

public class SteadyBoi extends Rocket {
    public static final double TARGET_HEIGHT = 100;
    public static final Angle TARGET_ANG = Angle.fromDegrees(5);

    public SteadyBoi() {}

    public String getAuthor() {
        return "Sam";
    }

    double MIN_THRUST = 0.01,
        THRUST_ANGLE_MULTIPLIER = 0.01,
        THRUST_SUBTRACTION_COEFFICIENT = 1.16612,
        CONSTANT_502 = 7.165224,
        // used for thrust calculation
        CONSTANT_504 = 3.257124,
        // used as a cutoff
        CONSTANT_506 = 6.2658654,
        // used for thrust calculation and as a cutoff
        CONSTANT_507 = 8.3763268,

        y,                  // y = 35.5235356,
        _P509,              // _P509 = -1999.257124,
        finalThrust,        // finalThrust = 0.32452543,
        thrustAngleSign;    // thrustAngleSign = -23.2356436;

    RocketState steadyBoiState;

    public void updateAngle(double in) {
        /*if (in < CONSTANT_506) {
            _P509 = CONSTANT_507;
        } else {
            _P509 = THRUST_ANGLE_MULTIPLIER;
        }/

        if (_P509 < CONSTANT_506) {*/
        if (in > CONSTANT_506) {
            thrustAngleSign = steadyBoiState.getAngle().add(TARGET_ANG).sin();
        } else {
            steadyBoiState = getState();
        }
    }

    public void calcThrust(double in) {
        /*
        // always will be true, else is dead code (unless constants are different)
        if (CONSTANT_506 < CONSTANT_507) {
            _P509 = in;
        } else {
            _P509 = CONSTANT_502;
        }

        if (_P509 < CONSTANT_506) {*/

        // thrust cutoff, if finalThrust is set in both cases, then rocket does not stop
        if (in < CONSTANT_506) {
            // finalThrust is subtracted from after this
            finalThrust = TARGET_HEIGHT;
            y = steadyBoiState.getPosition().y;
        } else {
            y = steadyBoiState.getVelocity().y;
        }
    }

    public void thrust() {
        // if thrust is less than cutoff
        if (finalThrust < MIN_THRUST) {
            // finalThrustAngle might be wrong?
            finalThrust = THRUST_ANGLE_MULTIPLIER;
        }

        // if thrust is more than 1, set to 1
        if (finalThrust > 1) {
            finalThrust = 1;
        }
        setThrust(finalThrust);
    }

    public void thrustAngle() {
        setThrustAngle(Math.signum(thrustAngleSign) * THRUST_ANGLE_MULTIPLIER);
    }

    protected void execute() {
        updateAngle(THRUST_ANGLE_MULTIPLIER);
        calcThrust(CONSTANT_504);
        finalThrust -= y;
        updateAngle(THRUST_ANGLE_MULTIPLIER);
        calcThrust(CONSTANT_502);
        finalThrust -= THRUST_SUBTRACTION_COEFFICIENT * y;

        thrust();

        updateAngle(CONSTANT_507);
        thrustAngle();
    }

    public Color getColor() {
        return Color.magenta;
    }
}