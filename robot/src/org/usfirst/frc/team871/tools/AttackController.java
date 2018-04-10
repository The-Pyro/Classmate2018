package org.usfirst.frc.team871.tools;

import edu.wpi.first.wpilibj.GenericHID;

public class AttackController extends GenericHID {
	double deadband;
	
	public AttackController(int port) {
		super(port);
		deadband = 0;
	}
	
	/**
	 * @author Jack Langhorn
	 * 
	 * Sets the deadband for all axes.  Deadband threshold must be greater than
	 * or equal to zero but less than one.
	 * 
	 * @param dead  Deadband value.
	 */
	public void setDeadband(double dead) {
		if (dead >= 0 && dead < 1) {
			deadband = dead;
		}
	}

	/**
	 * @author Jack Langhorn
	 * 
	 * Returns the only X axis on the controller.  The hand is ignored.
	 * 
	 * @param hand Ignored.
	 * @return The deadbanded joystick value.
	 */
	@Override
	public double getX(Hand hand) {
		return getAxis(0);
	}

	/**
	 * @author Jack Langhorn
	 * 
	 * Returns the only Y axis on the controller.  The hand is ignored.
	 * 
	 * @param hand Ignored.
	 * @return The deadbanded joystick value.
	 */
	@Override
	public double getY(Hand hand) {
		return getAxis(1);
	}
	
	/**
	 * @author Jack Langhorn
	 * 
	 * Gets deadbanded axis value.
	 * The following step function is used for calculating the return value:
	 * x < -deadband 				| -(((-x-d)^3)/((1-d)^3))
	 * -deadband <= x <= deadband 	| 0
	 * x > deadband 				| (((x-d)^3)/((1-d)^3))
	 * 
	 * The exponent can be varied to vary the slope of the curve.
	 * 
	 * The following formula can be used for a linear mapping:
	 * y = ((x-1)/(1-d)) + 1
	 * 
	 * @param axis The axis to measure
	 * @return The deadbanded joystick value
	 */
	public double getAxis(int axis) {
		double value = -getRawAxis(axis);
		
		if (value < -deadband) {
			return -(Math.pow((-value - deadband), 3)/Math.pow((1 - deadband), 3));
		} else if (value > deadband) {
			return (Math.pow((value - deadband), 3)/Math.pow((1 - deadband), 3));
		} else {
			return 0;
		}
	}
	
	@Override
	public String getName() {
		return "Logitech Attack3";
	}
	
	@Override
	public GenericHID.HIDType getType() {
		return GenericHID.HIDType.kHIDJoystick;
	}
}
