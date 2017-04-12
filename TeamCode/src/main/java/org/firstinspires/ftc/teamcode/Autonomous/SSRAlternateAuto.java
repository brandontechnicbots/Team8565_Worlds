package org.firstinspires.ftc.teamcode.Autonomous;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class SSRAlternateAuto extends BaseAutonomous {

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.gyroSensor.calibrate();

        while (!isStarted()) {
            telemetry.addData("Status", getRedAlliance() ? "RED alliance ready" : "BLUE alliance ready");
            telemetry.addData("DELAY(ms):", Integer.toString(getDelay()));
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
        robotSleep(getDelay()); //do we need delay

        navigateToBeacon();
        robotSleep(500);
        shootBalls();
        endNavigation();
    }

    private void navigateToBeacon() {
        if (getRedAlliance()) {
            encoderGyroDrive(270, 0.4); //forward
            gyroTurn(67); //turn left
            encoderGyroDrive(2100, 0.4);
        } else {
            encoderGyroDrive(400, -0.4); //forward
            gyroTurn(-62); //turn left
            encoderGyroDrive(2100, -0.4);
        }

    }

    private void endNavigation() {
        if (getRedAlliance()) {

            if (getCorner()) {
                encoderGyroDrive(2200, 0.3); //to ramp
            } else {
                gyroTurn(87); //turn left
                encoderGyroDrive(1600, -0.3);
                gyroTurn(25);
                robotSleep(1000);
                gyroTurn(-30);
                encoderGyroDrive(800, -0.4);
            }

        } else {

            if (getCorner()) {
                encoderGyroDrive(2200, -0.3); //to ramp
            } else {
                encoderGyroDrive(200, 0.3);
                gyroTurn(87); //turn left
                encoderGyroDrive(1500, -0.3);
            }
        }
    }

}
