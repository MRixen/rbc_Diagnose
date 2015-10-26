package com.example.manuelrixen.abbtestapp.Tabs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */
public class MachineData extends Activity implements Receiver.EventListener {

    protected int MAX_TABLE_ENTRIES = 12;
    private TextView[] textViews = new TextView[MAX_TABLE_ENTRIES];
    private TableRow[] tableRows = new TableRow[MAX_TABLE_ENTRIES];

    private String[] machineData = new String[MAX_TABLE_ENTRIES];

    private BaseData baseData;
    private Receiver receiver;

    // TODO Check every entry that it is send correctly (and shown in the table)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_machinedata);

        int[] textViewIds = new int[] {R.id.text_machine, R.id.text_project, R.id.text_build, R.id.text_fat,
                R.id.text_sop, R.id.text_serial, R.id.text_version, R.id.text_rtype,
                R.id.text_cid, R.id.text_lanip, R.id.text_clang, R.id.text_dutytime};

        int[] tableRowIds = new int[] {R.id.tableRow1, R.id.tableRow2, R.id.tableRow3, R.id.tableRow4,
                R.id.tableRow5, R.id.tableRow6, R.id.tableRow7, R.id.tableRow8,
                R.id.tableRow9, R.id.tableRow10, R.id.tableRow11, R.id.tableRow12};

        for (int i=0;i<= textViewIds.length-1;i++) {
            tableRows[i] = (TableRow) findViewById(tableRowIds[i]);
            textViews[i] = (TextView) tableRows[i].findViewById(textViewIds[i]);
        }

        Bundle bundle = getIntent().getExtras();
        baseData = (BaseData)bundle.getSerializable("baseData");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (receiver == null) {
            try {
                receiver = baseData.getReceiver();
                receiver.registerListener(this);
            }catch(NullPointerException e){}
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("MachineData", "onSaveInstanceState");
        outState.putStringArray("machineData", machineData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d("MachineData", "onViewStateRestored");
            machineData = savedInstanceState.getStringArray("machineData");
            for (int i=0;i<=machineData.length;i++) textViews[i].setText(machineData[i]);
        }
    }

    public Receiver.EventListener getFirstEventListener() {
        return this;
    }

    @Override
    public void onEvent(String data1, String data2) {
        showEvent(data2, data1);
    }

    @Override
    public void onError() {
        textViews[0].setText("Error");
    }

    private void showEvent(String msgType, String eventMessage) {
        int number = -1;
        try{
            number = Integer.parseInt(msgType);
        }
        catch(NumberFormatException e){
        }
        if (number != -1){
            try {
                Log.d("machineData", "onEvent1_ok");
                machineData[Integer.parseInt(msgType)] = eventMessage;
                textViews[Integer.parseInt(msgType)].setText(eventMessage);
            } catch (NumberFormatException e) {
                Log.d("machineData", "onEvent1_not_ok");
            }
        }
    }

}