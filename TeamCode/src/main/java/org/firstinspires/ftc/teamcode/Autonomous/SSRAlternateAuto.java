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
        shootBalls();
        endNavigation();
    }

    private void navigateToBeacon() {
        if (getRedAlliance()) {
            encoderGyroDrive(670, 0.4); //forward
            gyroTurn(71); //turn left
            encoderGyroDrive(2450, 0.4);
        } else {
            encoderGyroDrive(900, -0.4); //forward
            gyroTurn(-72); //turn left
            encoderGyroDrive(1200, -0.4);
        }
        robotSleep(500);

    }

    private void endNavigation() {
        if (getRedAlliance()) {

            if (getCorner()) {
                encoderGyroDrive(2200, 0.3); //to ramp
            } else {
                encoderGyroDrive(1650, -0.4);
                gyroTurn(-40); //turn left
                encoderGyroDrive(2400, 0.4);
            }

        } else {

            if (getCorner()) {
                encoderGyroDrive(2200, -0.3); //to ramp
            } else {
                encoderGyroDrive(2000, 0.3);
                gyroTurn(33); //turn left
                encoderGyroDrive(2400, -0.3);
            }
        }
    }

}
