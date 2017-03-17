package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BlueBeaconCenter", group = "Linear Opmode")
public class SSRAuto_BlueBeaconCenter extends SSRAuto {
    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected Boolean getRedAlliance() {
        return false;
    }

    @Override
    protected Boolean getCorner() {
        return false;
    }
}
