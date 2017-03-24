package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BlueBeaconCorner", group = "Linear Opmode")
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
