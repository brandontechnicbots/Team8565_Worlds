package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BlueShootOnlyCenter", group = "Alternate Auto")
public class SSRAlternateAuto_BlueCenter extends SSRAlternateAuto {
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
