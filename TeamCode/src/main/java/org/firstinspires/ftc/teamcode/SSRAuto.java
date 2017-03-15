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

        telemetry.addData("Status", "Wait For Start");
        telemetry.addData("DELAY(ms):", Integer.toString(getDelay()) + ", Red Alliance:" + getRedAlliance());
        telemetry.addData("Line Threshold", lineThreshold + ", Current: " + robot.lightSensor.getLightDetected());
        telemetry.update();
        idle();
        waitForStart();
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
            gyroTurn(34,0,1);
            encoderGyroDrive(3300, 0.4); //go forward
            gyroTurn(-26, 1, 0); //2nd turn
            encoderOnlyDrive(3200, 0.4, 0.4); //go forward into wall
            gyroTurn(-4);
        } else {
            gyroTurn(-25,0,1);
            encoderGyroDrive(2900, -0.6);
            gyroTurn(5); //do a rough curve
            encoderGyroDrive(400, -0.6);
            gyroTurn(5);
            encoderGyroDrive(400, -0.2);
            encoderOnlyDrive(1900, -0.3, -0.3); //add stall protection??
        }

    }

    private void detectLine() {
        if (getRedAlliance()) {
            robot.leftMotor.setPower(-0.15);
            robot.rightMotor.setPower(-0.20);
        } else {
            robot.leftMotor.setPower(0.13);
            robot.rightMotor.setPower(0.17);
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
            encoderGyroDrive(50, 0.3);
        } else { //Compensation on red only
            encoderGyroDrive(230, 0.15);
        }
    }

    private void detectSecondLine() {
        if (getRedAlliance()) {
            encoderGyroDrive(50, -0.3); //just so we don't detect the same line twice
            gyroTurn(-2.5, 0, 1);
            encoderOnlyDrive(800, -0.2, -0.25);
            detectLine();
            encoderGyroDrive(50, 0.15);

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
                robot.beaconServo.setPosition(.98);
            } else if (redTotal != blueTotal) {
                robot.beaconServo.setPosition(.22);
            }
            robotSleep(400);
            robot.beaconServo.setPosition(.63);
        }
    }

    void shootBalls() {
        shootBalls(false);
    } //overload this to hell

    void shootBalls(boolean preMove) {
        if (preMove) {
            encoderGyroDrive(100, 0.2); //drive forwards
        }
        robot.shooter.setPower(0.75); //turn on shooter
        robotSleep(600);
        robot.shooter.setPower(0);
//        robot.sweeper.setPower(-0.9);
        robotSleep(500); //pause between shots
//        robot.sweeper.setPower(0);
        robot.shooter.setPower(0.75);
        robotSleep(600);
        robot.shooter.setPower(0);
    }

    private void endNavigation() {
        if (getRedAlliance()) {
            gyroTurn(27, 1, 0);
            encoderGyroDrive(1300, -0.4);
            gyroTurn(17, 1, 0);
            shootBalls();
            gyroTurn(80);
            encoderGyroDrive(1100, 0.4);

        } else {
            gyroTurn(60,1,0);
            gyroTurn(-46,0,1);
            shootBalls();
            gyroTurn(-10,1,0);
            encoderGyroDrive(1800, 0.3);
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
            robot.leftMotor.setPower(power + correction);
            robot.rightMotor.setPower(power - correction);
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
        double target_angle = robot.gyroSensor.getIntegratedZValue() + deg;//Set goal
        resetStartTime();//Safety Timer

        while (opModeIsActive() && Math.abs(target_angle - robot.gyroSensor.getIntegratedZValue()) > 2 && getRuntime() < 5) {
            //if (!opModeIsActive()) return; //Emergency Kill
            double error_degrees = target_angle - robot.gyroSensor.getIntegratedZValue(); //Compute Error
            double motor_output = robot.gyroTurnController.findCorrection(error_degrees); //Get Correction
            motor_output = (motor_output > 0) ? Range.clip(motor_output, 0.25, 0.6) : Range.clip(motor_output, -0.6, -0.25);
            robot.leftMotor.setPower(-1 * motor_output * leftMultiplier);
            robot.rightMotor.setPower(motor_output * rightMultiplier);
            //Log.d("DEBUG_Gyro", Double.toString(robot.gyroSensor.getIntegratedZValue()));
        }
        robot.stopRobot();
        return;
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
}
