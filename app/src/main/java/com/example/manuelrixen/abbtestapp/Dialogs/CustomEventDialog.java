package com.example.manuelrixen.abbtestapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.R;

/**
 * Created by Manuel.Rixen on 05.09.2015.
 */
public class CustomEventDialog extends Dialog {

    private final ImageView imageViewer;
    private final Context context;
    private TextView descriptionText, actionText, header, consequencesText, causesText;
    private String[] dialogMessages;


    public CustomEventDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_events);
        this.context = context;

        header = (TextView) this.findViewById(R.id.header);
        descriptionText = (TextView) this.findViewById(R.id.descriptionText);
        actionText = (TextView) this.findViewById(R.id.actionText);
        consequencesText = (TextView) this.findViewById(R.id.consequencesText);
        causesText = (TextView) this.findViewById(R.id.causesText);
        imageViewer = (ImageView) this.findViewById(R.id.icon);
    }
    public int calcDimPercentageLandscape(String dimType, int dimPercentage, Context context) {
        // Calculate display size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int pxWidth = displayMetrics.widthPixels;
        int pxHeight = displayMetrics.heightPixels;
        Log.d("sizeL11", String.valueOf(pxWidth));
        Log.d("sizeL22", String.valueOf(pxHeight));
        if (dimType.equals("width")) return (pxWidth / 100) * dimPercentage;
        if (dimType.equals("height")) return (pxHeight / 100) * dimPercentage;

        else return 0;
    }

    public int calcDimPercentagePortrait(String dimType, int dimPercentage, Context context) {
        // Calculate display size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int pxWidth = displayMetrics.widthPixels;
        int pxHeight = displayMetrics.heightPixels;
        if (dimType.equals("width")) return (pxWidth / 100) * dimPercentage;
        if (dimType.equals("height")) return (pxHeight / 100) * dimPercentage;
        else return 0;
    }


    public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "reverse portrait";
            default:
                return "reverse landscape";
        }
    }

    public void showDialog(String[] dialogMessages) {
        int dialogWidth=0;
        int dialogHeight=0;
        if ((getRotation(context).equals("landscape")) || (getRotation(context).equals("reverse landscape"))) {
            dialogWidth = calcDimPercentageLandscape("width", 90, context);
            dialogHeight = calcDimPercentageLandscape("height", 90, context);
        }
        if (getRotation(context).equals("portrait")) {
            dialogWidth = calcDimPercentagePortrait("width", 90, context);
            dialogHeight = calcDimPercentagePortrait("height", 60, context);
        }
        Window dialogWindow = this.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.ModernWhite)));
        dialogWindow.setTitleColor(context.getResources().getColor(R.color.ModernWhite));
        dialogWindow.setLayout(dialogWidth, dialogHeight);

        this.dialogMessages = dialogMessages;
        header.setText(dialogMessages[0]);
        descriptionText.setText(dialogMessages[1]);
        actionText.setText(dialogMessages[2]);
        consequencesText.setText(dialogMessages[3]);
        causesText.setText(dialogMessages[4]);
        // Show different images for the error type
        switch (dialogMessages[5]) {
            case "1":
                imageViewer.setImageDrawable(getContext().getResources().getDrawable(R.drawable.info));
                break;
            case "2":
                imageViewer.setImageDrawable(getContext().getResources().getDrawable(R.drawable.warning));
                break;
            case "3":
                imageViewer.setImageDrawable(getContext().getResources().getDrawable(R.drawable.error));
                break;
        }
        this.show();
    }

    public void recreateDialog() {
        showDialog(dialogMessages);
    }

}