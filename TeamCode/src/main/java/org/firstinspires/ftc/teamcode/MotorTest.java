package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Motor Test", group = "Linear Opmode")
public class MotorTest extends SSRAuto {

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        robot.gyroSensor.calibrate();

        telemetry.addData("Status", "Wait For Start/Gyro Calibration");
        telemetry.update();
        waitForStart();
        while (robot.gyroSensor.isCalibrating())
            robotSleep(200); //Wait for Gyro to finish calibrating

        telemetry.addData("PHASE 1-Motors/Gyro", "CHECK TELEMETRY");
        telemetry.update();
        encoderGyroDrive(1500, 0.3);
        encoderGyroDrive(1500, -0.3);
        insertGamepadBreakpont();
        gyroTurn(-90);
        insertGamepadBreakpont();
        gyroTurn(180);
        insertGamepadBreakpont();
        gyroTurn(-90);
        insertGamepadBreakpont();

        telemetry.addData("PHASE 2-SERVOS", "CHECK TELEMETRY");
        telemetry.update();

        robot.beaconServo.setPosition(robot.beaconRight);
        robot.releaseServo.setPosition(robot.releaseOpen);
        telemetry.addData("Servos", robot.beaconServo.getPosition() + ", "
                );
        robotSleep(2000);

        robot.beaconServo.setPosition(robot.beaconLeft);
        robot.releaseServo.setPosition(robot.releaseClosed);
        robotSleep(2000);

    }

    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected Boolean getRedAlliance() {
        return false;
    }

    @Override
    protected Boolean getCorner() {
        return false;
    }
}