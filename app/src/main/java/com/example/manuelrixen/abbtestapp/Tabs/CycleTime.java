package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

public class CycleTime extends Activity implements Receiver.EventListener, View.OnTouchListener, View.OnClickListener {

    private TextView cycleTimeViewer_actual, cycleTimeViewer_mean;
    private boolean dialogIsActive = false;
    private boolean firstStart = true;
    private Button clearButton;
    private RelativeLayout actLayout;
    private float[] actualTimeData = new float[16];
    private float[] meanTimeData = new float[16];
//    private CustomGraphDialog customGraphDialog;

    private BaseData baseData;
    private Receiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_cycletime);

        clearButton = (Button) findViewById(R.id.buttonClear);
        actLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        clearButton.setOnClickListener(this);
        cycleTimeViewer_actual = (TextView) findViewById(R.id.cycleTimeTextField_actual);
        cycleTimeViewer_actual.setOnTouchListener(this);
        cycleTimeViewer_mean = (TextView) findViewById(R.id.cycleTimeTextField_mean);
        cycleTimeViewer_mean.setOnTouchListener(this);

//        customGraphDialog = new CustomGraphDialog(this);

        Bundle bundle = getIntent().getExtras();
        baseData = (BaseData)bundle.getSerializable("baseData");

        if (receiver == null) {
            try {
                receiver = baseData.getReceiver();
                receiver.registerListener(this);
            }catch(NullPointerException e){}
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onEvent(String msg, String msgType) {
        showMessage(msgType, msg);
    }

    @Override
    public void onError() {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void showMessage(String msgType, String msg) {
        if (msgType.equals("c1")){
            String msgTemp = msg.replace(".", ",");
            cycleTimeViewer_actual.setText(msgTemp);
        }
        if (msgType.equals("c2")){
            String msgTemp = msg.replace(".", ",");
            cycleTimeViewer_mean.setText(msgTemp);
        }
        if (msgType.equals("c3")){
            String[] actualTimeDataTemp = msg.split("_");
            // TODO: Show mean and actual cycle time as graph
            for (int i=0;i<=actualTimeDataTemp.length-1;i++){
                actualTimeData[i] = Float.parseFloat(actualTimeDataTemp[i].replace(",", "."));
            }
            try {
//                if (customGraphDialog.getDialogState()) {
//                    Log.d("actualTimeData", String.valueOf(actualTimeData[0]));
//                    sendDataToNode(actualTimeData, "cycleTimeData");
//                }
            }
            catch(NullPointerException e){
                Log.d("Console:showMessage", String.valueOf(e));
            }
            Log.d("Actual time: ", msg);
        }
        if (msgType.equals("c4")){
            String[] meanTimeDataTemp = msg.split("_");
            for (int i=0;i<=meanTimeDataTemp.length-1;i++){
                meanTimeData[i] = Float.parseFloat(meanTimeDataTemp[i].replace(",", "."));
            }
            Log.d("Mean time: ", msg);
        }
    }

    private void sendDataToNode(float[] data, String name) {
        Bundle bundle = new Bundle();
        Message msg1 = new Message();

        bundle.putFloatArray(name, data);
        msg1.setData(bundle);
        if (BaseData.sendToVisualization != null) BaseData.sendToVisualization.sendMessage(msg1);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        customGraphDialog.showDialog();
        return false;
    }

    @Override
    public void onClick(View v) {
        // Handle click event when clear button is pressed
            cycleTimeViewer_actual.setText("");
            cycleTimeViewer_mean.setText("");
    }

}
