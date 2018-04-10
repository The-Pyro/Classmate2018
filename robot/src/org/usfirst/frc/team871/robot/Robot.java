package org.usfirst.frc.team871.robot;

import org.usfirst.frc.team871.tools.AttackController;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Spark;

public class Robot extends IterativeRobot {
	private static final String ANALOG_KEY = "analogs";
	private static final String DIO_KEY = "dios";
	Spark blackMotor, redMotor;
	int blackMotorController, redMotorController;
	boolean invertBlackMotor, invertRedMotor;

	Relay relay;
	
	NetworkTable table;
	
	DigitalInput dios[];
	
	AnalogInput analogs[];
	
	AttackController joy0, joy1;
	
	@Override
	public void robotInit() {
		blackMotor = new Spark(0);
		blackMotorController = -1;
		invertBlackMotor = false;

		redMotor = new Spark(1);
		redMotorController = -1;
		invertBlackMotor = false;
		
		relay = new Relay(0);
		
		dios = new DigitalInput[10];
		for (int i = 0; i < 10; i++) {
			dios[i] = new DigitalInput(i);
		}
		
		analogs = new AnalogInput[4];
		for (int i = 0; i < 4; i++) {
			analogs[i] = new AnalogInput(i);
		}
		
		joy0 = new AttackController(0);
		joy0.setDeadband(0.05);

		joy1 = new AttackController(1);
		joy1.setDeadband(0.05);

		NetworkTableInstance defaultInstance = NetworkTableInstance.getDefault();
		defaultInstance.startClientTeam(871);
		defaultInstance.setNetworkIdentity("robot");
		
		table = defaultInstance.getTable("classmate");
		table.addEntryListener("relay",
				(table, key, entry, value, flags) -> {
					switch (entry.getString("off").toLowerCase()) {
					case "off":
						relay.set(Relay.Value.kOff);
						break;
					case "forward":
						relay.set(Relay.Value.kForward);
						break;
					case "reverse":
						relay.set(Relay.Value.kReverse);
						break;
					default:
						relay.set(Relay.Value.kOff);
						break;
					}
				}, TableEntryListener.kNew | TableEntryListener.kUpdate);
		
		table.getSubTable("blackMotor").addEntryListener("config",
				(table, key, entry, value, flags) -> {
					String params[] = entry.getString("-1:false").split(":");
					blackMotorController = Integer.parseInt(params[0]);
					invertBlackMotor = Boolean.parseBoolean(params[1]);
				}, TableEntryListener.kNew | TableEntryListener.kUpdate);

		table.getSubTable("redMotor").addEntryListener("config",
				(table, key, entry, value, flags) -> {
					String params[] = entry.getString("-1:false").split(":");
					redMotorController = Integer.parseInt(params[0]);
					invertRedMotor = Boolean.parseBoolean(params[1]);
				}, TableEntryListener.kNew | TableEntryListener.kUpdate);
	}
	
	@Override
	public void teleopInit() {
	}

	@Override
	public void teleopPeriodic() {
		double joy0Val = joy0.getAxis(1);
		double joy1Val = joy1.getAxis(1);
			
		boolean dioVals[] = new boolean[10];
		for (int i = 0; i < 10; i++) {
			dioVals[i] = dios[i].get();
		}
		table.getEntry(DIO_KEY).setBooleanArray(dioVals);
		
		double analogVals[] = new double[4];
		for (int i = 0; i < 4; i++) {
			analogVals[i] = analogs[i].getVoltage();
		}
		table.getEntry(ANALOG_KEY).setDoubleArray(analogVals);

		double blackMotorVal = 0.0;
		switch (blackMotorController) {
		case 0:
			blackMotorVal = (invertBlackMotor ? -joy0Val : joy0Val);
			break;
		case 1:
			blackMotorVal = (invertBlackMotor ? -joy1Val : joy1Val);
			break;
		case 2:
			blackMotorVal = (invertBlackMotor) ? -analogVals[0]/5 :
				analogVals[0]/5;
			break;
		default:
			blackMotorVal = 0.0;
			break;
		}	
		blackMotor.set(blackMotorVal);
		table.getSubTable("blackMotor").getEntry("speed").setDouble(blackMotorVal);

		double redMotorVal = 0.0;
		switch (redMotorController) {
		case 0:
			redMotorVal = (invertRedMotor ? -joy0Val : joy0Val);
			break;
		case 1:
			redMotorVal = (invertRedMotor ? -joy1Val : joy1Val);
			break;
		case 2:
			redMotorVal = (invertRedMotor) ? -analogVals[0]/5 : analogVals[0]/5;
			break;
		default:
			redMotorVal = 0.0;
			break;
		}
		redMotor.set(redMotorVal);
		table.getSubTable("redMotor").getEntry("speed").setDouble(redMotorVal);
	}

	@Override
	public void disabledInit() {
		table.getSubTable("blackMotor").getEntry("speed").setDouble(0.0);
		table.getSubTable("redMotor").getEntry("speed").setDouble(0.0);
	}
	
	@Override
	public void disabledPeriodic() {	
		boolean dioVals[] = new boolean[10];
		for (int i = 0; i < 10; i++) {
			dioVals[i] = dios[i].get();
		}
		table.getEntry(DIO_KEY).setBooleanArray(dioVals);
		
		double analogVals[] = new double[4];
		for (int i = 0; i < 4; i++) {
			analogVals[i] = analogs[i].getVoltage();
		}
		table.getEntry(ANALOG_KEY).setDoubleArray(analogVals);
	}
}
