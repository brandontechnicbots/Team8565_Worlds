package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;

@TeleOp(name = "Teleop", group = "Iterative Opmode")  // @Autonomous(...) is the other common choice
public class SSRTeleop extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    SSRRobot robot = new SSRRobot(); // Get Robot Config.
    GamepadWrapper joy1 = new GamepadWrapper();

    double throttle, secondThrottle, secondRightThrottle, rightThrottle;
    private int doublePressRBumper = 0;
    private int ultrasonicTimer, ultrasonicDistance, ultrasonicState = 0;
    Boolean slowMode = false;

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
        ballFeedControl();
        //telemetry.addData("Slow Mode(Hit X)", slowMode);
        telemetry.addData("Status", "Running: " + runtime.toString());
        telemetry.addData("Controls", "X,B,A-Beacon DpadL,Y-Sweeper LT,RT-Lift RB-Release");
//        telemetry.addData("Linear Slide(CAP=16.5k)", robot.linear.getCurrentPosition());
    }

    @Override
    public void stop() {
    }

    void driveControl() {
        final double SLOWMODEPOWER = 0.6;
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

    private void ballFeedControl() {
        robot.rangeCache = robot.ultraSensorReader.read(robot.ULTRA_REG_START, robot.ULTRA_READ_LENGTH);
        ultrasonicDistance = (robot.rangeCache[0] & 0xFF);

        //telemetry.addData("US Value", ultrasonicDistance + "," + ultrasonicState);

        if (ultrasonicState == 1) {//move forward
            if (robot.valveServo.getPosition() == 0.5) {
                ultrasonicState = 2;
            } else {
                robot.valveServo.setPosition(robot.valveServo.getPosition() - .125);
            }
        } else if (ultrasonicState == 2) {//reset
            if (robot.valveServo.getPosition() == 1) {
                ultrasonicState = 0;
            } else {
                robot.valveServo.setPosition(robot.valveServo.getPosition() + .125);
            }
        } else if (ultrasonicDistance < 10 || ultrasonicDistance > 14) {
            robot.valveServo.setPosition(robot.valveOpen);
            ultrasonicState = 0;
        } else if (ultrasonicDistance > 9 && ultrasonicDistance <= 14) {
            robot.valveServo.setPosition(robot.valveClose);
            ultrasonicState = 1;
        }
    }

    private void buttonControl() {

        joy1.update(gamepad1);

        if (gamepad1.x) {
            robot.beaconServo.setPosition(robot.beaconLeft);
        } else if (gamepad1.b) {
            robot.beaconServo.setPosition(robot.beaconRight);
        } else if (gamepad1.a) {
            robot.beaconServo.setPosition(robot.beaconMiddle);
        }


        if (gamepad1.dpad_left) {
            robot.sweeper.setPower(0.03);
        } else if (joy1.toggle.y) {
            robot.sweeper.setPower(-0.4);
        } else if (gamepad2.left_trigger > 0.5) {
            robot.sweeper.setPower(0.03);
        } else if (gamepad2.right_trigger > 0.5) {
            robot.sweeper.setPower(-0.03);
        } else {
            robot.sweeper.setPower(0);
        }


        /* if (gamepad1.left_trigger == 1) {
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

        if (gamepad1.left_trigger == 1) {
            robot.linear.setPower(-1);
        } else if (gamepad1.right_trigger == 1) {
            robot.linear.setPower(1);
        } else {
            robot.linear.setPower(0);
        }

        if (gamepad1.right_bumper && gamepad1.left_bumper) {
            if (robot.releaseServo.getPosition() == robot.releaseClosed) {
                robot.releaseServo.setPosition(robot.releaseOpen);
            } else {
                robot.releaseServo.setPosition(robot.releaseClosed);
            }
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
