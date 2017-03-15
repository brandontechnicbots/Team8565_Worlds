package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Teleop", group = "Iterative Opmode")  // @Autonomous(...) is the other common choice
public class SSRTeleop extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    SSRRobot robot = new SSRRobot(); // Get Robot Config. Edit robot config in the MainRobot file.
    GamepadWrapper joy1 = new GamepadWrapper();

    double throttle, secondThrottle, secondRightThrottle, rightThrottle;
    private int doublePressRBumper = 0;
    Boolean slowMode = false;
    final double SLOWMODEPOWER = 0.6;


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
        newDriveControl();
        buttonControl();
        //telemetry.addData("Slow Mode(Hit X)", slowMode);
        telemetry.addData("Status", "Running: " + runtime.toString());
        telemetry.addData("Controls", "Y-ClawOut,A-ClawIn,DpadU-Shoot");
//        telemetry.addData("Linear Slide(CAP=16.5k)", robot.linear.getCurrentPosition());
    }

    @Override
    public void stop() {
    }

    void driveControl() {
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

    public void newDriveControl() {
        throttle = gamepad1.left_stick_y;
        rightThrottle = gamepad1.right_stick_y;

        throttle = -(Math.signum(throttle) * ((Math.pow(throttle, 2) * (1 - .1)) + .1));
        rightThrottle = -(Math.signum(rightThrottle) * ((Math.pow(rightThrottle, 2) * (1 - .1)) + .1));

        //Dead zone
        throttle = (Math.abs(throttle) < 0.1) ? 0 : throttle;
        rightThrottle = (Math.abs(rightThrottle) < 0.1) ? 0 : rightThrottle;

        //Clip at 1
        throttle = Range.clip(throttle, -1, 1);
        rightThrottle = Range.clip(rightThrottle, -1, 1);

        robot.leftMotor.setPower(throttle);
        robot.rightMotor.setPower(rightThrottle);
        telemetry.addData("Throttle(L,R)", robot.leftMotor.getPower() + ", " + robot.rightMotor.getPower());
    }

    private void buttonControl() {

        joy1.update(gamepad1);

        if (gamepad1.x) {
            robot.beaconServo.setPosition(.22);
        } else if (gamepad1.b) {
            robot.beaconServo.setPosition(.98);
        } else if (gamepad1.a) {
            robot.beaconServo.setPosition(.63);
        }

        /*
        if (gamepad1.dpad_left) {
            robot.sweeper.setPower(0.03);
        } else if (joy1.toggle.y) {
            robot.sweeper.setPower(-0.9);
        } else {
            robot.sweeper.setPower(0);
        }

        /*if (joy1.toggle.dpad_down) {
            robot.sweeper.setPower(-0.9);
        } else {
            robot.sweeper.setPower(0);
        }*/
        /*
        if (gamepad1.left_trigger == 1) {
            if (robot.linear.getCurrentPosition() > 0) {
                robot.linear.setPower(-1);
            } else {
                robot.linear.setPower(0);
            }
        } else if (gamepad1.right_trigger == 1) {
            if (robot.linear.getCurrentPosition() < 16500) {
                robot.linear.setPower(1);
            } else {
                robot.linear.setPower(0);
            }
        } else {
            robot.linear.setPower(0);
        } */

        if (gamepad1.right_bumper) {
            doublePressRBumper++;
            telemetry.addData("Double Bumper,", doublePressRBumper);
            if (doublePressRBumper > 200) {
                robot.releaseServo.setPosition(1);
                doublePressRBumper = 0;
            }
        } else {
            robot.releaseServo.setPosition(.076);
        }

        if (gamepad1.dpad_up || gamepad2.dpad_up) {
            robot.shooter.setPower(0.8);
        } else if (gamepad1.dpad_down || gamepad2.dpad_down) {
            robot.shooter.setPower(-0.5);
        } else {
            robot.shooter.setPower(0);
        }
    }


}
