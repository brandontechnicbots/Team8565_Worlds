package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Meet1_Auto;

@Autonomous(name = "Red0", group = "Linear Opmode")
public class Meet1_Red0 extends Meet1_Auto {
    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected int getRedAlliance() {
        return 1;
    }

}
