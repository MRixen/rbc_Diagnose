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
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseClass;
import com.example.manuelrixen.abbtestapp.Drawing.CycleTimeDrawThread;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

public class Logging extends Fragment implements Receiver.FirstEventListener, Receiver.SecondEventListener  {

    private TextView loggingViewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_3, container, false);

        loggingViewer = (TextView) rootView.findViewById(R.id.loggingTextField);
        loggingViewer.setMovementMethod(new ScrollingMovementMethod());

        return rootView;
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
        if (msgType.equals("l")) loggingViewer.setText(msg + "\n" + loggingViewer.getText());
    }
}
