package org.firstinspires.ftc.teamcode;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsAnalogOpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class Meet1_Auto extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    DcMotor leftMotor, rightMotor;
    Servo rightClaw, leftClaw;
    Servo frontServo, backServo, slideServo;
    PIDController gyroController;
    ModernRoboticsI2cGyro gyroSensor;
    ModernRoboticsAnalogOpticalDistanceSensor lightSensor;
    ModernRoboticsI2cColorSensor colorSensor;
    Double lineThreshold;


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftMotor = hardwareMap.dcMotor.get("left");
        rightMotor = hardwareMap.dcMotor.get("right");
        frontServo = hardwareMap.servo.get("front");
        backServo = hardwareMap.servo.get("back");
        rightClaw = hardwareMap.servo.get("rightc");
        leftClaw = hardwareMap.servo.get("leftc");
        slideServo = hardwareMap.servo.get("slide");
        gyroSensor = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        lightSensor = (ModernRoboticsAnalogOpticalDistanceSensor) hardwareMap.opticalDistanceSensor.get("light");
        colorSensor = (ModernRoboticsI2cColorSensor) hardwareMap.colorSensor.get("color");
        frontServo.setPosition(0.1);
        backServo.setPosition(0.1);
        slideServo.setPosition(1.0);
        rightClaw.setPosition(0.5);
        leftClaw.setPosition(0.5);
        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        gyroSensor.calibrate();


        loadCalibration();

        waitForStart();
        runtime.reset();
        while (gyroSensor.isCalibrating()) sleep(200); //Wait for Gyro to finish calibrating
        sleep(500);

        telemetry.addData("InDelay", "yes");
        sleep(getDelay()); //do we need delay

        navigateToBeacon();
        detectLine();
        pushBeacon();
        detectSecondLine();
        pushBeacon();
        endNavigation();
    }

    private void navigateToBeacon() throws InterruptedException {
        if (getRedAlliance()) {
            encoderGyroDrive(300, 0.3);
            gyroTurn(37);
            encoderGyroDrive(3000, 0.5);
            gyroTurn(-36,1,0);
            encoderGyroDrive(950, 0.5);

        } else {
            encoderGyroDrive(200, -0.3);
            gyroTurn(-37);
            encoderGyroDrive(2950, -0.5);
            gyroTurn(38, 1, 0); //Left SWT
            encoderGyroDrive(700, -0.5);
        }
    }

    private void detectLine() throws InterruptedException {
        while (opModeIsActive()) {
            //Log.d("Debug", "Light1:" + Double.toString(lightSensor.getLightDetected()));
            if (getRedAlliance()) {
                leftMotor.setPower(-0.17);
                rightMotor.setPower(-0.22);
            } else {
                leftMotor.setPower(0.2);
                rightMotor.setPower(0.17);
            }
            if (lightSensor.getLightDetected() > lineThreshold) {
                stopRobot();
                break;
            }
        }
    }

    private void pushBeacon() throws InterruptedException {
        int redTotal = 0;
        int blueTotal = 0;
        for (int i = 0; i < 5000; i++) { //Runs 100 times, tune this
            redTotal += colorSensor.red(); // Add to the values
            blueTotal += colorSensor.blue();
            //telemetry.addData("Blue, Red",  blueTotal + "," + redTotal);
            //telemetry.update();
        }
        if (redTotal + blueTotal > 30) { //Only run if with readings
            if ((redTotal < blueTotal) ^ getRedAlliance()) { //XOR blue
                backServo.setPosition(0.8);
                sleep(400);
                frontServo.setPosition(0.1);
            } else {
                frontServo.setPosition(0.8);
                sleep(400);
                backServo.setPosition(0.1);
            }
            frontServo.setPosition(0.1);
            backServo.setPosition(0.1);
        }
    }

    private void detectSecondLine() throws InterruptedException {
        if (getRedAlliance()) {
            encoderGyroDrive(800, 0.3);
        } else {
            encoderGyroDrive(800, -0.3);
        }
        while (opModeIsActive()) {
            //Log.d("Debug", "Light2:" + Double.toString(lightSensor.getLightDetected()));
            if (getRedAlliance()) {
                leftMotor.setPower(0.15);
                rightMotor.setPower(0.23);
            } else {
                leftMotor.setPower(-0.2);
                rightMotor.setPower(-0.2);
            }
            if (lightSensor.getLightDetected() > lineThreshold) {
                stopRobot();
                break;
            }
        }
    }

    private void endNavigation() throws InterruptedException {
        if (getRedAlliance()) {
            gyroTurn(50, 1, 0);
            encoderGyroDrive(3000,-0.4);
        } else {
            gyroTurn(-49, 1, 0);
            encoderGyroDrive(3000,0.4);
        }
    }

    private void encoderGyroDrive(int distance, double power) throws InterruptedException {
        gyroController = new PIDController("gyro", 0.03, 0.0, 0, 0.8);
        if (gyroSensor.isCalibrating()) return; //Bad
        gyroSensor.resetZAxisIntegrator();
        int startDistance = leftMotor.getCurrentPosition();
        resetStartTime();//Safety Timer

        while (Math.abs(leftMotor.getCurrentPosition() - startDistance) < Math.abs(distance) && getRuntime() < Math.abs(distance / 500) + 1000) {
            if (!opModeIsActive()) return; //Emergency Kill
            //telemetry.addData("LENCODER", leftMotor.getCurrentPosition());
            //telemetry.addData("RENCODER", rightMotor.getCurrentPosition());
            //telemetry.update();
            //Log.i("DEBUG_Encoder", Double.toString(leftMotor.getCurrentPosition()));
            //Log.i("DEBUG_Distance", Double.toString(-1 * (startDistance - leftMotor.getCurrentPosition())));
            double error_degrees = gyroSensor.getIntegratedZValue(); //Compute Error
            double correction = gyroController.findCorrection(error_degrees); //Get Correction
            correction = Range.clip(correction, -0.3, 0.3); //Limit Correction
            //Log.i("DEBUG_Error", String.valueOf(correction));
            leftMotor.setPower(power + correction);
            rightMotor.setPower(power - correction);
            //log();
        }
        stopRobot();
    }

    private void gyroTurn(double deg, double leftMultiplier, double rightMultiplier) throws InterruptedException {
        gyroController = new PIDController("gyro", 0.025, 0.0000, 0, 0.8);
        if (gyroSensor.isCalibrating()) //Bad
            return;
        gyroSensor.resetZAxisIntegrator();
        double target_angle = gyroSensor.getIntegratedZValue() + deg;//Set goal
        resetStartTime();//Safety Timer

        while (Math.abs(target_angle - gyroSensor.getIntegratedZValue()) > 2 && getRuntime() < 10) {
            if (!opModeIsActive()) return; //Emergency Kill
            double error_degrees = target_angle - gyroSensor.getIntegratedZValue(); //Compute Error
            double motor_output = gyroController.findCorrection(error_degrees); //Get Correction
            if (motor_output > 0) motor_output = Range.clip(motor_output, 0.6, 1);
            else if (motor_output < 0) motor_output = Range.clip(motor_output, -1, -0.6);
            leftMotor.setPower(-1 * motor_output * leftMultiplier);
            rightMotor.setPower(motor_output * rightMultiplier);
            Log.d("DEBUG_Gyro", Double.toString(gyroSensor.getIntegratedZValue()));
        }
        stopRobot();
        return;
    }

    private void gyroTurn(double deg) throws InterruptedException {
        gyroTurn(deg, 1, 1);
    }

    private void loadCalibration() {
        //load calibration values
        double WHITEVALUE = 0;
        double BLACKVALUE = 0;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/FIRST/calibration.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String date = br.readLine();
            WHITEVALUE = Double.parseDouble(br.readLine());
            BLACKVALUE = Double.parseDouble(br.readLine());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        lineThreshold = 0.6 * BLACKVALUE + 0.4 * WHITEVALUE;
    }

    private void stopRobot() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        /*double t = getRuntime();
        while (opModeIsActive()) {
            if (getRuntime() > t + 0.1) {
                break;
            }
        }*/
    }

    abstract protected int getDelay();

    abstract protected Boolean getRedAlliance();
}
