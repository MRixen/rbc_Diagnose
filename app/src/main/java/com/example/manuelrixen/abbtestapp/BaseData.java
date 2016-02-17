package com.example.manuelrixen.abbtestapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.example.manuelrixen.abbtestapp.Socket.Receiver;

import java.io.Serializable;

/**
 * Created by Manuel.Rixen on 27.08.2015.
 */
public class BaseData implements Serializable {
    public static Handler sendToVisualization = null;
    private Context context;
    private Activity activity;
    private Receiver receiver;

    public BaseData(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void startReceiver(String ip, String port, int applicationMode) {
        this.receiver = new Receiver(context, ip, port, activity, applicationMode);
        Thread rThread = new Thread(receiver);
        rThread.start();
    }

    public Receiver getReceiver() {
        return this.receiver;
    }
}
