package com.example.manuelrixen.abbtestapp.Socket;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * This class set the events to send data on the topic with a specific frequency
 */
public class Receiver implements Runnable {


    private final Context context;
    private boolean isRunning = true;

    private NetClient nc;
    private String data[] = new String[]{"", ""};
    private Activity activity = new Activity();
    private ArrayList<EventListener> listeners = new ArrayList<EventListener>();

    public Receiver(Context context, String ip, String port) {
        this.context = context;
        if (nc == null) nc = new NetClient(ip, Integer.parseInt(port));
    }

    public void stopRunRoutine() {
        isRunning = false;
    }

    public void run() {
        if (nc.connectWithServer()) {

            while (isRunning) {
                data = nc.receiveDataFromServer();
                if ((data[0] != null) && (data[1] != null)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (EventListener eventListener : listeners) eventListener.onEvent(data[0], data[1]);
                            } catch (NullPointerException e) {
                                Log.d("Exception:run", String.valueOf(e));
                            }
                        }
                    });
                }
            }
            nc.disConnectWithServer();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Can't connect to server.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface EventListener {
        void onEvent(String data1, String data2);
        void onError();
    }

    public void registerListener (EventListener listener){
        this.listeners.add(listener);
    }

}
