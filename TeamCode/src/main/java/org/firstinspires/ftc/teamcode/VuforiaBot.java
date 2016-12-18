package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Jerry on 11/5/2016.
 */

@TeleOp(name = "VuforiaBot", group = "Iterative Opmode")

public class VuforiaBot extends OpMode{

    DcMotor leftMotor;
    DcMotor rightMotor;
    DcMotor shootMotor;
    Float throttle, secondThrottle, secondRightThrottle, rightThrottle;
    String shooter = "off";
    Servo climbers;


    @Override
    public void init() {

        //HARDWARE MAP
        leftMotor  = hardwareMap.dcMotor.get("left");
        rightMotor = hardwareMap.dcMotor.get("right");
        shootMotor = hardwareMap.dcMotor.get("shootMotor");
        climbers = hardwareMap.servo.get("climbers");

        shootMotor.setDirection(DcMotor.Direction.FORWARD);
        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        //Driving and Joystick controls
        throttle = -1 * gamepad1.left_stick_y;
        rightThrottle = -1 * gamepad1.right_stick_y;
        secondThrottle = -1 * gamepad2.left_stick_y;
        secondRightThrottle = -1 * gamepad2.right_stick_y;

        //Dead zone
        throttle = (Math.abs(throttle) < 0.3) ? 0 : throttle;
        rightThrottle = (Math.abs(rightThrottle) < 0.05) ? 0 : rightThrottle;
        secondThrottle = (Math.abs(secondThrottle) < 0.3) ? 0 : secondThrottle;
        secondRightThrottle = (Math.abs(secondRightThrottle) < 0.05) ? 0 : secondRightThrottle;

        //Clip at 1
        throttle = Range.clip(throttle, -1, 1);
        rightThrottle = Range.clip(rightThrottle, -1, 1);
        secondThrottle = Range.clip(secondThrottle, -1, 1);
        secondRightThrottle = Range.clip(secondRightThrottle, -1, 1);

        leftMotor.setPower(-throttle);
        rightMotor.setPower(-rightThrottle);

        //Climber
        if (gamepad1.y)
            climbers.setPosition(1);
        else if (gamepad1.a)
            climbers.setPosition(0.55);

        //Shooter code
        if (gamepad1.left_trigger ==1) {
            shootMotor.setPower(1);
            shooter = "on";

        }
        if (gamepad1.right_trigger ==1) {
            shootMotor.setPower(0.0);
            shooter = "off";
        }

        //Telemetry
        telemetry.addData("Status", "Position: " + shooter);
        telemetry.addData("leftStickY", "Position: " + throttle);
        telemetry.addData("rightStickY", "Position: " + rightThrottle);


    }

}
