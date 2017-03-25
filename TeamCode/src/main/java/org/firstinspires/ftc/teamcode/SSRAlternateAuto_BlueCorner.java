package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BlueShootOnlyCorner", group = "Linear Opmode")
public class SSRAlternateAuto_BlueCorner extends SSRAlternateAuto {
    @Override
    protected int getDelay() {
        return 7000;
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
