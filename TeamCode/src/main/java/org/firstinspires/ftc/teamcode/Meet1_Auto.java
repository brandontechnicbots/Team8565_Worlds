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

@Autonomous(name = "Autonomous", group = "Linear Opmode")
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
        beacon = hardwareMap.servo.get("beacon");
        sensorGyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        sensorGyro.calibrate();
        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightMotor.setDirection(DcMotor.Direction.FORWARD);
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sensorGyro.calibrate();

        waitForStart();
        runtime.reset();
        while (sensorGyro.isCalibrating()) sleep(200); //Wait for Gyro to finish calibrating
        sleep(500);

        telemetry.addData("InDelay", "yes");
        sleep(getDelay()); //do we need delay

        //START TUNING HERE!!!
        encoderGyroDrive(4000, -0.3);
        gyroPID(90);
        encoderGyroDrive(3900, -0.3);
        encoderGyroDrive(2000, 0.3);
        gyroPID(-90);
        encoderGyroDrive(2000, -0.3);
        gyroPID(90);
        encoderGyroDrive(2000, -0.3);
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
        gyroController = new PIDController("gyro", 0.0045, 0.0001, 0, 0.8);
        if (sensorGyro.isCalibrating()) //Bad
            return;
        sensorGyro.resetZAxisIntegrator();
        double target_angle = sensorGyro.getIntegratedZValue() + deg;//Set goal
        resetStartTime();//Safety Timer

        while (Math.abs(target_angle - sensorGyro.getIntegratedZValue()) > 2 && getRuntime() < 10) {
            if (!opModeIsActive()) return; //Emergency Kill
            double error_degrees = target_angle - sensorGyro.getIntegratedZValue(); //Compute Error
            double motor_output = gyroController.findCorrection(error_degrees); //Get Correction
            telemetry.addData("Gyro:", String.valueOf(sensorGyro.getIntegratedZValue()));
            motor_output = Range.clip(motor_output, -0.5, 0.5); //Clip motors
            leftMotor.setPower(-1*motor_output);
            rightMotor.setPower(motor_output);
            if (Math.abs(target_angle - sensorGyro.getIntegratedZValue()) < 2)
                return;
        }
    }

    private void stopRobot() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    abstract protected int getDelay();

    abstract protected int getRedAlliance();
}
