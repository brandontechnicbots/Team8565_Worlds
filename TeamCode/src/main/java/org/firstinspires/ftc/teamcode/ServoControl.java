package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Servo Control", group = "Iterative Opmode")  // @Autonomous(...) is the other common choice
public class ServoControl extends Meet1_Teleop {
    private ElapsedTime runtime = new ElapsedTime();
    MainRobot robot = new MainRobot();   // Get Robot Config. HINT TO SAMUEL: Edit robot config in the MainRobot file.

    double throttle, secondThrottle, secondRightThrottle, rightThrottle;

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
//        driveControl();
        servoControl(robot.capServo);
        telemetry.addData("Status", "Running: " + runtime.toString());

    }

    @Override
    public void stop() {
    }


    public void servoControl(Servo s) {
        if (gamepad1.x)
            s.setPosition(0.5);
        if (gamepad1.y)
            s.setPosition(Range.clip(s.getPosition() + 0.002, 0, 1));
        else if (gamepad1.a)
            s.setPosition(Range.clip(s.getPosition() - 0.002, 0, 1));
        telemetry.addData("Servo Pos:", s.getPosition());
        telemetry.addData("Instructions:", "X=0.5,Y=+0.002,A=-0.002");

    }

}