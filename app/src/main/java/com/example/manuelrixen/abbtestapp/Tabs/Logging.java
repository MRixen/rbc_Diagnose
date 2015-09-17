package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseClass;
import com.example.manuelrixen.abbtestapp.Drawing.CycleTimeDrawThread;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

public class Logging extends Fragment implements Receiver.FirstEventListener, Receiver.SecondEventListener, View.OnClickListener {

    private TextView loggingViewer;
    private Button clearButton;
    private int logCounter = 0;
    private int MAX_LOG_COUNTER = 100;
    private String[] logData = new String[MAX_LOG_COUNTER];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_3, container, false);

        clearButton = (Button) rootView.findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(this);
        loggingViewer = (TextView) rootView.findViewById(R.id.loggingTextField);
        loggingViewer.setMovementMethod(new ScrollingMovementMethod());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray("loggingViewer", logData);
        outState.putInt("logCounter", logCounter);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            logCounter = savedInstanceState.getInt("logCounter");
            logData = savedInstanceState.getStringArray("logData");
            for (int i=0;i<=logData.length;i++) loggingViewer.setText(logData[i] + "\n" + loggingViewer.getText());
        }
        super.onViewStateRestored(savedInstanceState);
    }

    public Receiver.FirstEventListener getFirstEventListener() {
        return this;
    }

    public Receiver.SecondEventListener getSecondEventListener() {
        return this;
    }

    @Override
    public void onError1() {
        Log.d("Console", "onError1");
        loggingViewer.setText("Cant connect to server.");
    }

    @Override
    public void onEvent1(boolean normal, String msg, String msgType) {
        showMessage(msgType, msg, normal);
    }

    @Override
    public void onError2() {
        Log.d("Console", "onError2");
        loggingViewer.setText("Cant connect to server.");
    }

    @Override
    public void onEvent2(boolean normal, String msg, String msgType) {
        showMessage(msgType, msg, normal);
    }

    private void showMessage(String msgType, String msg, boolean normal) {
        if (msgType.equals("l")){
            logData[logCounter] = msg;
            loggingViewer.setText(logData[logCounter] + "\n" + loggingViewer.getText());
            if (logCounter <= MAX_LOG_COUNTER - 1) logCounter += 1;
            else logCounter = 0;
        }
    }

    @Override
    public void onClick(View v) {
        // Handle click event when clear button is pressed
        if (loggingViewer != null){
            loggingViewer.setText("");
            logCounter = 0;
        }
    }

}
