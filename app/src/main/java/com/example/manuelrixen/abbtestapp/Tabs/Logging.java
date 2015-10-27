package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

import java.util.ArrayList;

public class Logging extends Activity implements Receiver.EventListener, View.OnClickListener {

    private ListView loggingViewer;
    private int logCounter = 0;
    private int MAX_LOG_COUNTER = 100;
    private BaseData baseData;
    private Receiver receiver;
    private ArrayList<String> loggingList;
    private int MAX_LOG_AMOUNT = 10;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_logging);

        Button clearButton = (Button) findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(this);
        loggingViewer = (ListView) findViewById(R.id.loggingListView);
        loggingList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                loggingList);
        loggingViewer.setAdapter(arrayAdapter);

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
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("loggingList", loggingList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loggingList = savedInstanceState.getStringArrayList("loggingList");
    }

    @Override
    public void onError() {
        Log.d("Console", "onError1");
        loggingList.clear();
        // TODO Show error messages
    }

    @Override
    public void onEvent(String msg, String msgType) {
        showMessage(msgType, msg);
    }

    private void showMessage(String msgType, String msg) {
        if (msgType.equals("l")){
            if (loggingList.size() >= MAX_LOG_AMOUNT){
                loggingList.remove(loggingList.size() - 1);
            }
            loggingList.add(0, msg);
            arrayAdapter.notifyDataSetChanged();
            if (logCounter <= MAX_LOG_COUNTER - 1) logCounter += 1;
            else logCounter = 0;
        }
    }

    @Override
    public void onClick(View v) {
        // Handle click event when clear button is pressed
        loggingList.clear();
        arrayAdapter.notifyDataSetChanged();
    }
}
