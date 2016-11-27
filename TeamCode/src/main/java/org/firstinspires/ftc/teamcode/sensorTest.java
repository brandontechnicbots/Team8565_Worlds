package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsAnalogOpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@TeleOp(name = "Calibration", group = "Iterative Opmode")
public class sensorTest extends Meet1_Teleop {
    double whiteValue, matValue;

    @Override
    public void init() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        robot.gyroSensor.calibrate();

    }

    @Override
    public void loop() {
        if (!robot.gyroSensor.isCalibrating()) {
            driveControl();
            telemetry.addData("Y=Set White", "A=Set Mat, X=Save to file");
            telemetry.addData("Color: Red, Blue", robot.colorSensor.red() + " " + robot.colorSensor.blue() + ", GYRO: " + robot.gyroSensor.getIntegratedZValue());
            telemetry.addData("whiteValue", Double.toString(whiteValue) + ", matValue: " + Double.toString(matValue));
            telemetry.addData("Encoders(L,R): ", robot.leftMotor.getCurrentPosition() + " " + robot.rightMotor.getCurrentPosition());
            if (gamepad1.y) { //White
                whiteValue = robot.lightSensor.getLightDetected();
            }
            if (gamepad1.a) { //Black
                matValue = robot.lightSensor.getLightDetected();
            }
            if (gamepad1.x) { //Save
                writeCalibration();
            }
        } else {
            telemetry.addData("Status: ", "Gyro Updating");
        }
    }


    public void writeCalibration() {
        if (whiteValue > 0 && matValue > 0) {
            try {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/FIRST/calibration.txt");
                FileOutputStream fileoutput = new FileOutputStream(file);
                PrintStream ps = new PrintStream(fileoutput);
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                ps.println(date);
                ps.println(whiteValue);
                ps.println(matValue);

                ps.close();
                fileoutput.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
