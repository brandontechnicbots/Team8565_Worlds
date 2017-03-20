package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Linear", group = "Iterative Opmode")
public class ServoControl extends SSRTeleop {

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
    }

    @Override
    public void loop() {
        servoControl(robot.valveServo);
        if (gamepad1.right_trigger == 1) {
            robot.linear.setPower(1);
        } else if (gamepad1.left_trigger == 1) {
            robot.linear.setPower(-1);
        } else {
            robot.linear.setPower(0);
        }
        if (gamepad1.y)
        {
            robot.sweeper.setPower(0.1);
        } else {
            robot.sweeper.setPower(0);
        }
        newDriveControl();

    }

    @Override
    public void stop() {
    }


    public void servoControl(Servo s) {
        if (gamepad1.x)
            s.setPosition(0.5);
        if (gamepad1.b)
            s.setPosition(Range.clip(s.getPosition() + 0.002, 0, 1));
        else if (gamepad1.a)
            s.setPosition(Range.clip(s.getPosition() - 0.002, 0, 1));
        telemetry.addData("Servo Pos:", s.getPosition());
        telemetry.addData("Instructions:", "X=0.5,B=+0.002,A=-0.002");

    }

}