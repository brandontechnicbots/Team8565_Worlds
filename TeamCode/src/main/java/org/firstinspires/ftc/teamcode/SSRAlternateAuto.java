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

import static android.R.interpolator.linear;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class SSRAlternateAuto extends SSRAuto {

    SSRRobot robot = new SSRRobot(); // Get Robot Config. Edit robot config in the MainRobot file.
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.gyroSensor.calibrate();

        while (!isStarted()) {
            telemetry.addData("Status", getRedAlliance() ? "RED alliance ready" : "BLUE alliance ready");
            telemetry.addData("DELAY(ms):", Integer.toString(getDelay()));
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
        robotSleep(500);
        shootBalls();
        endNavigation();
    }

    private void navigateToBeacon() {
        if (getRedAlliance()) {
            encoderGyroDrive(270, 0.4); //forward
            gyroTurn(67); //turn left
            encoderGyroDrive(2100, 0.4);
        } else {
            encoderGyroDrive(400, -0.4); //forward
            gyroTurn(-62); //turn left
            encoderGyroDrive(2100, -0.4);
        }

    }

    private void endNavigation() {
        if (getRedAlliance()) {

            if (getCorner()) {
                encoderGyroDrive(2200, 0.3); //to ramp
            } else {
                gyroTurn(87); //turn left
                encoderGyroDrive(1600, -0.3);
                gyroTurn(25);
                robotSleep(1000);
                gyroTurn(-30);
                encoderGyroDrive(800, -0.4);
            }

        } else {

            if (getCorner()) {
                encoderGyroDrive(2200, -0.3); //to ramp
            } else {
                encoderGyroDrive(200, 0.3);
                gyroTurn(87); //turn left
                encoderGyroDrive(1500, -0.3);
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

    void shootBalls() {
        shootBalls(2);
    }

    void shootBalls(int balls) {
        final double AUTO_SHOOTER_POWER = .6;
        robot.shooter.setPower(AUTO_SHOOTER_POWER); //turn on shooter
        robotSleep(800);
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

}
