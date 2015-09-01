package com.example.manuelrixen.abbtestapp.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manuelrixen.abbtestapp.Tabs.CycleTime;
import com.example.manuelrixen.abbtestapp.Tabs.Logging;
import com.example.manuelrixen.abbtestapp.Tabs.MachineData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.sql.Time;

/*
 * This class set the events to send data on the topic with a specific frequency
 */
public class Receiver implements Runnable {

    private final int eventListenerNumber;
    private final Context context;
    private boolean isRunning = true;
    private FirstEventListener cycleTime_firstEventListener;
    private FirstEventListener machineData_firstEventListener;
    private FirstEventListener logging_firstEventListener;
    private SecondEventListener cycleTime_secondEventListener;

    private NetClient nc;
    private String data[] = new String[] {"", ""};
    private Activity activity = new Activity();
    private boolean normal;

    public Receiver(Context context, String ip, String port, boolean normal, int eventListenerNumber, CycleTime cycleTime, MachineData machineData, Logging logging) {
        this.normal = normal;
        this.context = context;
        this.cycleTime_firstEventListener = cycleTime.getFirstEventListener();
        this.cycleTime_secondEventListener = cycleTime.getSecondEventListener();
        this.machineData_firstEventListener = machineData.getFirstEventListener();
        this.logging_firstEventListener = logging.getFirstEventListener();

        this.eventListenerNumber = eventListenerNumber;
        nc = new NetClient(ip, Integer.parseInt(port));
    }

    public void stopRunRoutine() {
        isRunning = false;
    }

    public void run() {
        if(nc.connectWithServer()) {
            Log.d("connectWithServer", "client " + eventListenerNumber + "connected");
            while (isRunning) {
                data = nc.receiveDataFromServer();
                if ((data[0] != null) && (data[1] != null)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                switch (data[1]) {
                                    case "l":
                                        if (normal) {
                                            logging_firstEventListener.onEvent1(true, data[0], data[1]);
                                        } else {
                                            logging_firstEventListener.onEvent1(false, data[0], data[1]);
                                        }
                                        break;
                                    case "c":
                                        if (normal) {
                                            cycleTime_secondEventListener.onEvent2(true, data[0], data[1]);
                                        } else {
                                            cycleTime_secondEventListener.onEvent2(false, data[0], data[1]);
                                        }
                                        break;
                                    default:
                                        if (normal) {
                                            machineData_firstEventListener.onEvent1(true, data[0], data[1]);
                                        } else {
                                            machineData_firstEventListener.onEvent1(false, data[0], data[1]);
                                        }
                                        break;
                                }


                            }
                            catch(NullPointerException e){
                                Log.d("Exception:run", String.valueOf(e));
                                if (cycleTime_firstEventListener == null) Log.d("cycleTime_firstEventListener", "null");
                                if (cycleTime_secondEventListener == null) Log.d("cycleTime_secondEventListener", "null");
                            }
                        }
                    });
                }
            }
            nc.disConnectWithServer();
        }
        else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Can't connect to server.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface FirstEventListener {
        public void onError1();
        public void onEvent1(boolean normal, String data1, String data2);
    }

    public interface SecondEventListener {
        public void onError2();
        public void onEvent2(boolean normal, String data1, String data2);
    }
}
