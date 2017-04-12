package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BlueShootOnlyCorner", group = "Alternate Auto")
public class SSRAlternateAuto_BlueCorner extends SSRAlternateAuto {
    @Override
    protected int getDelay() {
        return 8000;
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
