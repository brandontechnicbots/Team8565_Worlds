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
    MainRobot robot = new MainRobot();   // Get Robot Config. HINT TO SAMUEL: Edit robot config in the MainRobot file.

    double throttle, secondThrottle, secondRightThrottle, rightThrottle;
    Boolean slowMode = false;
    final double SLOWMODEPOWER = 0.2;

    @Override
    public void init() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    runtime.reset();
    }

    @Override
    public void loop() {
        driveControl();
        buttonControl();
        //servoControl(robot.rightClaw);
        telemetry.addData("Slow Mode(Hit X)", slowMode);
        telemetry.addData("Status", "Running: " + runtime.toString());
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
        throttle = (Math.abs(throttle) < 0.1) ? 0 : throttle;
        rightThrottle = (Math.abs(rightThrottle) < 0.1) ? 0 : rightThrottle;
        secondThrottle = (Math.abs(secondThrottle) < 0.1) ? 0 : secondThrottle;
        secondRightThrottle = (Math.abs(secondRightThrottle) < 0.1) ? 0 : secondRightThrottle;

        //Slow Mode
        if (slowMode) {
            throttle = SLOWMODEPOWER * Math.signum(throttle);
            rightThrottle = SLOWMODEPOWER * Math.signum(rightThrottle);
            secondThrottle = SLOWMODEPOWER * Math.signum(secondThrottle);
            secondRightThrottle = SLOWMODEPOWER * Math.signum(secondRightThrottle);
        }

        //Clip at 1
        throttle = Range.clip(throttle, -1, 1);
        rightThrottle = Range.clip(rightThrottle, -1, 1);
        secondThrottle = Range.clip(secondThrottle, -1, 1);
        secondRightThrottle = Range.clip(secondRightThrottle, -1, 1);

        robot.leftMotor.setPower(throttle);
        robot.rightMotor.setPower(rightThrottle);
        telemetry.addData("Throttle(L,R)", robot.leftMotor.getPower() + ", " + robot.rightMotor.getPower());
    }

    public void servoControl(Servo s) {
        if (gamepad1.x)
            s.setPosition(0.5);
        if (gamepad1.y)
            s.setPosition(Range.clip(s.getPosition() + 0.002, 0, 1));
        else if (gamepad1.a)
            s.setPosition(Range.clip(s.getPosition() - 0.002, 0, 1));
        telemetry.addData("Servo Pos:", s.getPosition());
    }

    public void buttonControl() {
        if (gamepad1.x) {
                slowMode = !slowMode;
        } else if (gamepad1.b) {
            //robot.slideServo.setPosition(0.4);
        }

        if (gamepad1.y) {
            robot.leftClaw.setPosition(0.426);
            robot.rightClaw.setPosition(0.58);
        } else if (gamepad1.a) {
            robot.leftClaw.setPosition(0.586);
            robot.rightClaw.setPosition(0.42);
        }
        if (gamepad1.right_trigger == 1) {
            robot.linear.setPower(1);
        } else if (gamepad1.left_trigger == 1) {
            robot.linear.setPower(-1);
        } else {
            robot.linear.setPower(0);
        }

    }

}