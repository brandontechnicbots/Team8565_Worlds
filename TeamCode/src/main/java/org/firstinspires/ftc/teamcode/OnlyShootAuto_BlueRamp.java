package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//@Autonomous(name = "OnlyShoot_BlueRamp", group = "Linear Opmode")
public class OnlyShootAuto_BlueRamp extends OnlyShootAuto {
    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected Boolean getRedAlliance() {
        return false;
    }

    @Override
    protected Boolean getShootingEndOnRamp() { return true;}
}
