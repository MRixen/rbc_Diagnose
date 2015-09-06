package com.example.manuelrixen.abbtestapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Manuel.Rixen on 05.09.2015.
 */
public class CustomDialog extends Dialog{

    private TextView descriptionText, actionText, header;

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_events);

        Window dialogWindow = this.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.ModernWhite)));
        dialogWindow.setTitleColor(context.getResources().getColor(R.color.ModernWhite));

        header = (TextView) this.findViewById(R.id.header);
        descriptionText = (TextView) this.findViewById(R.id.descriptionText);
        actionText = (TextView) this.findViewById(R.id.actionText);
    }

    public void showDialog(String[] dialogMessages) {
        header.setText(dialogMessages[0]);
        descriptionText.setText(dialogMessages[1]);
        actionText.setText(dialogMessages[2]);
        show();
    }
}