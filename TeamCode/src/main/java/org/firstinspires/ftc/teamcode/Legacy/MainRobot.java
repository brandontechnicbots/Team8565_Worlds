package org.firstinspires.ftc.teamcode.Legacy;

import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsAnalogOpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.PIDController;

/**
 * This is NOT an opmode.
 * <p>
 * This class can be used to define all the specific hardware for a single
 */

public class MainRobot {
    /* Public OpMode members. */
    public DcMotor leftMotor, rightMotor, linear, shooter, sweeper = null;
    public Servo beaconServo, frontServo, backServo, leftClaw, rightClaw, capServo = null;
    public ModernRoboticsI2cGyro gyroSensor = null;
    public ModernRoboticsAnalogOpticalDistanceSensor lightSensor = null;
    public ModernRoboticsI2cColorSensor colorSensor = null;
    public PIDController gyroDriveController = new PIDController("Drive", 0.03, 0.0, 0, 0.8),
            gyroTurnController = new PIDController("Turn", 0.025, 0.0000, 0, 0.8);

    /* local OpMode members. */
    HardwareMap hwMap = null;
    private ElapsedTime period = new ElapsedTime();

    /* Constructor */
    public MainRobot() {

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        leftMotor = hwMap.dcMotor.get("left");
        rightMotor = hwMap.dcMotor.get("right");
        linear = hwMap.dcMotor.get("linear");
        shooter = hwMap.dcMotor.get("shooter");
        sweeper = hwMap.dcMotor.get("sweeper");

        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        linear.setDirection(DcMotor.Direction.REVERSE);
        shooter.setDirection(DcMotor.Direction.FORWARD);
        sweeper.setDirection(DcMotor.Direction.FORWARD);

        // Set all motors to zero power
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        linear.setPower(0);
        shooter.setPower(0);
        sweeper.setPower(0);

        // Set all motors to run with/without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sweeper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Set Brake Behavior
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sweeper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Define and initialize ALL installed servos.
        beaconServo = hwMap.servo.get("auto");
        capServo = hwMap.servo.get("cap");
        beaconServo.setPosition(0.7);
        capServo.setPosition(0.02);

        //Initialize Sensors
        gyroSensor = (ModernRoboticsI2cGyro) hwMap.gyroSensor.get("gyro");
        lightSensor = (ModernRoboticsAnalogOpticalDistanceSensor) hwMap.opticalDistanceSensor.get("light");
        colorSensor = (ModernRoboticsI2cColorSensor) hwMap.colorSensor.get("color");
    }

    public void stopRobot() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        //sleep(500);
    }

}

