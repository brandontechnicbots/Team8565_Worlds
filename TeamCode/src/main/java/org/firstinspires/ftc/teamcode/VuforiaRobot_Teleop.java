package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Teleop for Vuforia", group="Iterative Opmode")  // @Autonomous(...) is the other common choice
public class VuforiaRobot_Teleop extends OpMode
{
    private ElapsedTime runtime = new ElapsedTime();

    DcMotor leftMotor;
    DcMotor rightMotor;
    Servo beacon;
    Float throttle, secondThrottle, secondRightThrottle, rightThrottle;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {
    }


    @Override
    public void start() {
        //HARDWARE MAP
        leftMotor  = hardwareMap.dcMotor.get("LeftMotor");
        rightMotor = hardwareMap.dcMotor.get("RightMotor");
        //beacon = hardwareMap.servo.get("beacon");

        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        //SERVO INITIALIZATION
        //beacon.setPosition(0.81);
        runtime.reset();
    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Running: " + runtime.toString());

        driveControl();
        buttonControl();
        //servoControl(beacon);
    }

    @Override
    public void stop() {
    }

    public void driveControl() {
        //Driving and Joystick controls
        throttle = -1 * gamepad1.left_stick_y;
        rightThrottle = -1 * gamepad1.right_stick_y;
        secondThrottle = -1 * gamepad2.left_stick_y;
        secondRightThrottle = -1 * gamepad2.right_stick_y;

        //Dead zone
        throttle = (Math.abs(throttle) < 0.3) ? 0 : throttle;
        rightThrottle = (Math.abs(rightThrottle) < 0.05) ? 0 : rightThrottle;
        secondThrottle = (Math.abs(secondThrottle) < 0.3) ? 0 : secondThrottle;
        secondRightThrottle = (Math.abs(secondRightThrottle) < 0.05) ? 0 : secondRightThrottle;

        //Clip at 1
        throttle = Range.clip(throttle, -1, 1);
        rightThrottle = Range.clip(rightThrottle, -1, 1);
        secondThrottle = Range.clip(secondThrottle, -1, 1);
        secondRightThrottle = Range.clip(secondRightThrottle, -1, 1);

        leftMotor.setPower(-throttle);
        rightMotor.setPower(-rightThrottle);

    }

    public void servoControl(Servo s) {

    }

    public void buttonControl(){

    }

    private void stopRobot() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}