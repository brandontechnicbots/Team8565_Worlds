package org.firstinspires.ftc.teamcode;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsAnalogOpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.qualcomm.robotcore.hardware.HardwareDevice.Manufacturer.ModernRobotics;

/**
 * This is NOT an opmode.
 * <p>
 * This class can be used to define all the specific hardware for a single
 */

public class SSRRobot {
    byte[] rangeCache;
    I2cAddr ultraSensorAddress = new I2cAddr(0x14);

    /* Public OpMode members. */
    public DcMotor leftMotor, rightMotor, linear, shooter, sweeper = null;
    public Servo beaconServo, releaseServo, leftCapServo, rightCapServo, valveServo = null;
    public I2cDevice ultraSensor = null;
    public I2cDeviceSynch ultraSensorReader = null;
    public ModernRoboticsI2cGyro gyroSensor = null;
    public ModernRoboticsAnalogOpticalDistanceSensor lightSensor = null;
    public ModernRoboticsI2cColorSensor colorSensor = null;

    public PIDController gyroDriveController = new PIDController("Drive", 0.03, 0.0, 0, 0.8),
            gyroTurnController = new PIDController("Turn", 0.005, 0.004, 0.0, 0.8);

    public static final double releaseOpen       =  .4;  // positions for release servo
    public static final double releaseClosed     =  .05;
    public static final double valveOpen         =   1;
    public static final double valveClose        =  .5;
    public static final double beaconLeft        =  .23;  // positions for beacon servo
    public static final double beaconMiddle      =  .63;
    public static final double beaconRight       =  .99;

    public static final int ULTRA_REG_START = 0x04;
    public static final int ULTRA_READ_LENGTH = 2;


    /* local OpMode members. */
    HardwareMap hwMap = null;
    private ElapsedTime period = new ElapsedTime();

    /* Constructor */
    public SSRRobot() {

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
        sweeper = hwMap.dcMotor.get("harvester");

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
        beaconServo = hwMap.servo.get("beacon");
        releaseServo = hwMap.servo.get("release");
        valveServo = hwMap.servo.get("valve");

        //leftCapServo = hwMap.servo.get("linear1");
        //rightCapServo = hwMap.servo.get("linear1");
        releaseServo.setPosition(releaseClosed);
        beaconServo.setPosition(beaconMiddle);
        valveServo.setPosition(valveOpen);
        //leftCapServo.setPosition(0.02);
        //rightCapServo.setPosition(0.02);

        //Initialize Sensors
        gyroSensor = (ModernRoboticsI2cGyro) hwMap.gyroSensor.get("gyro");
        lightSensor = (ModernRoboticsAnalogOpticalDistanceSensor) hwMap.opticalDistanceSensor.get("light");
        colorSensor = (ModernRoboticsI2cColorSensor) hwMap.colorSensor.get("color");
        ultraSensor = hwMap.i2cDevice.get("ultra");
        colorSensor.enableLed(false);

        ultraSensorReader = new I2cDeviceSynchImpl(ultraSensor, ultraSensorAddress, false);
        ultraSensorReader.engage();
    }

    public void stopRobot() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        //sleep(500);
    }


}

