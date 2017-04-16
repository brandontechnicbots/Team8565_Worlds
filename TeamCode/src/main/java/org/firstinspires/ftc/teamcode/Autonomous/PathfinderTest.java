package org.firstinspires.ftc.teamcode.Autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import java.io.File;
/*
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier; */

@Autonomous(name = "Pathfinder Test", group = "Testing")
@Disabled
public class PathfinderTest extends BaseAutonomous{

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        loadTapeCalibration();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.gyroSensor.calibrate();

        while (!isStarted()) {
            telemetry.addData("Status", getRedAlliance() ? "RED alliance ready" : "BLUE alliance ready");
            telemetry.addData("DELAY(ms):", Integer.toString(getDelay()));
            telemetry.addData("Line Threshold", lineThreshold + ", Current: " + robot.lightSensor.getLightDetected());
            telemetry.addData("Gyro ", robot.gyroSensor.isCalibrating() ? "CALIBRATING" : robot.gyroSensor.getIntegratedZValue());
            telemetry.update();
            idle();
        }
        //waitForStart();
        resetStartTime();
        while (opModeIsActive()) { //Quick Hack to prevent errors (hopefully)
            if (!robot.gyroSensor.isCalibrating()) {
                break;
            }
        }
        robotSleep(500);

        telemetry.addData("InDelay", "yes");
        telemetry.update();
        /*
        Waypoint[] points = new Waypoint[] {
                new Waypoint(-4, -1, Pathfinder.d2r(-45)),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
                new Waypoint(-2, -2, 0),                        // Waypoint @ x=-2, y=-2, exit angle=0 radians
                new Waypoint(0, 0, 0)                           // Waypoint @ x=0, y=0,   exit angle=0 radians
        };

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 1.7, 2.0, 60.0);
        Trajectory trajectory = Pathfinder.generate(points, config);
        TankModifier modifier = new TankModifier(trajectory).modify(0.5);

        EncoderFollower left = new EncoderFollower(modifier.getLeftTrajectory());
        EncoderFollower right = new EncoderFollower(modifier.getRightTrajectory());

        // Encoder Position is the current, cumulative position of your encoder. If you're using an SRX, this will be the
        // 'getEncPosition' function.
        // 1000 is the amount of encoder ticks per full revolution
        // Wheel Diameter is the diameter of your wheels (or pulley for a track system) in meters
        left.configureEncoder(robot.leftMotor.getCurrentPosition(), 1000, wheel_diameter);
        right.configureEncoder(robot.rightMotor.getCurrentPosition(), 1000, wheel_diameter);


        // The first argument is the proportional gain. Usually this will be quite high
        // The second argument is the integral gain. This is unused for motion profiling
        // The third argument is the derivative gain. Tweak this if you are unhappy with the tracking of the trajectory
        // The fourth argument is the velocity ratio. This is 1 over the maximum velocity you provided in the
        //      trajectory configuration (it translates m/s to a -1 to 1 scale that your motors can read)
        // The fifth argument is your acceleration gain. Tweak this if you want to get to a higher or lower speed quicker
        left.configurePIDVA(1.0, 0.0, 0.0, 1 / max_velocity, 0);
        right.configurePIDVA(1.0, 0.0, 0.0, 1 / max_velocity, 0);

        ///////////////////

        double l = left.calculate(robot.leftMotor.getCurrentPosition());
        double r = right.calculate(robot.rightMotor.getCurrentPosition());

        double gyro_heading = robot.gyroSensor.getHeading();    // Assuming the gyro is giving a value in degrees
        double desired_heading = Pathfinder.r2d(left.getHeading());  // Should also be in degrees

        double angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);
        double turn = 0.8 * (-1.0/80.0) * angleDifference;

        robot.leftMotor.setPower(l + turn);
        robot.rightMotor.setPower(r - turn);

        //write
        File myFile = new File("myfile.traj");
        Pathfinder.writeToFile(myFile, trajectory);
        File myFile = new File("myfile.csv");
        Pathfinder.writeToCSV(myFile, trajectory);
        //read
        File myFile = new File("myfile.traj");
        Trajectory trajectory = Pathfinder.readFromFile(myFile);
        File myFile = new File("myfile.csv");
        Trajectory trajectory = Pathfinder.readFromCSV(myFile);
        */
    }

    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected Boolean getRedAlliance() {
        return true;
    }

    @Override
    protected Boolean getCorner() {
        return false;
    }
}
