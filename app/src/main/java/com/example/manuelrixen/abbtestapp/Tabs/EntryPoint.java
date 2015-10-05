package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.manuelrixen.abbtestapp.Barcode.BarCodeReading;
import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.CustomDecisionDialog;
import com.example.manuelrixen.abbtestapp.CustomInputDialog;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

import java.util.ArrayList;
import java.util.Calendar;

public class EntryPoint extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private boolean firstRun = true;
    private BaseData baseData;
    private ListView connectionViewer;
    private ArrayList<String> eventListDescriptions, eventListData;
    private ArrayAdapter<String> arrayAdapter;
    private CustomInputDialog customInputDialog;
    private CustomDecisionDialog customDecisionDialog;
    // TODO Save last connections in listview and make it possible to reconnect to it via onClick event

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_entrypoint);

        Button newConnectionButton = (Button) findViewById(R.id.buttonNewConnection);
        newConnectionButton.setOnClickListener(this);

        connectionViewer = (ListView) findViewById(R.id.lastConnectionListView);
        connectionViewer.setOnItemClickListener(this);
        eventListData = new ArrayList<String>();
        eventListDescriptions = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                eventListDescriptions);
        connectionViewer.setAdapter(arrayAdapter);

        Bundle bundle = getIntent().getExtras();
        baseData = (BaseData)bundle.getSerializable("baseData");

        customDecisionDialog = new CustomDecisionDialog(this);
        customInputDialog = new CustomInputDialog(this);
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
    public void onClick(View v) {
        // Handle click event when new connection button is pressed
        startBarcodeScanner();
    }

    private void startBarcodeScanner() {
        Intent barCodeReader = new Intent(this, BarCodeReading.class);
        startActivityForResult(barCodeReader, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Bundle iData = data.getExtras();
            String ip = iData.getString("ip");
            String port = iData.getString("port");
            // Save to list view and show dialog to set name for it
            if (eventListDescriptions != null && eventListData != null) {
                // TODO Show options by long click on list view (rename, remove)
                Calendar c = Calendar.getInstance();
                eventListDescriptions.add(0,  c.getTime().toString()+";"+ip+";"+port);
                eventListData.add(0, ip+";"+port);
                arrayAdapter.notifyDataSetChanged();
            }
            try {
                Receiver receiver = baseData.getReceiver();
                receiver.stopRunRoutine();
            }catch(NullPointerException e){}
            baseData.startReceiver(ip, port);

        }
        if(resultCode == RESULT_CANCELED){
//            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        arrayAdapter.getItem(position);
        final boolean[] answer = {false};
        View.OnClickListener yesButtonAnswer = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer[0] = true;
            }
        };
        View.OnClickListener noButtonAnswer = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer[0] = false;
            }
        };
        customDecisionDialog.showDialog("Wollen Sie die Verbindung wirklich aufbauen?", yesButtonAnswer, noButtonAnswer);
        // Set new connection from the list view
        if (answer[0]) {
            String[] connectionData = eventListData.get(position).split(";");
            try {
                Receiver receiver = baseData.getReceiver();
                receiver.stopRunRoutine();
            } catch (NullPointerException e) {
            }
            baseData.resetReceiver();
            baseData.startReceiver(connectionData[0], connectionData[1]);
        }
    }

}
