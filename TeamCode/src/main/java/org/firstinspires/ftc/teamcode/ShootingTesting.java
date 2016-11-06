package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Jerry on 11/5/2016.
 */

@TeleOp(name = "ShootingTesting", group = "Iterative Opmode")

public class ShootingTesting extends OpMode{

    DcMotor leftMotor;
    DcMotor rightMotor;
//    DcMotor shootMotor;
Float throttle, secondThrottle, secondRightThrottle, rightThrottle;
//    String shooter = "off";


    @Override
    public void init() {
        //HARDWARE MAP
        leftMotor  = hardwareMap.dcMotor.get("left");
        rightMotor = hardwareMap.dcMotor.get("right");
//        shootMotor = hardwareMap.dcMotor.get("shoot");

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

/*      if (gamepad1.x) {
            shootMotor.setPower(0.5);
            shooter = "on";

        }
        if (gamepad1.y) {
            shootMotor.setPower(0.0);
            shooter = "off";
        }*/

//        telemetry.addData("Status", "Position: " + shooter);
        telemetry.addData("leftStickY", "Position: " + throttle);
        telemetry.addData("rightStickY", "Position: " + secondThrottle);


    }

}
