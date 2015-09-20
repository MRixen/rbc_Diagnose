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
import com.example.manuelrixen.abbtestapp.Drawing.CycleTimeDrawThread;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

public class CycleTime extends Activity implements Receiver.EventListener, View.OnTouchListener, View.OnClickListener {

    private TextView cycleTimeViewer;
    private CycleTimeDrawThread cycleTimeDrawThread;
    private boolean dialogIsActive = false;
    private boolean firstStart = true;
    private Button clearButton;
    private RelativeLayout actLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_2);

        clearButton = (Button) findViewById(R.id.buttonClear);
        actLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        clearButton.setOnClickListener(this);
        actLayout.setOnTouchListener(this);
        cycleTimeViewer = (TextView) findViewById(R.id.cycleTimeTextField);

        cycleTimeDrawThread = new CycleTimeDrawThread(this);

        Bundle bundle = getIntent().getExtras();
        BaseData baseData = (BaseData)bundle.getSerializable("baseData");
        Receiver receiver = baseData.getReceiver();
        receiver.registerListener(this);
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


    private void showMessage(String msgType, String msg) {
        if (msgType.equals("c")){
            String msgTemp = msg.replace(",", ".");
            cycleTimeViewer.setText(msgTemp);
            try {
                if (dialogIsActive) sendDataToNode(Float.parseFloat(msgTemp), "cycleTimeData");
            }
            catch(NullPointerException e){
                Log.d("Console:showMessage", String.valueOf(e));
            }
        }
    }

    private void sendDataToNode(float data, String name) {
        Bundle bundle = new Bundle();
        Message msg1 = new Message();

        bundle.putFloat(name, data);
        msg1.setData(bundle);
        if (BaseData.sendToVisualization != null) BaseData.sendToVisualization.sendMessage(msg1);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //setDialog(getActivity());
        return false;
    }

    @Override
    public void onClick(View v) {
        // Handle click event when clear button is pressed
        if (cycleTimeViewer != null){
            cycleTimeViewer.setText("");
        }
    }

}
