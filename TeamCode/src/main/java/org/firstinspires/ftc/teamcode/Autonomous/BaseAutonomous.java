package org.firstinspires.ftc.teamcode.Autonomous;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.SSRRobot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class BaseAutonomous extends LinearOpMode {

    SSRRobot robot = new SSRRobot(); // Get Robot Config. Edit robot config in the MainRobot file.
    private ElapsedTime runtime = new ElapsedTime();
    Double lineThreshold; //White tape threshold, computed in the loadTapeCalibration OpMode

    //Method to shoot balls

    void shootBalls() {
        shootBalls(2);
    }

    void shootBalls(int balls) {
        final double AUTO_SHOOTER_POWER = .6;
        robot.shooter.setPower(AUTO_SHOOTER_POWER); //turn on shooter
        robotSleep(600);
        robot.shooter.setPower(0);
        robotSleep(800);
        if (balls == 2) {
            robot.valveServo.setPosition(robot.valveClose); //let 2nd ball drop in
            robotSleep(150);
            robot.valveServo.setPosition(robot.valveOpen);
            robotSleep(600);
            robot.shooter.setPower(AUTO_SHOOTER_POWER); //shoot 2nd ball
            robotSleep(600);
            robot.shooter.setPower(0);
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

        while (opModeIsActive() && Math.abs(target_angle - robot.gyroSensor.getIntegratedZValue()) > 3 && getRuntime() < 1.5) {
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
