package com.example.manuelrixen.abbtestapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.R;

/**
 * Created by Manuel.Rixen on 05.09.2015.
 */
public class CustomChooserDialog extends Dialog {

    private final RadioGroup connectChooser;
    private final TextView lastConnection;
    private Context context;
    private EditText inputTextField;
    private Button okButton, cancelButton;
    private TextView descriptionText;
    private boolean result;

    public CustomChooserDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_chooser);
        this.context = context;

        connectChooser = (RadioGroup) findViewById(R.id.radioGroup);
        descriptionText = (TextView) this.findViewById(R.id.description);
        cancelButton = (Button) this.findViewById(R.id.cancel_button);
        okButton = (Button) this.findViewById(R.id.ok_button);
        lastConnection = (TextView) this.findViewById(R.id.lastConnection);
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

    public void showDialog(String dialogHeader, String lastConnectionText, View.OnClickListener cancelButtonListener, View.OnClickListener okButtonListener) {
        int dialogWidth=0;
        int dialogHeight=0;
        if ((getRotation(context).equals("landscape")) || (getRotation(context).equals("reverse landscape"))) {
            dialogWidth = calcDimPercentageLandscape("width", 90, context);
            dialogHeight = calcDimPercentageLandscape("height", 90, context);
        }
        if (getRotation(context).equals("portrait")) {
            dialogWidth = calcDimPercentagePortrait("width", 90, context);
            dialogHeight = calcDimPercentagePortrait("height", 50, context);
        }
        Window dialogWindow = this.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.ModernWhite)));
        dialogWindow.setTitleColor(context.getResources().getColor(R.color.ModernWhite));
        dialogWindow.setLayout(dialogWidth, dialogHeight);

        result = false;
        descriptionText.setText(dialogHeader);
        lastConnection.setText(lastConnectionText);

        cancelButton.setOnClickListener(cancelButtonListener);
        okButton.setOnClickListener(okButtonListener);

        this.show();
    }

    public String getConnectOption() {
        int connectionOptionID = connectChooser.getCheckedRadioButtonId();
        Button radioButton = (RadioButton) findViewById(connectionOptionID);
        return radioButton.getText().toString();
    }

}