package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "RedBeaconCenter", group = "Linear Opmode")
public class SSRAuto_RedBeaconCenter extends SSRAuto {
    @Override
    protected int getDelay() {
        return 4000;
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
