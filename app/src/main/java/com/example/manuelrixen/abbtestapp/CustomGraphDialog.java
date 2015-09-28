package com.example.manuelrixen.abbtestapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.Drawing.CycleTimeDrawThread;

/**
 * Created by Manuel.Rixen on 05.09.2015.
 */
public class CustomGraphDialog extends Dialog{

    private final TextView minCycleTimeText, maxCycleTimeText;
    private SurfaceView surfaceView;
    private CycleTimeDrawThread cycleTimeDrawThread;
    private boolean dialogIsActive = false;

    public CustomGraphDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cycletimegraph);

        int dialogWidth = calcDimPercentage("width", 60, context);
        int dialogHeight = calcDimPercentage("height", 95, context);

        Window dialogWindow = this.getWindow();
        dialogWindow.setTitleColor(context.getResources().getColor(R.color.ModernWhite));
        dialogWindow.setLayout(dialogWidth, dialogHeight);

        maxCycleTimeText = (TextView) this.findViewById(R.id.textFieldMaxCycleTime);
        minCycleTimeText = (TextView) this.findViewById(R.id.textFieldMinCycleTime);
        surfaceView = (SurfaceView) this.findViewById(R.id.surface_graph);

        cycleTimeDrawThread = new CycleTimeDrawThread(context);
        cycleTimeDrawThread.startDrawThread(surfaceView,dialogWidth,dialogHeight,minCycleTimeText,maxCycleTimeText);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dialogIsActive = false;
        Log.d("onDetachedFromWindow", "inside");
    }

    public int calcDimPercentage(String dimType, int dimPercentage, Context context){

        // Calculate display size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int pxWidth = displayMetrics.widthPixels;
        int pxHeight = displayMetrics.heightPixels;
        Log.d("pxWidth", String.valueOf(pxWidth));
        Log.d("pxHeight", String.valueOf(pxHeight));

        if (dimType.equals("width")) return (pxWidth/100)*dimPercentage;
        if (dimType.equals("height")) return (pxHeight/100)*dimPercentage;
        else return 0;
    }

    public void showDialog() {
        show();
        dialogIsActive = true;
    }

    public boolean getDialogState(){

        return dialogIsActive;
    }
}