package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BlueBeaconCorner", group = "Main Auto")
public class SSRAuto_BlueBeaconCorner extends SSRAuto {
    @Override
    protected int getDelay() {
        return 4000;
    }

    @Override
    protected Boolean getRedAlliance() {
        return false;
    }

    @Override
    protected Boolean getCorner() {
        return true;
    }
}
