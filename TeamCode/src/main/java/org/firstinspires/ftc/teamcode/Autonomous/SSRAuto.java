package org.firstinspires.ftc.teamcode.Autonomous;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class SSRAuto extends BaseAutonomous {

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        loadTapeCalibration();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.gyroSensor.calibrate();

        while (!isStarted()) {
            telemetry.addData("Status", getRedAlliance() ? "RED alliance ready" : "BLUE alliance ready");
            telemetry.addData("DELAY(ms):", Integer.toString(getDelay()));
            telemetry.addData("Line Threshold", lineThreshold + ", Current: " + robot.lightSensor.getLightDetected());
            telemetry.addData("Gyro ", robot.gyroSensor.isCalibrating() ? "CALIBRATING" : robot.gyroSensor.getIntegratedZValue());
            telemetry.update();
            idle();
        }
        //waitForStart();
        resetStartTime();
        while (opModeIsActive()) { //Quick Hack to prevent errors (hopefully)
            if (!robot.gyroSensor.isCalibrating()) {
                break;
            }
        }
        robotSleep(500);

        telemetry.addData("InDelay", "yes");
        telemetry.update();

        navigateToBeacon();
        detectFirstLine();
        pushBeacon();
        detectSecondLine();
        pushBeacon();
        endNavigation();
    }

    private void navigateToBeacon() {
        if (getRedAlliance()) {
            gyroTurn(30, 0, 1);
            encoderGyroDrive(3400, 0.4); //go forward
            gyroTurn(-16, 1, 0); //2nd turn
            encoderOnlyDrive(1500, 0.4, 0.4); //go forward into wall
            detectLineForward();
            robotSleep(getDelay()); //do we need delay
            encoderOnlyDrive(200, 0.4, 0.4); //go forward into wall
            gyroTurn(-4);
            encoderOnlyDrive(400, -0.3, -0.3);
        } else {
            gyroTurn(-25, 0, 1);
            encoderGyroDrive(2900, -0.6);
            gyroTurn(12); //do a rough curve
            encoderGyroDrive(400, -0.6);
            gyroTurn(5);
            encoderOnlyDrive(200, -0.2, -0.24); //slower into the wall just in case
            encoderOnlyDrive(300, -0.3, -0.3);
            detectLineForward();
            robotSleep(getDelay());
            encoderOnlyDrive(150, -0.3, -0.3); //add stall protection??
            gyroTurn(3.5, 0, 1);
            //encoderOnlyDrive(200, 0.3, 0.3);
        }

    }

    private void detectLineForward() {
        if (getRedAlliance()) {
            robot.leftMotor.setPower(0.22);
            robot.rightMotor.setPower(0.24);
        } else {
            robot.leftMotor.setPower(-0.13);
            robot.rightMotor.setPower(-0.18);
        }
        while (opModeIsActive()) {
            //telemetry.addData("Light1", robot.lightSensor.getLightDetected());
            //telemetry.update();
            if (robot.lightSensor.getLightDetected() > lineThreshold) {
                robot.stopRobot();
                break;
            }
        }
    }

    private void detectFirstLine() {//Going Backwards
        if (getRedAlliance()) {
            robot.leftMotor.setPower(-0.13);
            robot.rightMotor.setPower(-0.18);
        } else {
            robot.leftMotor.setPower(0.13);
            robot.rightMotor.setPower(0.18);
        }
        while (opModeIsActive()) {
            //telemetry.addData("Light1", robot.lightSensor.getLightDetected());
            //telemetry.update();
            if (robot.lightSensor.getLightDetected() > lineThreshold) {
                robot.stopRobot();
                break;
            }
        }

        if (!getRedAlliance()) { //Compensation on blue only
            encoderGyroDrive(40, 0.3);
        } else { //Compensation on red only
            encoderGyroDrive(165, 0.15);
        }
    }

    private void detectSecondLine() { ///Going Backwards
        if (getRedAlliance()) {
            encoderGyroDrive(50, -0.3); //just so we don't detect the same line twice
            gyroTurn(-2.5, 0, 1);
            encoderOnlyDrive(800, -0.2, -0.25);
            detectFirstLine();
            encoderGyroDrive(40, 0.15);

        } else {
            encoderGyroDrive(50, 0.3); //just so we don't detect the same line twice
            gyroTurn(2.5, 0, 1);
            encoderOnlyDrive(800, 0.2, 0.25);
            detectFirstLine();
        }
    }

    private void pushBeacon() {
        int redTotal = 0;
        int blueTotal = 0;
        for (int i = 0; i < 2000; i++) { //Runs 100 times, tune this
            if (redTotal != 255 && blueTotal != 255) {
                redTotal += robot.colorSensor.red(); // Add to the values
                blueTotal += robot.colorSensor.blue();
            }
            telemetry.addData("Blue, Red", blueTotal + "," + redTotal);
            telemetry.update();
        }
        if (redTotal + blueTotal > 30) { //Only run if with readings
            if ((redTotal < blueTotal) ^ getRedAlliance()) { //XOR blue
                robot.beaconServo.setPosition(robot.beaconRight);
            } else if (redTotal != blueTotal) {
                robot.beaconServo.setPosition(robot.beaconLeft);
            }
            robotSleep(600);
            robot.beaconServo.setPosition(robot.beaconMiddle);
        }
    }

    private void endNavigation() {
        if (getRedAlliance()) {
            gyroTurn(45, 1, 0);
            encoderGyroDrive(1200, -0.4);
            //gyroTurn(21, 1, 0);
            robotSleep(1000);
            shootBalls();
            if (getCorner()) {
                gyroTurn(78);
                encoderGyroDrive(1600, 0.4);
            } else {
                gyroTurn(95);
                encoderGyroDrive(600, -0.4);
                gyroTurn(60);
                sleep(2000);
                gyroTurn(-60);
                encoderGyroDrive(400, -0.4);
            }

        } else { //blue side
            gyroTurn(63, 1, 0); //1st swt
            encoderGyroDrive(500,-0.3);
            gyroTurn(-29, 0, 1); //2nd swt
            robotSleep(750);
            shootBalls();
            if (getCorner()) {
                gyroTurn(-13, 1, 0);
                encoderGyroDrive(2500, 0.3);
            } else {
                gyroTurn(-85);
                encoderGyroDrive(1800, 0.3);
                encoderOnlyDrive(500, 0.3, 0.4);
            }
        }
    }

}
