package com.example.manuelrixen.abbtestapp.Barcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.manuelrixen.abbtestapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by Manuel.Rixen on 18.08.2015.
 */
public class BarCodeReading extends FragmentActivity {

    private IntentIntegrator intentIntegrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodereading);

        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.QR_CODE_TYPES);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start barcode scanner
        intentIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // Handle scan result
            String content = scanResult.getContents();
            String[] rData = validContent(content);
            if ((!(rData[0].equals("")) && !(rData[1].equals(""))) && (!(rData[0].equals("x")) && !(rData[1].equals("x")))) {
                Bundle b = new Bundle();
                b.putString("ip", rData[0]);
                b.putString("port", rData[1]);
                Intent i = getIntent(); //gets the intent that called this intent
                i.putExtras(b);
                setResult(RESULT_OK, i);
                finish();
            } else if ((rData[0].equals("x")) && (rData[1].equals("x"))) {
                // Nullpointer exception, e.g. content is null, e.g. user pressed back button
                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);
                finish();
            } else {
                // Do nothing when the routine validContent() say that the qr code is invalid
                Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String[] validContent(String content) {
        boolean state[] = new boolean[]{false, false};
        String ipTemp = "";
        String portTemp = "";
        try {
            if (content.contains(";")) {
                String[] tempContent = content.split(";");
                ipTemp = tempContent[0];
                portTemp = tempContent[1];
            }
            // Check IP format
            String[] ipData = ipTemp.split("\\.");
            Log.d("split", String.valueOf(ipData.length));
            for (int i = 0; i <= ipData.length - 1; i++) {
                if (ipData[i].length() <= 3) state[0] = true;
                else state[0] = false;
            }
            // TODO Check port format
            try {
                if ((Integer.parseInt(portTemp) <= 4999) && (Integer.parseInt(portTemp) >= 1025))
                    state[1] = true;
            } catch (NumberFormatException e) {
                state[1] = false;
            }

            if ((state[0] && state[1])) {
                return new String[]{ipTemp, portTemp};
            } else return new String[]{"", ""};
        } catch (NullPointerException e) {
            return new String[]{"x", "x"};
        }
    }
}
