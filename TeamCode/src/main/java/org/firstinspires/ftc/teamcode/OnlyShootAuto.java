package org.firstinspires.ftc.teamcode;

//@Autonomous(name = "Autonomous", group = "Linear Opmode")
abstract public class OnlyShootAuto extends Meet1Auto {

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.gyroSensor.calibrate();

        telemetry.addData("Status", "Wait For Start");
        telemetry.addData("DELAY(ms):", Integer.toString(getDelay()) + ",Red Alliance:" + getRedAlliance()
                            + ",Ramp End:"+ getShootingEndOnRamp());
        telemetry.update();
        idle();
        waitForStart();
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

        navigateToShoot();
        shootBalls(false);
        endShootNavigation();
    }

    /*
                gyroTurn(-60,0,1);
            robot.shooter.setPower(0.75); //turn on shooter
            robotSleep(600);
            robot.shooter.setPower(0);
            gyroTurn(-77);
            robotSleep(10000);
            encoderGyroDrive(2000,0.4);
     */
    private void navigateToShoot() {
        if (getRedAlliance()) {
            encoderGyroDrive(200, 0.5);
            gyroTurn(90);
            encoderGyroDrive(950, 0.5);
        } else {
            encoderGyroDrive(200, 0.5);
            gyroTurn(90);
            encoderGyroDrive(950, -0.5);
        }
    }

    private void endShootNavigation() {
        if (getRedAlliance()) {
            if (getShootingEndOnRamp()) {
                gyroTurn(-20);
                encoderGyroDrive(1350, 0.5);
            } else {

            }

        } else {
            if (getShootingEndOnRamp()) {
                gyroTurn(20,1,0);
                encoderGyroDrive(1450, -0.5);
            } else {
                gyroTurn(-60);
                encoderGyroDrive(1800, 0.5);
            }
        }

    }

    abstract protected Boolean getShootingEndOnRamp();
}
