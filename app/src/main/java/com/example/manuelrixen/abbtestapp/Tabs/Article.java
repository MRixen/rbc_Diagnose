package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.Dialogs.CustomShowDialog;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

public class Article extends Activity implements Receiver.EventListener, View.OnTouchListener, View.OnClickListener {

    protected int MAX_ARTICLE_COUNTER = 5;
    private TextView cycleTimeViewer_actual, cycleTimeViewer_mean, testview;
    private float[] actualTimeData = new float[16];
    private float[] meanTimeData = new float[16];

    private BaseData baseData;
    private Receiver receiver;
    private String[] articleName = new String[MAX_ARTICLE_COUNTER];
    private String[] articleCounter = new String[MAX_ARTICLE_COUNTER];
    private TextView[] articleTextViews = new TextView[MAX_ARTICLE_COUNTER];
    private TextView[] counterTextViews = new TextView[MAX_ARTICLE_COUNTER];
    private TableRow[] tableRows = new TableRow[MAX_ARTICLE_COUNTER];
    private RelativeLayout layoutTimes;
    private CustomShowDialog customShowDialog;
    private Integer MAX_CYCLETIME_DURATION = 4;

    // TODO: Make it possible that the size of MAX_ARTICLE_COUNTER is variable (the size comes from abb controller
    // TODO: Add settings activity to change MAX_CYCLETIME_DURATION
    // TODO:  Solve problem with file access error when writ/read article data to file (abb controller)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_article);

        customShowDialog = new CustomShowDialog(this);

        // Set cycle time text fields
        cycleTimeViewer_actual = (TextView) findViewById(R.id.cycleTimeTextField_actual);
        cycleTimeViewer_actual.setOnTouchListener(this);
        cycleTimeViewer_mean = (TextView) findViewById(R.id.cycleTimeTextField_mean);
        cycleTimeViewer_mean.setOnTouchListener(this);


        int[] articleViewIds = new int[]{R.id.article1, R.id.article2, R.id.article3, R.id.article4, R.id.article5};
        int[] counterViewIds = new int[]{R.id.counter1, R.id.counter2, R.id.counter3, R.id.counter4, R.id.counter5};

        int[] tableRowIds = new int[]{R.id.tableRow4, R.id.tableRow5, R.id.tableRow6, R.id.tableRow7, R.id.tableRow8};

        for (int i = 0; i <= articleViewIds.length - 1; i++) {
            tableRows[i] = (TableRow) findViewById(tableRowIds[i]);
            articleTextViews[i] = (TextView) tableRows[i].findViewById(articleViewIds[i]);
            counterTextViews[i] = (TextView) tableRows[i].findViewById(counterViewIds[i]);
        }

        Button clearButton = (Button) findViewById(R.id.buttonClear);

        clearButton.setOnClickListener(this);


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
            String msgTemp = msg;//msg.replace(".", ",");
            cycleTimeViewer_actual.setText(msgTemp);
            customShowDialog.setCycleTimePresenter(0, msgTemp, Color.BLACK);

            if (Float.valueOf(msgTemp)>MAX_CYCLETIME_DURATION){
                cycleTimeViewer_actual.setTextColor(Color.RED);
                customShowDialog.setCycleTimePresenter(0, msgTemp, Color.RED);
            }
        }
        if (msgType.equals("c2")) {
            String msgTemp = msg;//msg.replace(".", ",");
            cycleTimeViewer_mean.setText(msgTemp);
            customShowDialog.setCycleTimePresenter(1, msgTemp, Color.BLACK);
        }
        if (msgType.equals("a")) {
            String[] tempMessage = msg.split(":");
            if (!tempMessage[0].equals(" ")) articleName[Integer.parseInt(tempMessage[2])] = tempMessage[0];
            articleCounter[Integer.parseInt(tempMessage[2])] = tempMessage[1];
            if (!tempMessage[0].equals(" ")) articleTextViews[Integer.parseInt(tempMessage[2])].setText(tempMessage[0]);
            counterTextViews[Integer.parseInt(tempMessage[2])].setText(tempMessage[1]);
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
        // Show presenter for the cycle time (simple dialog)
        customShowDialog.showDialog();
        return false;
    }

    @Override
    public void onClick(View v) {
        // Handle click event when clear button is pressed
        cycleTimeViewer_actual.setText("");
        cycleTimeViewer_mean.setText("");
    }
}
