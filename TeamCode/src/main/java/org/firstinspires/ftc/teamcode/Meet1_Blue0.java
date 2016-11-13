package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Blue0", group = "Linear Opmode")
public class Meet1_Blue0 extends Meet1_Auto {
    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected Boolean getRedAlliance() {
        return false;
    }

}
