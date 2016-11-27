package org.firstinspires.ftc.teamcode;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class Meet1_Auto extends LinearOpMode {

    MainRobot robot = new MainRobot();   // Get Robot Config. HINT TO SAMUEL: Edit robot config in the MainRobot file.
    private ElapsedTime runtime = new ElapsedTime();
    Double lineThreshold; //White tape threshold, computed in the loadTapeCalibration OpMode
    PIDController gyroController; //PID Controller for Gyro

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        loadTapeCalibration();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.gyroSensor.calibrate();

        telemetry.addData("Status", "Wait For Start");
        telemetry.update();
        idle();
        waitForStart();
        runtime.reset();
        while (robot.gyroSensor.isCalibrating()) robotSleep(200); //Wait for Gyro to finish calibrating
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
            encoderGyroDrive(300, 0.3);
            gyroTurn(37);
            encoderGyroDrive(3000, 0.5);
            gyroTurn(-36, 1, 0);
            encoderGyroDrive(950, 0.5);
        } else {
            encoderGyroDrive(200, -0.3);
            gyroTurn(-37);
            encoderGyroDrive(2950, -0.5);
            gyroTurn(38, 1, 0); //Left SWT
            encoderGyroDrive(700, -0.5);
        }
    }

    private void detectLine() {
        while (opModeIsActive()) {
            //Log.d("Debug", "Light1:" + Double.toString(lightSensor.getLightDetected()));
            if (getRedAlliance()) {
                robot.leftMotor.setPower(-0.17);
                robot.rightMotor.setPower(-0.22);
            } else {
                robot.leftMotor.setPower(0.2);
                robot.rightMotor.setPower(0.17);
            }
            if (robot.lightSensor.getLightDetected() > lineThreshold) {
                robot.stopRobot();
                break;
            }
        }
    }

    private void pushBeacon() {
        int redTotal = 0;
        int blueTotal = 0;
        for (int i = 0; i < 5000; i++) { //Runs 100 times, tune this
            redTotal += robot.colorSensor.red(); // Add to the values
            blueTotal += robot.colorSensor.blue();
            //telemetry.addData("Blue, Red",  blueTotal + "," + redTotal);
            //telemetry.update();
        }
        if (redTotal + blueTotal > 30) { //Only run if with readings
            if ((redTotal < blueTotal) ^ getRedAlliance()) { //XOR blue
                robot.backServo.setPosition(0.8);
                robotSleep(400);
                robot.frontServo.setPosition(0.1);
            } else {
                robot.frontServo.setPosition(0.8);
                robotSleep(400);
                robot.backServo.setPosition(0.1);
            }
            robot.frontServo.setPosition(0.1);
            robot.backServo.setPosition(0.1);
        }
    }

    private void detectSecondLine() {
        if (getRedAlliance()) {
            encoderGyroDrive(800, 0.3);
        } else {
            encoderGyroDrive(800, -0.3);
        }
        while (opModeIsActive()) {
            //Log.d("Debug", "Light2:" + Double.toString(lightSensor.getLightDetected()));
            if (getRedAlliance()) {
                robot.leftMotor.setPower(0.15);
                robot.rightMotor.setPower(0.23);
            } else {
                robot.leftMotor.setPower(-0.2);
                robot.rightMotor.setPower(-0.2);
            }
            if (robot.lightSensor.getLightDetected() > lineThreshold) {
                robot.stopRobot();
                break;
            }
        }
    }

    private void endNavigation() {
        if (getRedAlliance()) {
            gyroTurn(50, 1, 0);
            encoderGyroDrive(3000, -0.4);
        } else {
            gyroTurn(-49, 1, 0);
            encoderGyroDrive(3000, 0.4);
        }
    }

    //Robot Basic Navigation Methods

    private void encoderGyroDrive(int distance, double power) {
        if (robot.gyroSensor.isCalibrating()) return; //Bad
        robot.gyroSensor.resetZAxisIntegrator();
        int startDistance = robot.leftMotor.getCurrentPosition();
        resetStartTime();//Safety Timer

        while (Math.abs(robot.leftMotor.getCurrentPosition() - startDistance) < Math.abs(distance) && getRuntime() < Math.abs(distance / 500) + 1000) {
            if (!opModeIsActive()) return; //Emergency Kill
            //telemetry.addData("LENCODER", robot.leftMotor.getCurrentPosition());
            //telemetry.addData("RENCODER", robot.rightMotor.getCurrentPosition());
            //telemetry.update();
            //Log.i("DEBUG_Encoder", Double.toString(robot.leftMotor.getCurrentPosition()));
            //Log.i("DEBUG_Distance", Double.toString(-1 * (startDistance - robot.leftMotor.getCurrentPosition())));
            double error_degrees = robot.gyroSensor.getIntegratedZValue(); //Compute Error
            double correction = robot.gyroDriveController.findCorrection(error_degrees); //Get Correction
            correction = Range.clip(correction, -0.3, 0.3); //Limit Correction
            //Log.i("DEBUG_Error", String.valueOf(correction));
            robot.leftMotor.setPower(power + correction);
            robot.rightMotor.setPower(power - correction);
        }
        robot.stopRobot();
    }

    private void gyroTurn(double deg, double leftMultiplier, double rightMultiplier) {
        if (robot.gyroSensor.isCalibrating()) //Bad
            return;
        robot.gyroSensor.resetZAxisIntegrator();
        double target_angle = robot.gyroSensor.getIntegratedZValue() + deg;//Set goal
        resetStartTime();//Safety Timer

        while (Math.abs(target_angle - robot.gyroSensor.getIntegratedZValue()) > 2 && getRuntime() < 10) {
            if (!opModeIsActive()) return; //Emergency Kill
            double error_degrees = target_angle - robot.gyroSensor.getIntegratedZValue(); //Compute Error
            double motor_output = robot.gyroTurnController.findCorrection(error_degrees); //Get Correction
            if (motor_output > 0) motor_output = Range.clip(motor_output, 0.6, 1);
            else if (motor_output < 0) motor_output = Range.clip(motor_output, -1, -0.6);
            robot.leftMotor.setPower(-1 * motor_output * leftMultiplier);
            robot.rightMotor.setPower(motor_output * rightMultiplier);
            //Log.d("DEBUG_Gyro", Double.toString(robot.gyroSensor.getIntegratedZValue()));
        }
        robot.stopRobot();
        return;
    }

    private void gyroTurn(double deg) {
        gyroTurn(deg, 1, 1);
    }

    private void loadTapeCalibration() {
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

    private void robotSleep(double t) {
        double rt = getRuntime();
        while (opModeIsActive()) {
            if (getRuntime()> rt + t/1000) break;
        }
    }

    abstract protected int getDelay();

    abstract protected Boolean getRedAlliance();
}
