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
        shootBalls();
        endShootNavigation();
    }

    private void navigateToShoot() {
        if (getRedAlliance()) {

        } else {
            encoderGyroDrive(500, 0.5);
            insertGamepadBreakpont();
            gyroTurn(90);
            insertGamepadBreakpont();
            encoderGyroDrive(1500, -0.5);
            insertGamepadBreakpont();
        }
    }

    private void endShootNavigation() {
        if (getRedAlliance()) {
            if (getShootingEndOnRamp()) {

            } else {

            }

        } else {
            if (getShootingEndOnRamp()) {
                insertGamepadBreakpont();
                encoderGyroDrive(800, -0.5);
            } else {
                insertGamepadBreakpont();
                gyroTurn(-60);
                encoderGyroDrive(1800, 0.5);
            }
        }

    }

    abstract protected Boolean getShootingEndOnRamp();
}
