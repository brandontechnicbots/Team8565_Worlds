package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BlueShootOnlyCenter", group = "Linear Opmode")
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
