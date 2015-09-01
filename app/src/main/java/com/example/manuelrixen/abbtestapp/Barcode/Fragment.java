package com.example.manuelrixen.abbtestapp.Barcode;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.Result;
import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment;

/**
* Created by mito on 9/17/13.
*/
public class Fragment extends BarCodeScannerFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels - 100;

        int frameWidth = 350;
        int frameHeight = 350;

        this.setFramingRect(frameWidth, frameHeight, width / 2 - frameWidth / 2, height / 2 - frameHeight / 2);
    }

    public Fragment() {

    }
}