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
        insertGamepadBreakpont();
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

        robot.beaconServo.setPosition(0.98);
        //robot.capServo.setPosition(0.45);
        telemetry.addData("Servos", robot.beaconServo.getPosition() + ", "
                );
        robotSleep(2000);

        robot.beaconServo.setPosition(0.22);
        //robot.capServo.setPosition(0.02);

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
}