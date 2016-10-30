package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsAnalogOpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Autonomous(name = "sensors", group = "Linear Opmode")
 public class sensorTest extends LinearOpMode {

    ModernRoboticsAnalogOpticalDistanceSensor lightSensor;
    ModernRoboticsI2cColorSensor colorSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        lightSensor = (ModernRoboticsAnalogOpticalDistanceSensor) hardwareMap.opticalDistanceSensor.get("light");
        colorSensor = (ModernRoboticsI2cColorSensor) hardwareMap.colorSensor.get("color");

        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Light", lightSensor.getLightDetected());
            telemetry.addData("R, B", colorSensor.red()+ " "+colorSensor.blue());
            telemetry.update();
        }

    }
}
