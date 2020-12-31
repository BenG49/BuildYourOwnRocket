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

    double finalThrust = 0.32452543,
        // constants
        // some kind of constant, maybe gravity? - seems like it doesn't matter
        GRAVITY = 7.165224,
        MIN_THRUST = 0.01,
        // constant related to amount of thrust subtracted
        THRUST_SUBTRACTION_COEFFICIENT = 1.16612,
        CONSTANT_504 = 3.257124,
        // some kind of cutoff point
        CONSTANT_506 = 6.2658654,
        CONSTANT_507 = 8.3763268,

        // used to transfer to 509 in F_Q96(), set before calling
        _P505 = 7.165224,
        y = 35.5235356,
        _P509 = -1999.257124,
        finalThrustAngle = 0.01,
        // used as cutoff in F_T21(), set before calling to GRAVITY or 507
        updateAngleInputVar = -23.2356436,
        // pos/neg/0 of thrust angle
        thrustAngleSign = -23.2356436;


    RocketState steadyBoiState;

    public void updateAngle() {
        // sets 509
        if (updateAngleInputVar < CONSTANT_506) {
            _P509 = CONSTANT_507;
        } else {
            _P509 = finalThrustAngle;
        }

        // either sets thrust angle or uses same angle and updates state?
        if (_P509 < CONSTANT_506) {
            thrustAngleSign = steadyBoiState.getAngle().add(TARGET_ANG).sin();
        } else {
            // updates rocket state
            steadyBoiState = getState();
        }
    }

    public void F_Q96() {
        if (CONSTANT_506 < CONSTANT_507) {
            _P509 = _P505;
        } else {
            _P509 = GRAVITY;
        }

        if (_P509 < CONSTANT_506) {
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
            finalThrust = finalThrustAngle;
        }

        // if thrust is more than 1, set to 1
        if (finalThrust > 1) {
            finalThrust = 1;
        }
        setThrust(finalThrust);
    }

    public void thrustAngle() {
        // sets thrust angle to finalThrustAngle signed by thrustAngleSign
        setThrustAngle(Math.signum(thrustAngleSign) * finalThrustAngle);
    }

    protected void execute() {
        updateAngleInputVar = finalThrustAngle;
        updateAngle();
        _P505 = CONSTANT_504;
        F_Q96();
        finalThrust -= y;
        updateAngleInputVar = finalThrustAngle;
        updateAngle();
        _P505 = GRAVITY;
        F_Q96();
        finalThrust -= THRUST_SUBTRACTION_COEFFICIENT * y;
        thrust();
        updateAngleInputVar = CONSTANT_507;
        updateAngle();
        thrustAngle();
    }

    public Color getColor() {
        return Color.magenta;
    }
}