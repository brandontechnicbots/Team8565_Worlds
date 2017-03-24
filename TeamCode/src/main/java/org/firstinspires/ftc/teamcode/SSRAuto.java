package org.firstinspires.ftc.teamcode;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class SSRAuto extends LinearOpMode {

    SSRRobot robot = new SSRRobot(); // Get Robot Config. Edit robot config in the MainRobot file.
    private ElapsedTime runtime = new ElapsedTime();
    Double lineThreshold; //White tape threshold, computed in the loadTapeCalibration OpMode

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        loadTapeCalibration();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.gyroSensor.calibrate();

        while (!isStarted()) {
            telemetry.addData("Status", getRedAlliance() ? "RED alliance ready" : "BLUE alliance ready");
            telemetry.addData("DELAY(ms):", Integer.toString(getDelay()));
            telemetry.addData("Line Threshold", lineThreshold + ", Current: " + robot.lightSensor.getLightDetected());
            telemetry.addData("Gyro ", robot.gyroSensor.isCalibrating() ? "CALIBRATING" : robot.gyroSensor.getIntegratedZValue());
            telemetry.update();
            idle();
        }
        //waitForStart();
        resetStartTime();
        while (opModeIsActive()) { //Quick Hack to prevent errors (hopefully)
            if (!robot.gyroSensor.isCalibrating()) {
                break;
            }
        }
        robotSleep(500);

        telemetry.addData("InDelay", "yes");
        telemetry.update();
        robotSleep(getDelay()); //do we need delay

        navigateToBeacon();
        detectLine();
        pushBeacon();
        detectSecondLine();
        pushBeacon();
        endNavigation();
    }

    private void navigateToBeacon() {
        if (getRedAlliance()) {
            gyroTurn(30, 0, 1);
            encoderGyroDrive(3200, 0.4); //go forward
            gyroTurn(-15, 1, 0); //2nd turn
            encoderOnlyDrive(3200, 0.4, 0.4); //go forward into wall
            gyroTurn(-4);
            encoderOnlyDrive(300,-0.3,-0.3);
        } else {
            gyroTurn(-25, 0, 1);
            encoderGyroDrive(2900, -0.6);
            gyroTurn(5); //do a rough curve
            encoderGyroDrive(400, -0.6);
            gyroTurn(5);
            encoderGyroDrive(400, -0.2);
            encoderOnlyDrive(1900, -0.3, -0.3); //add stall protection??
            gyroTurn(3.5,0,1);
        }

    }

    private void detectLine() {
        if (getRedAlliance()) {
            robot.leftMotor.setPower(-0.15);
            robot.rightMotor.setPower(-0.20);
        } else {
            robot.leftMotor.setPower(0.13);
            robot.rightMotor.setPower(0.18);
        }
        while (opModeIsActive()) {
            telemetry.addData("Light1", robot.lightSensor.getLightDetected());
            telemetry.update();
            if (robot.lightSensor.getLightDetected() > lineThreshold) {
                robot.stopRobot();
                break;
            }
        }

        if (!getRedAlliance()) { //Compensation on blue only
            encoderGyroDrive(40, 0.3);
        } else { //Compensation on red only
            encoderGyroDrive(165, 0.15);
        }
    }

    private void detectSecondLine() {
        if (getRedAlliance()) {
            encoderGyroDrive(50, -0.3); //just so we don't detect the same line twice
            gyroTurn(-2.5, 0, 1);
            encoderOnlyDrive(800, -0.2, -0.25);
            detectLine();
            encoderGyroDrive(100, 0.15);

        } else {
            encoderGyroDrive(50, 0.3); //just so we don't detect the same line twice
            gyroTurn(2.5, 0, 1);
            encoderOnlyDrive(800, 0.2, 0.25);
            detectLine();
        }
    }

    private void pushBeacon() {
        int redTotal = 0;
        int blueTotal = 0;
        for (int i = 0; i < 2000; i++) { //Runs 100 times, tune this
            if (redTotal != 255 && blueTotal != 255) {
                redTotal += robot.colorSensor.red(); // Add to the values
                blueTotal += robot.colorSensor.blue();
            }
            telemetry.addData("Blue, Red", blueTotal + "," + redTotal);
            telemetry.update();
        }
        if (redTotal + blueTotal > 30) { //Only run if with readings
            if ((redTotal < blueTotal) ^ getRedAlliance()) { //XOR blue
                robot.beaconServo.setPosition(robot.beaconRight);
            } else if (redTotal != blueTotal) {
                robot.beaconServo.setPosition(robot.beaconLeft);
            }
            robotSleep(600);
            robot.beaconServo.setPosition(robot.beaconMiddle);
        }
    }

    void shootBalls() {
        shootBalls(2);
    }

    void shootBalls(int balls) {
        final double AUTO_SHOOTER_POWER = .6;
        robot.shooter.setPower(AUTO_SHOOTER_POWER); //turn on shooter
        robotSleep(600);
        robot.shooter.setPower(0);
        if (balls == 2) {
            robot.valveServo.setPosition(robot.valveClose);
            robotSleep(150);
            robot.valveServo.setPosition(robot.valveOpen);
            robotSleep(600);
            robot.shooter.setPower(AUTO_SHOOTER_POWER);
            robotSleep(600);
            robot.shooter.setPower(0);
        }
    }

    private void endNavigation() {
        if (getRedAlliance()) {
            gyroTurn(24, 1, 0);
            encoderGyroDrive(1300, -0.4);
            gyroTurn(21, 1, 0);
            shootBalls();
            if (getCorner()) {
                gyroTurn(80);
                encoderGyroDrive(1100, 0.4);
            } else {
                gyroTurn(80);
                encoderGyroDrive(1200, -0.4);
                gyroTurn(25);
                robotSleep(1000);
                gyroTurn(-30);
                encoderGyroDrive(800, -0.4);
            }

        } else {
            gyroTurn(60, 1, 0);
            gyroTurn(-40, 0, 1);
            shootBalls();
            if (getCorner()) {
                gyroTurn(-8, 1, 0);
                encoderGyroDrive(2200, 0.3);
            } else {
                gyroTurn(-82);
                encoderGyroDrive(2400, 0.3);
                robotSleep(1000);
                encoderGyroDrive(100, 0.3);
            }
        }
    }

    //Robot Basic Navigation Methods

    void encoderGyroDrive(int distance, double power) {
        if (robot.gyroSensor.isCalibrating()) return; //Bad
        robot.gyroSensor.resetZAxisIntegrator();
        int startDistance = robot.leftMotor.getCurrentPosition();
        resetStartTime();//Safety Timer

        while (opModeIsActive() && Math.abs(robot.leftMotor.getCurrentPosition() - startDistance) < Math.abs(distance) && getRuntime() < Math.abs(distance / 500) + 1000) {
            //if (!opModeIsActive()) return; //Emergency Kill
            //telemetry.addData("LENCODER", robot.leftMotor.getCurrentPosition());
            //telemetry.addData("RENCODER", robot.rightMotor.getCurrentPosition());
            //telemetry.update();
            //Log.i("DEBUG_Encoder", Double.toString(robot.leftMotor.getCurrentPosition()));
            //Log.i("DEBUG_Distance", Double.toString(-1 * (startDistance - robot.leftMotor.getCurrentPosition())));
            double error_degrees = robot.gyroSensor.getIntegratedZValue(); //Compute Error
            double correction = robot.gyroDriveController.findCorrection(error_degrees); //Get Correction
            correction = Range.clip(correction, -0.3, 0.3); //Limit Correction
            //Log.i("DEBUG_Error", String.valueOf(correction));
            robot.leftMotor.setPower(power - correction);
            robot.rightMotor.setPower(power + correction);
        }
        robot.stopRobot();
    }

    void encoderOnlyDrive(int distance, double leftPower, double rightPower) {
        int startDistance = robot.leftMotor.getCurrentPosition();
        resetStartTime();//Safety Timer

        while (opModeIsActive() && Math.abs(robot.leftMotor.getCurrentPosition() - startDistance) < Math.abs(distance) && getRuntime() < Math.abs(distance / 500) + 1000) {
            robot.leftMotor.setPower(leftPower);
            robot.rightMotor.setPower(rightPower);
        }
        robot.stopRobot();
    }

    void gyroTurn(double deg, double leftMultiplier, double rightMultiplier) {
        if (robot.gyroSensor.isCalibrating()) //Bad
            return;
        robot.gyroSensor.resetZAxisIntegrator();
        double target_angle = robot.gyroSensor.getIntegratedZValue() - deg;//Set goal
        resetStartTime();//Safety Timer

        while (opModeIsActive() && Math.abs(target_angle - robot.gyroSensor.getIntegratedZValue()) > 3 && getRuntime() < 3) {
            //if (!opModeIsActive()) return; //Emergency Kill
            double error_degrees = target_angle - robot.gyroSensor.getIntegratedZValue(); //Compute Error
            double motor_output = robot.gyroTurnController.findCorrection(error_degrees); //Get Correction
            //Log.e("cows", Double.toString(motor_output));
            if (leftMultiplier == 0 || rightMultiplier == 0) {
                motor_output = 1.7 * motor_output;
                motor_output = (motor_output > 0) ? Range.clip(motor_output, 0.37, 0.6) : Range.clip(motor_output, -0.6, -0.37);
            } else {
                motor_output = (motor_output > 0) ? Range.clip(motor_output, 0.22, 0.5) : Range.clip(motor_output, -0.5, -0.22);
            }
            robot.leftMotor.setPower(motor_output * leftMultiplier);
            robot.rightMotor.setPower(-1*motor_output * rightMultiplier);
            //Log.d("DEBUG_Gyro", Double.toString(robot.gyroSensor.getIntegratedZValue()));
            //telemetry.addData("Z:", robot.gyroSensor.getIntegratedZValue());
            //telemetry.addData("ERROR:", error_degrees);
            //telemetry.addData("MO:", motor_output);
            //telemetry.update();
        }
        robot.stopRobot();
    }

    void gyroTurn(double deg) {
        gyroTurn(deg, 1, 1);
    }

    void insertGamepadBreakpont() {
        while (opModeIsActive()) {
            telemetry.addData("Hit B to", " continue");
            telemetry.update();
            idle();
            if (gamepad1.b) {
                break;
            }
        }
    }

    void loadTapeCalibration() {
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
            Log.e("ERROR", "CALIBRATION NOT SET");
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e("ERROR", "CALIBRATION NOT SET");
        }

        lineThreshold = 0.6 * BLACKVALUE + 0.4 * WHITEVALUE;
    }

    void robotSleep(double t) {
        double rt = getRuntime();
        while (opModeIsActive()) {
            if (getRuntime() > rt + t / 1000) break;
        }
    }

    abstract protected int getDelay();

    abstract protected Boolean getRedAlliance();

    abstract protected Boolean getCorner();

}
