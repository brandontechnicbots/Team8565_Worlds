package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "SHOOT ONLY: Blue CORNER", group = "Alternate Auto")
public class SSRAlternateAuto_BlueCorner extends SSRAlternateAuto {
    @Override
    protected int getDelay() {
        return 15000;
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
