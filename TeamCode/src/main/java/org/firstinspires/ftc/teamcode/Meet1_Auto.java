package org.firstinspires.ftc.teamcode;

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
    Servo beacon;
    PIDController gyroController;
    ModernRoboticsI2cGyro sensorGyro;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftMotor = hardwareMap.dcMotor.get("left");
        rightMotor = hardwareMap.dcMotor.get("right");
        //beacon = hardwareMap.servo.get("beacon");
        sensorGyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        sensorGyro.calibrate();
        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sensorGyro.calibrate();

        waitForStart();
        runtime.reset();
        while (sensorGyro.isCalibrating()) sleep(200); //Wait for Gyro to finish calibrating
        sleep(500);

        telemetry.addData("InDelay", "yes");
        sleep(getDelay()); //do we need delay

        encoderGyroDrive(1300, -0.3);
        gyroPID(60);
        encoderGyroDrive(2900, -0.3);
        gyroPID(-60);
        encoderGyroDrive(4000, -0.3);

    }

    private void encoderGyroDrive(int distance, double power) throws InterruptedException {
        gyroController = new PIDController("gyro", 0.03, 0.0, 0, 0.8);
        if (sensorGyro.isCalibrating()) return; //Bad
        sensorGyro.resetZAxisIntegrator();
        int startDistance = rightMotor.getCurrentPosition();
        resetStartTime();//Safety Timer

        while (Math.abs(rightMotor.getCurrentPosition() - startDistance) < Math.abs(distance) && getRuntime() < Math.abs(distance / 500)) {
            if (!opModeIsActive()) return; //Emergency Kill
            telemetry.addData("Distance", -1 * (startDistance - rightMotor.getCurrentPosition()));
            telemetry.update();
            double error_degrees = sensorGyro.getIntegratedZValue(); //Compute Error
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
        gyroController = new PIDController("gyro", 0.006, 0.0005, 0, 0.8);
        if (sensorGyro.isCalibrating()) //Bad
            return;
        sensorGyro.resetZAxisIntegrator();
        double target_angle = sensorGyro.getIntegratedZValue() + deg;//Set goal
        resetStartTime();//Safety Timer

        while (Math.abs(target_angle - sensorGyro.getIntegratedZValue()) > 2 && getRuntime() < 10) {
            if (!opModeIsActive()) return; //Emergency Kill
            double error_degrees = target_angle - sensorGyro.getIntegratedZValue(); //Compute Error
            double motor_output = gyroController.findCorrection(error_degrees); //Get Correction
            telemetry.addData("Gyro:", error_degrees);
            telemetry.update();
            if (motor_output>0) motor_output = Range.clip(motor_output, 0.08, 0.4);
            else if (motor_output<0) motor_output= Range.clip(motor_output,-0.4, -0.08);
            leftMotor.setPower(-1 * motor_output);
            rightMotor.setPower(motor_output);
        }
        stopRobot();
        return;
    }

    private void pushBeacon() throws InterruptedException {
        int redTotal = 0;
        int blueTotal = 0;
        for (int i = 0; i < 100; i++) { //Runs 100 times, tune this
            //redTotal += colorsensor.red(); // Add to the values
            //blueTotal += colorsensor.blue();
        }
        telemetry.addData("Blue, Red",  blueTotal + "," + redTotal);
        telemetry.update();
        if (redTotal + blueTotal > 30) { //Only run if with readings
            if (redTotal < blueTotal) {
                if (getRedAlliance() == 0) {
                } else {
                }
            } else if (redTotal > blueTotal) {
                if (getRedAlliance() == 0) {
                } else {
                }
            }
        }
    }
    private void stopRobot() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        double t = getRuntime();
        while (getRuntime()< t+0.3) {} //hack until I remember the right command
    }

    abstract protected int getDelay();

    abstract protected int getRedAlliance();
}
