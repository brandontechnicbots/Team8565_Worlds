package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.text.SimpleDateFormat;
import java.util.Date;

@TeleOp(name = "Teleop", group = "Iterative Opmode")  // @Autonomous(...) is the other common choice
public class Meet1_Teleop extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();

    DcMotor leftMotor;
    DcMotor rightMotor;
    DcMotor linear;
    Servo frontServo, backServo, leftClaw, rightClaw, slideServo;
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
        leftMotor = hardwareMap.dcMotor.get("left");
        rightMotor = hardwareMap.dcMotor.get("right");
        linear = hardwareMap.dcMotor.get("linear");
        frontServo = hardwareMap.servo.get("front");
        backServo = hardwareMap.servo.get("back");
        slideServo = hardwareMap.servo.get("slide");
        rightClaw = hardwareMap.servo.get("rightc");
        leftClaw = hardwareMap.servo.get("leftc");
        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightMotor.setDirection(DcMotor.Direction.FORWARD);

        //SERVO INITIALIZATION
        frontServo.setPosition(0.1);
        backServo.setPosition(0.1);
        leftClaw.setPosition(0.5);
        rightClaw.setPosition(0.492);
        slideServo.setPosition(0.8);
        runtime.reset();
    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Running: " + runtime.toString());

        driveControl();
        buttonControl();
    }

    @Override
    public void stop() {
        //stopRobot();
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
        /*if (gamepad1.x)
            s.setPosition(0.5);
        if (gamepad1.y)
            s.setPosition(s.getPosition() + 0.002);
        else if (gamepad1.a)
            s.setPosition(s.getPosition() - 0.002);
        telemetry.addData("Servo Pos:", s.getPosition());*/
    }

    public void buttonControl() {
        if (gamepad1.x) {
            slideServo.setPosition(0.05);
        } else if (gamepad1.b) {
            slideServo.setPosition(0.4);
        }

        if (gamepad1.y) {
            leftClaw.setPosition(0.5);
            rightClaw.setPosition(0.492);
        } else if (gamepad1.a) {
            leftClaw.setPosition(0.422);
            rightClaw.setPosition(0.582);
        }
        if (gamepad1.right_trigger == 1) {
            linear.setPower(1);
        } else if (gamepad1.left_trigger == 1) {
            linear.setPower(-1);
        } else {
            linear.setPower(0);
        }

    }

    private void stopRobot() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}