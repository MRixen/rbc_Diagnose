package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

public class Logging extends Activity implements Receiver.EventListener, View.OnClickListener {

    private TextView loggingViewer;
    private int logCounter = 0;
    private int MAX_LOG_COUNTER = 100;
    private String[] logData = new String[MAX_LOG_COUNTER];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_3);

        Button clearButton = (Button) findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(this);
        loggingViewer = (TextView) findViewById(R.id.loggingTextField);
        loggingViewer.setMovementMethod(new ScrollingMovementMethod());

        Bundle bundle = getIntent().getExtras();
        BaseData baseData = (BaseData)bundle.getSerializable("baseData");
        Receiver receiver = baseData.getReceiver();
        receiver.registerListener(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray("loggingViewer", logData);
        outState.putInt("logCounter", logCounter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            logCounter = savedInstanceState.getInt("logCounter");
            logData = savedInstanceState.getStringArray("logData");
            for (int i=0;i<=logData.length;i++) loggingViewer.setText(logData[i] + "\n" + loggingViewer.getText());
        }
    }

    @Override
    public void onError() {
        Log.d("Console", "onError1");
        loggingViewer.setText("Cant connect to server.");
    }

    @Override
    public void onEvent(String msg, String msgType) {
        showMessage(msgType, msg);
    }

    private void showMessage(String msgType, String msg) {
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
