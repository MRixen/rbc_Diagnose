package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

public class CycleTime extends Activity implements Receiver.EventListener, View.OnTouchListener, View.OnClickListener {

    private TextView cycleTimeViewer_actual, cycleTimeViewer_mean;
    private float[] actualTimeData = new float[16];
    private float[] meanTimeData = new float[16];

    private BaseData baseData;
    private Receiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_cycletime);

        Button clearButton = (Button) findViewById(R.id.buttonClear);

        clearButton.setOnClickListener(this);
        cycleTimeViewer_actual = (TextView) findViewById(R.id.cycleTimeTextField_actual);
        cycleTimeViewer_actual.setOnTouchListener(this);
        cycleTimeViewer_mean = (TextView) findViewById(R.id.cycleTimeTextField_mean);
        cycleTimeViewer_mean.setOnTouchListener(this);

//        customGraphDialog = new CustomGraphDialog(this);

        Bundle bundle = getIntent().getExtras();
        baseData = (BaseData) bundle.getSerializable("baseData");

        if (receiver == null) {
            try {
                receiver = baseData.getReceiver();
                receiver.registerListener(this);
            } catch (NullPointerException e) {
            }
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
        if (msgType.equals("c1")) {
            String msgTemp = msg.replace(".", ",");
            cycleTimeViewer_actual.setText(msgTemp);
        }
        if (msgType.equals("c2")) {
            String msgTemp = msg.replace(".", ",");
            cycleTimeViewer_mean.setText(msgTemp);
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
        // Show graph when user press on the cycle time (not implemented yet)
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
