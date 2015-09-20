package com.example.manuelrixen.abbtestapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Manuel.Rixen on 05.09.2015.
 */
public class CustomDialog extends Dialog{

    private TextView descriptionText, actionText, header, consequencesText, causesText;

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_events);

        int dialogWidth = calcDimPercentage("width", 60, context);
        int dialogHeight = calcDimPercentage("height", 95, context);

        Window dialogWindow = this.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.ModernWhite)));
        dialogWindow.setTitleColor(context.getResources().getColor(R.color.ModernWhite));
        dialogWindow.setLayout(dialogWidth, dialogHeight);

        header = (TextView) this.findViewById(R.id.header);
        descriptionText = (TextView) this.findViewById(R.id.descriptionText);
        actionText = (TextView) this.findViewById(R.id.actionText);
        consequencesText = (TextView) this.findViewById(R.id.consequencesText);
        causesText = (TextView) this.findViewById(R.id.causesText);
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

    public void showDialog(String[] dialogMessages) {
        header.setText(dialogMessages[0]);
        descriptionText.setText(dialogMessages[1]);
        actionText.setText(dialogMessages[2]);
        consequencesText.setText(dialogMessages[3]);
        causesText.setText(dialogMessages[4]);
        show();
    }
}