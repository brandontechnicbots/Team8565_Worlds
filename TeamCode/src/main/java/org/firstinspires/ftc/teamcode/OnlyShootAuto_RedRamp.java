package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "OnlyShoot_RedRamp", group = "Linear Opmode")
public class OnlyShootAuto_RedRamp extends OnlyShootAuto {
    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected Boolean getRedAlliance() {
        return true;
    }

    @Override
    protected Boolean getShootingEndOnRamp() { return true;}
}
