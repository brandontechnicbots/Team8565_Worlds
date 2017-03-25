package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "RedShootOnlyCenter", group = "Linear Opmode")
public class SSRAlternateAuto_RedCenter extends SSRAlternateAuto {
    @Override
    protected int getDelay() {
        return 000;
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
