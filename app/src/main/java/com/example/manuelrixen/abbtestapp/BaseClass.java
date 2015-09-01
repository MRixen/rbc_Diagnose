package com.example.manuelrixen.abbtestapp;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by Manuel.Rixen on 27.08.2015.
 */
public class BaseClass extends FragmentActivity {
    public static Handler sendToNode = null, sendToDataAcquisition = null, sendToVisualization = null;

    public int calcDimDP(String dimType, int dimDp, Context context){

        // Calculate display size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int pxWidth = displayMetrics.widthPixels;
        int pxHeight = displayMetrics.heightPixels;
        float dpWidth = pxWidth / displayMetrics.density;
        float dpHeight = pxHeight / displayMetrics.density;

        // Factor dp to px
        float factorHeight = (pxHeight / dpHeight);
        float factorWidth = (pxWidth / dpWidth);
        Log.d("factorHeight", String.valueOf(factorHeight));
        Log.d("factorWidth", String.valueOf(factorWidth));

        if (dimType.equals("width")) return Math.round(factorWidth*dimDp);
        if (dimType.equals("height")) return Math.round(factorHeight*dimDp);
        else return 0;
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
}
