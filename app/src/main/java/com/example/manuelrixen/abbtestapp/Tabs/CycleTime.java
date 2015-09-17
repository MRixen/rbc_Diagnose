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

public class CycleTime extends Fragment implements Receiver.FirstEventListener, Receiver.SecondEventListener, View.OnTouchListener, View.OnClickListener {

    private TextView cycleTimeViewer;
    private CycleTimeDrawThread cycleTimeDrawThread;
    private BaseClass baseClass = new BaseClass();
    private boolean dialogIsActive = false;
    private boolean firstStart = true;
    private Button clearButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_2, container, false);

        clearButton = (Button) rootView.findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(this);
        rootView.setOnTouchListener(this);
        cycleTimeViewer = (TextView) rootView.findViewById(R.id.cycleTimeTextField);

        cycleTimeDrawThread = new CycleTimeDrawThread(getActivity());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Save average cycle time, etc. (but not the actual!)
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        // TODO Load average cycle time, etc. (but not the actual!)
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
        cycleTimeViewer.setText("Cant connect to server.");
    }

    @Override
    public void onEvent1(boolean normal, String msg, String msgType) {
        showMessage(msgType, msg, normal);
    }

    @Override
    public void onError2() {
        Log.d("Console", "onError2");
        cycleTimeViewer.setText("Cant connect to server.");
    }

    @Override
    public void onEvent2(boolean normal, String msg, String msgType) {
        showMessage(msgType, msg, normal);
    }

    private void showMessage(String msgType, String msg, boolean normal) {
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

    private void setDialog(Context context){
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_cycletimegraph);
            dialog.setTitle("Cycle time graph");
            dialog.setCancelable(true);

            // Set title height
            Resources res = getActivity().getResources();
            int titleId = res.getIdentifier("title", "id", "android");
            View title = dialog.findViewById(titleId);
            if (title != null) {
                title.getLayoutParams().height = baseClass.calcDimDP("height", 40, getActivity());
            }

        if (firstStart) {
            SurfaceView surface = (SurfaceView) dialog.findViewById(R.id.surface_graph);
            int dialogWidth = baseClass.calcDimPercentage("width", 60, getActivity());
            int dialogHeight = baseClass.calcDimPercentage("height", 95, getActivity());

            dialog.getWindow().setLayout(dialogWidth, dialogHeight);

            TextView textFieldMaxCycleTime = (TextView) dialog.findViewById(R.id.textFieldMaxCycleTime);
            TextView textFieldMinCycleTime = (TextView) dialog.findViewById(R.id.textFieldMinCycleTime);

            cycleTimeDrawThread.startDrawThread(surface, dialogWidth, dialogHeight, textFieldMinCycleTime, textFieldMaxCycleTime);
            firstStart = false;
        }
            dialog.show();
            dialogIsActive = true;

    }

    private void sendDataToNode(float data, String name) {
        Bundle bundle = new Bundle();
        Message msg1 = new Message();

        bundle.putFloat(name, data);
        msg1.setData(bundle);
        if (BaseClass.sendToVisualization != null) BaseClass.sendToVisualization.sendMessage(msg1);
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

    public void saveData() {

    }

    public void loadData() {

    }
}
