package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "OnlyShoot_BlueCenter", group = "Linear Opmode")
public class OnlyShootAuto_BlueCenter extends OnlyShootAuto {
    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected Boolean getRedAlliance() {
        return false;
    }

    @Override
    protected Boolean getShootingEndOnRamp() { return false;}
}
