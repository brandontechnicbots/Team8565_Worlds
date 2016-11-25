package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.util.Range;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PIDController {
    double Kp = .1;
    double Ki = .01;
    double Kd = 1;

    double integral = 0.0d;
    double dampen = 0.66;
    double lastError = 0.0d;

    String name = "";

    boolean debug = false;

    List<String> debugValues = new ArrayList<String>();

    public PIDController(String name, double Kp, double Ki, double Kd, double dampen) {
        this.name = name;
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.dampen = dampen;
    }

    public void setDebug(boolean d) {
        this.debug = d;
    }

    public double findCorrection(double error) {
        integral = dampen * integral + error;
        double derivative = error - lastError;

        double pTerm = Kp * error;
        double iTerm = Ki * integral;
        double dTerm = Kd * derivative;
        if (debug) {
            String debugString = new Date() + "," + error + "," + lastError + "," + integral + "," + derivative + "," + pTerm + "," + iTerm + "," + dTerm + "," + (pTerm + iTerm + dTerm);
            Log.i("debug : ", debugString);
            debugValues.add(debugString);
            appendFile(debugString);
        }
        lastError = error;

        return pTerm + iTerm + dTerm;
    }

    private void appendFile(String s) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        try {
            fw = new FileWriter("/sdcard/FIRST/" + name + ".txt", true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.println(s);
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    }

    public void writeToDebugFile() {
        //Write sensor values to file
        try {
            SimpleDateFormat f = new SimpleDateFormat("MMdd_HHmm");
            File file = new File("/sdcard/FIRST/debugFor" + name + "_" + f.format(new Date()) + ".txt");
            FileOutputStream fileoutput = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fileoutput);
            for (String s : debugValues) {
                ps.println(s);
            }
            ps.close();
            fileoutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
