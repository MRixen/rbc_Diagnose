package com.example.manuelrixen.abbtestapp;

import android.app.Dialog;
import android.app.TabActivity;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.Socket.Receiver;

import java.io.Serializable;

/**
 * Created by Manuel.Rixen on 27.08.2015.
 */
public class BaseData implements Serializable{
    public static Handler sendToVisualization = null;
    private final Context context;
    public Receiver.EventListener eventListener = null;
    private Receiver receiver;

    public BaseData(Context context){
        this.context = context;
    }

    public void startReceiver(String ip, String port) {
        this.receiver = new Receiver(context, ip, port);
        Thread rThread = new Thread(receiver);
        rThread.start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Receiver getReceiver(){
        return this.receiver;
    }
}
