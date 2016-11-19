package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Samuel on 11/16/2016.
 */
    @TeleOp(name="TRR", group="Iterative Opmode")
    public class TRRTeleOp extends OpMode {

        DcMotor rightMotor, leftMotor;
        DcMotor liftX, liftY, liftZ, harvesterMotor;

        Servo harvesterServo, rotateServo, releaseServo, hangServo, guardServo;

        boolean init = false; //Hardware init
        boolean reset_state = false; //ResetConfig()
        boolean release_state = false;
        boolean hang_state = false; //Hang Mode
        boolean dpadDown = false;
        int liftYTarget = 0; //Up/Down Control
        double downTarget = 0;
        double rotatePos = 0.945;
        final int INITIAL = 80;
        final int INCREMENT = 80;
        int subtraction = INITIAL;

        double throttle, rightThrottle, secondThrottle, secondRightThrottle; //Joysticks

        public void init() {
            init = false;
        }

        public void hardwareConfig() {
            if (!init) {
                //HARDWARE MAP
                leftMotor = hardwareMap.dcMotor.get("Left");
                rightMotor = hardwareMap.dcMotor.get("Right");
                liftX = hardwareMap.dcMotor.get("LR");
                liftY = hardwareMap.dcMotor.get("Vertical");
                liftZ = hardwareMap.dcMotor.get("Extend");
                //hangMotor = hardwareMap.dcMotor.get("Hang");
                harvesterMotor = hardwareMap.dcMotor.get("Sweeper");

                harvesterServo = hardwareMap.servo.get("Turn");
                rotateServo = hardwareMap.servo.get("Rotate");
                releaseServo = hardwareMap.servo.get("Release");
                hangServo = hardwareMap.servo.get("Hanging");
                guardServo = hardwareMap.servo.get("Guard");

                rightMotor.setDirection(DcMotor.Direction.REVERSE);

                liftY.setDirection(DcMotor.Direction.REVERSE);

                //SERVO INITIALIZATION
                rotateServo.setPosition(0.945);
                harvesterServo.setPosition(0.177);
                releaseServo.setPosition(0.02);
                hangServo.setPosition(0.054);
                guardServo.setPosition(0.7);

                init = true;
            }
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

            //if (Math.abs(throttle) < .05 && Math.abs(rightThrottle) < .05) {  //1st driver not moving
            //    leftMotor.setPower(secondThrottle);
            //    rightMotor.setPower(secondRightThrottle);
            //} else {
            leftMotor.setPower(throttle);
            rightMotor.setPower(rightThrottle);
            //}
        }

        public void buttonControl() {
            if (!reset_state) {
                if (gamepad1.x) {//LR motor
                    liftX.setPower(0.1);
                    //rotateServo.setPosition(0.6);
                    rotatePos = 0.6;
                } else if (gamepad1.b) {//LR motor
                    liftX.setPower(-0.3);
                    //rotateServo.setPosition(0.2);
                    rotatePos = 0.2;
                } else liftX.setPower(0);
                setRotateServo(); //Always called
                if (gamepad1.y) //Dump box
                {
                    releaseServo.setPosition(0.5);
                    if (!release_state) {
                        downTarget = subtraction;
                        subtraction += INCREMENT;
                        release_state = true;
                    }
                }

                if (gamepad2.dpad_up)
                    liftYTarget = liftY.getCurrentPosition() + 200;
                else if (gamepad2.dpad_down)
                    liftYTarget = liftY.getCurrentPosition() - 200;

                if (gamepad1.left_trigger == 1) //Linear Motor shrinks
                    liftZ.setPower(-0.8);
                else if (gamepad1.right_trigger == 1) { //Linear Motor extends
                    liftZ.setPower(0.9);
                    if (liftY.getCurrentPosition() > 2900) liftYTarget = 5150;
                } else if (gamepad2.y)
                    liftZ.setPower(0.6);
                else if (gamepad2.a)
                    liftZ.setPower(-0.6);
                else
                    liftZ.setPower(0);

                if (gamepad1.left_bumper && gamepad1.right_bumper)
                    harvesterMotor.setPower(0);
                else if (gamepad1.right_bumper) //Turn Sweeper on and off
                    harvesterMotor.setPower(-0.1);
                else if (gamepad1.left_bumper)
                    harvesterMotor.setPower(0.2);
                else if (gamepad2.right_trigger == 1)
                    harvesterMotor.setPower(-0.05);
                else if (harvesterMotor.getPower() == -0.05)
                    harvesterMotor.setPower(0);

                if (gamepad1.dpad_down) { //All the way down
                    guardServo.setPosition(0.7);
                    if (liftZ.getCurrentPosition() >= 0)
                        liftZ.setPower(-0.5);
                    liftYTarget = 0;
                    harvesterServo.setPosition(0.177);
                } else if (gamepad1.dpad_up) { //Mid stage
                    guardServo.setPosition(0);
                    liftYTarget = 3000;
                    harvesterServo.setPosition(0.38);
                }
                liftYControl(liftYTarget);
            }
        }

        public void resetControl() {
            if (reset_state) { //If resetting block other actions
                rotateServo.setPosition(0.945);
                rotatePos = 0.945; //Just in case
                releaseServo.setPosition(0);
                liftYTarget = 3000;
                liftYControl(liftYTarget);
                //Move the x and z motor back to encoder 0
                //x motor should move between +25 and -25 of the target range
                if (liftX.getCurrentPosition() > 25)
                    liftX.setPower(-0.25);
                else if (liftX.getCurrentPosition() < -25)
                    liftX.setPower(0.25);
                else
                    liftX.setPower(0);

                ///Linear motor should move between +25 and -25 of the target range
                if (liftZ.getCurrentPosition() > downTarget)
                    liftZ.setPower(-0.1);
                else if (liftZ.getCurrentPosition() < -50)
                    liftZ.setPower(0.2);
                else
                    liftZ.setPower(0);

                //Clear reset state after linear motor stops
                //if ((Math.abs(liftX.getCurrentPosition()) < 25)
                //        && (liftY.getCurrentPosition() < 3000)) {
                if (liftZ.getCurrentPosition() < downTarget) {
                    reset_state = false;
                    release_state = false;
                    //downTarget = liftZ.getCurrentPosition();
                }
            }
        }

        public void liftYControl(int p) {
            telemetry.addData("Y target:", p);
            if (liftY.getCurrentPosition() < p - 50)
                liftY.setPower(0.7);
            else if (liftY.getCurrentPosition() > p + 50)
                liftY.setPower(-0.7);
            else liftY.setPower(0);
        }

        public void setRotateServo() {
            if (rotateServo.getPosition() < rotatePos)
                rotateServo.setPosition(Range.clip(rotateServo.getPosition() + 0.01, 0, 1));
            else if (rotateServo.getPosition() > rotatePos)
                rotateServo.setPosition(Range.clip(rotateServo.getPosition() - 0.01, 0, 1));
        }

        public void loop() {
            hardwareConfig();
            if (gamepad1.a) //RESET
                reset_state = true;
            resetControl();
            buttonControl();
            driveControl();
            log();
        }

        private void stopRobot() {
            //leftMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            //leftMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            leftMotor.setPower(0);
            rightMotor.setPower(0);
        }

        public void stop() {
            //stopRobot();
        }

        public void log() {
            //telemetry.addData("X Distance:", liftX.getCurrentPosition());
            //telemetry.addData("Y Distance:", liftY.getCurrentPosition());
            telemetry.addData("Z Distance:", liftZ.getCurrentPosition());
            telemetry.addData("down target:", downTarget);
            telemetry.addData("RESET: " + String.valueOf(reset_state), "HANG: " + String.valueOf(hang_state));
        }

    }

