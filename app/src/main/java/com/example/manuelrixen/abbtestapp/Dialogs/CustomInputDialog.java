package com.example.manuelrixen.abbtestapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.R;

/**
 * Created by Manuel.Rixen on 05.09.2015.
 */
public class CustomInputDialog extends Dialog {

    private EditText inputTextField;
    private Button okButton;
    private TextView descriptionText;
    private String result;

    public CustomInputDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_input);

        int dialogWidth = calcDimPercentage("width", 60, context);
        int dialogHeight = calcDimPercentage("height", 95, context);

        Window dialogWindow = this.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.ModernWhite)));
        dialogWindow.setTitleColor(context.getResources().getColor(R.color.ModernWhite));
        dialogWindow.setLayout(dialogWidth, dialogHeight);

        descriptionText = (TextView) this.findViewById(R.id.description);
        inputTextField = (EditText) this.findViewById(R.id.inputText);
        okButton = (Button) this.findViewById(R.id.ok_button);
    }

    public int calcDimPercentage(String dimType, int dimPercentage, Context context) {

        // Calculate display size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int pxWidth = displayMetrics.widthPixels;
        int pxHeight = displayMetrics.heightPixels;
        Log.d("pxWidth", String.valueOf(pxWidth));
        Log.d("pxHeight", String.valueOf(pxHeight));

        if (dimType.equals("width")) return (pxWidth / 100) * dimPercentage;
        if (dimType.equals("height")) return (pxHeight / 100) * dimPercentage;
        else return 0;
    }

    public String showDialog(String dialogHeader) {
        result = "";
        descriptionText.setText(dialogHeader);

        show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = inputTextField.getText().toString();
                dismiss();
            }

        });

        return result;
    }
}