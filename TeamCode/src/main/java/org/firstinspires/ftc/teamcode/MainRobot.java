package org.firstinspires.ftc.teamcode;

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

/**
 * This is NOT an opmode.
 * <p>
 * This class can be used to define all the specific hardware for a single
 */

public class MainRobot {
    /* Public OpMode members. */
    public DcMotor leftMotor, rightMotor, linear, shooter = null;
    public Servo leftClaw, rightClaw = null;
    public Servo frontServo, backServo, slideServo = null;
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

        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        linear.setDirection(DcMotor.Direction.FORWARD);
        shooter.setDirection(DcMotor.Direction.FORWARD);

        // Set all motors to zero power
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        linear.setPower(0);
        shooter.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Set Brake Behavior
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Define and initialize ALL installed servos.
        frontServo = hwMap.servo.get("front");
        backServo = hwMap.servo.get("back");
        rightClaw = hwMap.servo.get("rightc");
        leftClaw = hwMap.servo.get("leftc");
        //slideServo = hwMap.servo.get("slide");
        frontServo.setPosition(0.1);
        backServo.setPosition(0.1);
        //slideServo.setPosition(1.0);
        leftClaw.setPosition(0.327);
        rightClaw.setPosition(0.515);

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

