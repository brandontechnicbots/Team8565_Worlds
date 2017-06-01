package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Shooter Bot Teleop", group = "Shooterbot")  // @Autonomous(...) is the other common choice
public class ShooterRobotTeleop extends YRFTeleop {
    private ElapsedTime runtime = new ElapsedTime();
    ShooterRobot robot = new ShooterRobot(); // Get Robot Config.
    GamepadWrapper joy1 = new GamepadWrapper();

    double throttle, rightThrottle;

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
        telemetry.addData("Status", "Running: " + runtime.toString());

    }

    @Override
    public void stop() {
    }



}
