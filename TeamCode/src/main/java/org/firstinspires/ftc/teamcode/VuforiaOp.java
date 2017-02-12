package org.firstinspires.ftc.teamcode;

import android.annotation.TargetApi;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;

import static android.R.attr.left;
import static android.R.attr.right;
import static android.R.attr.rotation;

/**
 * Created by Samuel on 1/20/2017.
 */
@Autonomous(name="Vuforia Op", group ="Linear Opmode")

public class VuforiaOp extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor leftMotor = hardwareMap.dcMotor.get("left");
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        DcMotor rightMotor = hardwareMap.dcMotor.get("right");
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "ASYmU1X/////AAAAGeRbXZz3301OjdKqrFOt4OVPb5SKSng95X7hatnoDNuElPjQYMuihKQH5o9PC1jUJXk8lD12tefsfHj1PDgB6ga2gfL08UI3WL62Fov/j8rDLTMqKqBOP+jljOXhePm0stYgsK2+aSVaOIHmpY84uZHQ4pNExqGfkESerC6Nz1BZvDO/9zumPcCF98CjmcaGBGv4va7Kjd7XEQRBt0p+j+PAa9wYXywulvmqVnWTfh3fGiVWotAhI8jmzdxRAwTcutcl9CIBulmPa8/cGI3dGKXkKiXyR62gkgPOtLriz8lOzxwnyLC5vWPrr1MqbX5TRfrls3IQdQyfvPrtWnqirdtsWQ7m0eTNSC1/J1flxeaW";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4); //Sets possible errors

        VuforiaTrackables beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Lego");
        beacons.get(3).setName("Gears");

        VuforiaTrackableDefaultListener tools = (VuforiaTrackableDefaultListener) beacons.get(1).getListener();

        waitForStart();
        beacons.activate(); //When Vuforia starts tracking objects

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        leftMotor.setPower(0.01);
        rightMotor.setPower(0.01);

        while(opModeIsActive() && tools.getRawPose() == null) {
            idle();
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);

        //analyze beacon

        VectorF angles = anglesFromTarget(tools);

        VectorF trans = navOffWall(tools.getPose().getTranslation(), Math.toDegrees(angles.get(0)) - 90, new VectorF(500, 0, 0));

        boolean movingLeft = false, movingRight = false;
        Log.d("Angle from beacon: ", angles.toString());
        Log.d("Trans: ", trans.toString());
        telemetry.addData("Angle from beacon: ", angles.toString());

      /*  if(trans.get(0) > 0)
        {
            leftMotor.setPower(0.01);
            rightMotor.setPower(-0.01);
        } else {
            movingRight = true;
            leftMotor.setPower(-0.01);
            rightMotor.setPower(0.01);
            movingLeft = true;
        }

        do{
            if(tools.getPose() != null)
            {
                trans = navOffWall(tools.getPose().getTranslation(), Math.toDegrees(angles.get(0)) - 90, new VectorF(500, 0, 0));
            } else {
                leftMotor.setPower(0);
                rightMotor.setPower(0);
                Log.d("Angle from beacon: ", angles.toString());
                telemetry.addData("Angle from beacon: ", angles.toString());
                while (tools.getPose() == null)
                {
                    if (movingRight)
                    {
                        leftMotor.setPower(-0.01);
                        rightMotor.setPower(0.01);
                    } else if (movingLeft){
                        leftMotor.setPower(0.01);
                        rightMotor.setPower(-0.01);
                    }
                }
                leftMotor.setPower(0);
                rightMotor.setPower(0);

                Log.d("Angle from beacon: ", angles.toString());
                telemetry.addData("Angle from beacon: ", angles.toString());
                telemetry.addData("detected, " ,leftMotor.getPower());

                trans = navOffWall(tools.getPose().getTranslation(), Math.toDegrees(angles.get(0)) - 90, new VectorF(500, 0, 0));
                break;
            }
            idle();
        } while (opModeIsActive() && Math.abs(trans.get(0)) > 30);

        leftMotor.setPower(0);
        rightMotor.setPower(0);

        leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftMotor.setTargetPosition((int) (leftMotor.getCurrentPosition() + ((Math.hypot(trans.get(0), trans.get(2)) + 150) / 409.575 * 1120))); //first int is phone distance, econd int is wheel circum, third int is ticks per sec
        rightMotor.setTargetPosition((int) (rightMotor.getCurrentPosition() + ((Math.hypot(trans.get(0), trans.get(2)) + 150) / 409.575 * 1120)));

        leftMotor.setPower(0.01);
        rightMotor.setPower(0.01);

        while(opModeIsActive() && leftMotor.isBusy() && rightMotor.isBusy()){
            idle();
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        while(opModeIsActive() && (tools.getPose() == null || Math.abs(tools.getPose().getTranslation().get(0)) > 10))
        {
            if(tools.getPose() != null)
            {
                if(tools.getPose().getTranslation().get(0) > 0)
                {
                    leftMotor.setPower(-0.01);
                    rightMotor.setPower(0.01);
                } else {
                    leftMotor.setPower(0.01);
                    rightMotor.setPower(-0.01);
                }
            } else {
                leftMotor.setPower(-0.01);
                rightMotor.setPower(0.01);
            }
        }
*/
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }



        public VectorF navOffWall (VectorF trans, double robotAngle, VectorF offWall)
        {
            return new VectorF((float) (trans.get(0) - offWall.get(0) * Math.sin(Math.toRadians(robotAngle)) - offWall.get(2) *
            Math.cos(Math.toRadians(robotAngle))), trans.get(1), (float) (trans.get(2) + offWall.get(0) *
            Math.cos(Math.toRadians(robotAngle)) - offWall.get(2) * Math.sin(Math.toRadians(robotAngle))));
        }

        public VectorF anglesFromTarget(VuforiaTrackableDefaultListener image) {
            float[] data = image.getRawPose().getData();
            float[][] rotation = {{data[0], data[1]}, {data[4], data[5], data[6]}, {data[8],
                    data[9], data[10]}};
            telemetry.addData("rotation: ", rotation);

            double thetaX = Math.atan2(rotation[2][1], rotation[2][2]);
            double thetaY = Math.atan2(-rotation[2][0], Math.sqrt(rotation[2][1] * rotation[2][1] +
            rotation[2][2] * rotation[2][2]));
            double thetaZ = Math.atan2(rotation[1][0], rotation[0][0]);
            return new VectorF((float)thetaX, (float)thetaY, (float)thetaZ);
        }
        /*
        while(opModeIsActive())
        {
            for (VuforiaTrackable beac : beacons)
            {
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beac.getListener()).getPose();

                if (pose != null) //If they see the object
                {
                    VectorF translation = pose.getTranslation();

                    telemetry.addData(beac.getName() + "-Translation", translation);

                    double degreesToTurn = Math.toDegrees(Math.atan2(translation.get(2), translation.get(0))); //Y-axis, Zed Axis

                    telemetry.addData(beac.getName() + "-Degrees", degreesToTurn);

                }
            }
            telemetry.update();
        } */
    }
