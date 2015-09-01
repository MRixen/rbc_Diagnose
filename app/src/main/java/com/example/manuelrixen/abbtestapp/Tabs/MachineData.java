package com.example.manuelrixen.abbtestapp.Tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */
public class MachineData extends android.support.v4.app.Fragment implements Receiver.FirstEventListener {

    protected int MAX_TABLE_ENTRIES = 12;
    private TextView[] textViews = new TextView[MAX_TABLE_ENTRIES];
    private TableRow[] tableRows = new TableRow[MAX_TABLE_ENTRIES];

    private TextView textview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_1, container, false);

        int[] textViewIds = new int[] {R.id.text_machine, R.id.text_project, R.id.text_build, R.id.text_fat,
                R.id.text_sop, R.id.text_serial, R.id.text_version, R.id.text_rtype,
                R.id.text_cid, R.id.text_lanip, R.id.text_clang, R.id.text_dutytime};

        int[] tableRowIds = new int[] {R.id.tableRow1, R.id.tableRow2, R.id.tableRow3, R.id.tableRow4,
                R.id.tableRow5, R.id.tableRow6, R.id.tableRow7, R.id.tableRow8,
                R.id.tableRow9, R.id.tableRow10, R.id.tableRow11, R.id.tableRow12};

        for (int i=0;i<= textViewIds.length-1;i++) {
            tableRows[i] = (TableRow) rootView.findViewById(tableRowIds[i]);
            textViews[i] = (TextView) tableRows[i].findViewById(textViewIds[i]);
        }

        return rootView;
    }


    public Receiver.FirstEventListener getFirstEventListener() {
        return this;
    }

    @Override
    public void onError1() {
        Log.d("MachineData", "onError1");
    }

    @Override
    public void onEvent1(boolean normal, String data1, String data2) {
        try {
            textViews[Integer.parseInt(data2)].setText(data1);
            Log.d("data2", data2);
        }
        catch(NumberFormatException e){

        }
    }
}