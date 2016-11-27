package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Autonomous(name = "Motor Test", group = "Linear Opmode")
public class MotorTest extends LinearOpMode {

    MainRobot robot = new MainRobot();   // Get Robot Config

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        robot.gyroSensor.calibrate();

        telemetry.addData("Status", "Wait For Start/Gyro Calibration");
        telemetry.update();
        waitForStart();
        while (robot.gyroSensor.isCalibrating())
            robotSleep(200); //Wait for Gyro to finish calibrating

        telemetry.addData("PHASE 1-Motors/Gyro", "CHECK TELEMETRY");
        telemetry.update();
        encoderGyroDrive(500, 0.3);
        encoderGyroDrive(500, -0.3);
        gyroTurn(-90);
        gyroTurn(180);
        gyroTurn(-90);

        telemetry.addData("PHASE 2-SERVOS", "CHECK TELEMETRY");
        telemetry.update();

        robot.frontServo.setPosition(0.8);
        robot.backServo.setPosition(0.8);
        robot.leftClaw.setPosition(0.586);
        robot.rightClaw.setPosition(0.42);
        telemetry.addData("Servos", robot.frontServo.getPosition() + ", " +
                robot.backServo.getPosition() + ", " +
                robot.leftClaw.getPosition() + ", " +
                robot.rightClaw.getPosition());
        robotSleep(2000);

        robot.frontServo.setPosition(0.1);
        robot.backServo.setPosition(0.1);
        robot.leftClaw.setPosition(0.426);
        robot.rightClaw.setPosition(0.58);

        robotSleep(2000);

    }

    private void encoderGyroDrive(int distance, double power) {
        if (robot.gyroSensor.isCalibrating()) return; //Bad
        robot.gyroSensor.resetZAxisIntegrator();
        int startDistance = robot.leftMotor.getCurrentPosition();
        resetStartTime();//Safety Timer

        while (Math.abs(robot.leftMotor.getCurrentPosition() - startDistance) < Math.abs(distance) && getRuntime() < Math.abs(distance / 500) + 1000) {
            if (!opModeIsActive()) return; //Emergency Kill
            //telemetry.addData("LENCODER", robot.leftMotor.getCurrentPosition());
            //telemetry.addData("RENCODER", robot.rightMotor.getCurrentPosition());
            telemetry.addData("Distance(Left Motor)", -1 * (startDistance - robot.leftMotor.getCurrentPosition()));
            telemetry.update();
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
            telemetry.addData("Gyro Error", error_degrees);
            telemetry.update();
            if (motor_output > 0) motor_output = Range.clip(motor_output, 0.6, 1);
            else if (motor_output < 0) motor_output = Range.clip(motor_output, -1, -0.6);
            robot.leftMotor.setPower(-1 * motor_output * leftMultiplier);
            robot.rightMotor.setPower(motor_output * rightMultiplier);
        }
        robot.stopRobot();
        return;
    }

    private void gyroTurn(double deg) {
        gyroTurn(deg, 1, 1);
    }

    private void robotSleep(double t) {
        double rt = getRuntime();
        while (opModeIsActive()) {
            if (getRuntime() > rt + t / 1000) break;
        }
    }
}