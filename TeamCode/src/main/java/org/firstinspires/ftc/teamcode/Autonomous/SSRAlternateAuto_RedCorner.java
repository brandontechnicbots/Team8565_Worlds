package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "RedShootOnlyCorner", group = "Alternate Auto")
public class SSRAlternateAuto_RedCorner extends SSRAlternateAuto {
    @Override
    protected int getDelay() {
        return 17000;
    }

    @Override
    protected Boolean getRedAlliance() {
        return true;
    }

    @Override
    protected Boolean getCorner() {
        return true;
    }

}
