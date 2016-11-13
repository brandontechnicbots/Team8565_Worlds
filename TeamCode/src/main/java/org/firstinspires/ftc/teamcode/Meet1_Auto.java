package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsAnalogOpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class Meet1_Auto extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    DcMotor leftMotor, rightMotor;
    Servo rightClaw, leftClaw;
    Servo frontServo, backServo;
    PIDController gyroController;
    ModernRoboticsI2cGyro gyroSensor;
    ModernRoboticsAnalogOpticalDistanceSensor lightSensor;
    ModernRoboticsI2cColorSensor colorSensor;


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
        gyroSensor = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        lightSensor = (ModernRoboticsAnalogOpticalDistanceSensor) hardwareMap.opticalDistanceSensor.get("light");
        colorSensor = (ModernRoboticsI2cColorSensor) hardwareMap.colorSensor.get("color");
        frontServo.setPosition(0.1);
        backServo.setPosition(0.1);
        rightClaw.setPosition(0);
        leftClaw.setPosition(1);
        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        gyroSensor.calibrate();

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
    }

    private void navigateToBeacon() throws InterruptedException {
        if (getRedAlliance() == true) {
            encoderGyroDrive(500, 0.3);
            gyroPID(30);
            encoderGyroDrive(5000, 0.5);
            gyroLeftSWT(-30);
        } else {
            encoderGyroDrive(500, -0.3);
            gyroPID(-30);
            encoderGyroDrive(3800, -0.5);
            gyroLeftSWT(41);
            gyroLeftSWT(-5);
            gyroLeftSWT(-2);
        }


    }

    private void detectLine() throws InterruptedException {
        while (opModeIsActive()) {
            Log.d("Debug", "Light1:" + Double.toString(lightSensor.getLightDetected()));
            if (getRedAlliance()) {
                leftMotor.setPower(-0.17);
                rightMotor.setPower(-0.22);
            } else {
                leftMotor.setPower(0.2);
                rightMotor.setPower(0.2);
            }
            if (lightSensor.getLightDetected() > 0.15) {
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
            Log.d("Debug", "Light2:" + Double.toString(lightSensor.getLightDetected()));
            if (getRedAlliance()) {
                leftMotor.setPower(0.15);
                rightMotor.setPower(0.23);
            } else {
                leftMotor.setPower(-0.2);
                rightMotor.setPower(-0.2);
            }
            if (lightSensor.getLightDetected() > 0.14) {
                stopRobot();
                break;
            }
        }
    }

    private void encoderGyroDrive(int distance, double power) throws InterruptedException {
        gyroController = new PIDController("gyro", 0.03, 0.0, 0, 0.8);
        if (gyroSensor.isCalibrating()) return; //Bad
        gyroSensor.resetZAxisIntegrator();
        int startDistance = rightMotor.getCurrentPosition();
        resetStartTime();//Safety Timer

        while (Math.abs(rightMotor.getCurrentPosition() - startDistance) < Math.abs(distance) && getRuntime() < Math.abs(distance / 500)) {
            if (!opModeIsActive()) return; //Emergency Kill
            telemetry.addData("Distance", -1 * (startDistance - rightMotor.getCurrentPosition()));
            telemetry.update();
            double error_degrees = gyroSensor.getIntegratedZValue(); //Compute Error
            double correction = gyroController.findCorrection(error_degrees); //Get Correction
            correction = Range.clip(correction, -0.3, 0.3); //Limit Correction
            //Log.i("Error", String.valueOf(correction));
            leftMotor.setPower(power + correction);
            rightMotor.setPower(power - correction);
            //log();
        }
        stopRobot();
    }

    private void gyroPID(double deg) throws InterruptedException {
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
            //Log.d("Debug", Double.toString(motor_output));
            if (motor_output > 0) motor_output = Range.clip(motor_output, 0.6, 1);
            else if (motor_output < 0) motor_output = Range.clip(motor_output, -1, -0.6);
            leftMotor.setPower(-1 * motor_output);
            rightMotor.setPower(motor_output);
        }
        stopRobot();
        return;
    }

    private void gyroLeftSWT(double deg) throws InterruptedException {
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
            //Log.d("Debug", Double.toString(motor_output));
            if (motor_output > 0) motor_output = Range.clip(motor_output, 0.6, 1);
            else if (motor_output < 0) motor_output = Range.clip(motor_output, -1, -0.6);
            leftMotor.setPower(-1 * motor_output);
            //rightMotor.setPower(motor_output);
        }
        stopRobot();
        return;
    }
    private void gyroRightSWT(double deg) throws InterruptedException {
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
            //Log.d("Debug", Double.toString(motor_output));
            if (motor_output > 0) motor_output = Range.clip(motor_output, 0.6, 1);
            else if (motor_output < 0) motor_output = Range.clip(motor_output, -1, -0.6);
            rightMotor.setPower(motor_output);
        }
        stopRobot();
        return;
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
