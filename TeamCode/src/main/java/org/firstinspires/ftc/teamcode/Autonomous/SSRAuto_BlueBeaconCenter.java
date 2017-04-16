package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BlueBeaconCenter", group = "Main Auto")
public class SSRAuto_BlueBeaconCenter extends SSRAuto {
    @Override
    protected int getDelay() {
        return 000;
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
