package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Motor Test", group = "Linear Opmode")
public class MotorTest extends Meet1Auto {

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
        encoderGyroDrive(500, 0.3);
        encoderGyroDrive(500, -0.3);
        gyroTurn(-90);
        gyroTurn(180);
        gyroTurn(-90);

        telemetry.addData("PHASE 2-SERVOS", "CHECK TELEMETRY");
        telemetry.update();

        robot.frontServo.setPosition(0.8);
        robot.backServo.setPosition(0.8);
        robot.leftClaw.setPosition(0.586);
        robot.rightClaw.setPosition(0.42);
        telemetry.addData("Servos", robot.frontServo.getPosition() + ", " +
                robot.backServo.getPosition() + ", " +
                robot.leftClaw.getPosition() + ", " +
                robot.rightClaw.getPosition());
        robotSleep(2000);

        robot.frontServo.setPosition(0.1);
        robot.backServo.setPosition(0.1);
        robot.leftClaw.setPosition(0.426);
        robot.rightClaw.setPosition(0.58);

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