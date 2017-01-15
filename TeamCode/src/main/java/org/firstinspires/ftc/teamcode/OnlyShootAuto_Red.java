package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "OnlyShoot_Red", group = "Linear Opmode")
public class OnlyShootAuto_Red extends OnlyShootAuto {
    @Override
    protected int getDelay() {
        return 10000;
    }

    @Override
    protected Boolean getRedAlliance() {
        return true;
    }

    @Override
    protected Boolean getShootingEndOnRamp() { return false;}
}
