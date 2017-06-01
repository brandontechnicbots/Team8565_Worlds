package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Dumbo Bot Teleop", group = "Shooterbot")  // @Autonomous(...) is the other common choice
public class DumboRobotTeleop extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    DumboRobot robot = new DumboRobot(); // Get Robot Config.
    GamepadWrapper joy1 = new GamepadWrapper();

    double throttle, secondThrottle, secondRightThrottle, rightThrottle;

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
        telemetry.addData("Controls", "X,B,A-Beacon DpadL,Y-Sweeper LT,RT-Lift RB-Release");
//        telemetry.addData("Linear Slide(CAP=16.5k)", robot.linear.getCurrentPosition());
    }

    @Override
    public void stop() {
    }

    public void newDriveControl() {
        throttle = gamepad1.left_stick_y;
        rightThrottle = gamepad1.right_stick_y;
        secondThrottle = gamepad2.left_stick_y;
        secondRightThrottle = gamepad2.right_stick_y;

        throttle = -(Math.signum(throttle) * ((Math.pow(throttle, 2) * (1 - .1)) + .1));
        rightThrottle = -(Math.signum(rightThrottle) * ((Math.pow(rightThrottle, 2) * (1 - .1)) + .1));
        secondThrottle = -(Math.signum(secondThrottle) * ((Math.pow(secondThrottle, 2) * (1 - .1)) + .1)) * .5;
        secondRightThrottle = -(Math.signum(secondRightThrottle) * ((Math.pow(secondRightThrottle, 2) * (1 - .1)) + .1)) *.5;

        //Dead zone
        throttle = (Math.abs(throttle) < 0.1) ? 0 : throttle;
        rightThrottle = (Math.abs(rightThrottle) < 0.1) ? 0 : rightThrottle;
        secondThrottle = (Math.abs(secondThrottle) < 0.1) ? 0 : secondThrottle;
        secondRightThrottle = (Math.abs(secondRightThrottle) < 0.1) ? 0 : secondRightThrottle;

        //Clip at 1
        throttle = Range.clip(throttle, -1, 1);
        rightThrottle = Range.clip(rightThrottle, -1, 1);
        secondThrottle = Range.clip(secondThrottle, -1, 1);
        secondRightThrottle = Range.clip(secondRightThrottle, -1, 1);

        if (throttle != 0 || rightThrottle !=0) {
            robot.leftMotor.setPower(throttle);
            robot.rightMotor.setPower(rightThrottle);
        } else {
            robot.leftMotor.setPower(secondThrottle);
            robot.rightMotor.setPower(secondRightThrottle);
        }

        telemetry.addData("Throttle(L,R)", robot.leftMotor.getPower() + ", " + robot.rightMotor.getPower());
    }


    private void buttonControl() {

        joy1.update(gamepad1);

        //Sweeper and manual beacon presser servo
        if (gamepad1.dpad_left) { //1st joystick dpad left-slow backward, put back plate up
            robot.sweeper.setPower(0.03);
        } else if (joy1.toggle.y) { //1st joy y-normal speed forward , put back plate up
            robot.sweeper.setPower(-0.15);
        } else if (gamepad2.left_trigger > 0.5) { //2nd joy LT-normal speed backward
            robot.sweeper.setPower(0.05);
        } else if (gamepad2.right_trigger > 0.5) { //2nd joy RT-normal speed forward
            robot.sweeper.setPower(-0.05);
        } else if (gamepad1.dpad_right) { //1st joy dpad right- stop sweeper, put back plate down
            robot.sweeper.setPower(0);
        } else if (gamepad2.a) {
            //robot.backSweeperServo.setPosition(robot.backSweeperUp);
            //robot.sweeper.setPower(-0.03);
        } else {
            robot.sweeper.setPower(0);
        }

        //Linear Slide
        if (gamepad1.left_trigger == 1) {
            robot.linear.setPower(-1);
        } else if (gamepad1.right_trigger == 1) {
            robot.linear.setPower(1);
        } else {
            robot.linear.setPower(0);
        }



        //Shooter
        if (gamepad1.dpad_up || gamepad2.dpad_up) {
            robot.shooter.setPower(0.8);
        } else if (gamepad1.dpad_down || gamepad2.dpad_down) {
            robot.shooter.setPower(-0.5);
        } else {
            robot.shooter.setPower(0);
        }
    }

}
