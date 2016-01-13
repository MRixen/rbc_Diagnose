package com.example.manuelrixen.abbtestapp;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.manuelrixen.abbtestapp.Barcode.BarCodeReading;
import com.example.manuelrixen.abbtestapp.Dialogs.CustomAboutDialog;
import com.example.manuelrixen.abbtestapp.Dialogs.CustomDecisionDialog;
import com.example.manuelrixen.abbtestapp.Dialogs.CustomInputDialog;
import com.example.manuelrixen.abbtestapp.Tabs.Article;
import com.example.manuelrixen.abbtestapp.Tabs.Events;
import com.example.manuelrixen.abbtestapp.Tabs.Info;
import com.example.manuelrixen.abbtestapp.Tabs.Logs;

import java.util.Timer;
import java.util.TimerTask;

import static android.os.Process.myPid;

public class MainActivity extends TabActivity {

    private final long maxActivityShowTime = 3000;
    private PowerManager.WakeLock wl;
    private TabHost tabHost;
    private BaseData baseData;
    private boolean alreadyShown;
    private boolean firstStart = true;
    private CustomAboutDialog customAboutDialog;
    private NetworkInfo mWifi;
    private SharedPreferences sharedPreferences;
    private CustomDecisionDialog customDecisionDialog;
    private CustomInputDialog customInputDialog;
    private boolean useLastConnection = false;
    private SharedPreferences.Editor editor;
    private boolean setConnectionDataManually = false;
    private EditText ipField, portField;


    // TODO Check why zonenbahn-fehler isnt shown as event

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseData = new BaseData(this, this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        customDecisionDialog = new CustomDecisionDialog(this);
        customInputDialog = new CustomInputDialog(this, this);


        // create the TabHost that will contain the Tabs
        tabHost = getTabHost();

        customAboutDialog = new CustomAboutDialog(this);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final String ip = sharedPreferences.getString("ip", "0");
        final String port = sharedPreferences.getString("port", "0");
        alreadyShown = sharedPreferences.getBoolean("alreadyShown", false);
        firstStart = sharedPreferences.getBoolean("firstStart", true);
        if ( !(ip.equals("0")) && !(port.equals("0")) && alreadyShown){
            // Show user dialog to ask for manual input
            customDecisionDialog.showDialog("Use last connection with ip: "+ip+" and port: "+port+"?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    useLastConnection = true;
                    startReceiving(ip, port);
                    customDecisionDialog.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    useLastConnection = false;
                    customDecisionDialog.dismiss();
                    startBarcodeScanner();
                }
            }, new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    setConnectionDataManually = true;
                    showDialogForManuallyInput();
                    customDecisionDialog.dismiss();
                }
            });
        }

        else {
            if (firstStart) {
                startBarcodeScanner();
                firstStart = false;
            }
        }

        if (!mWifi.isConnected()) {
            Toast.makeText(this, R.string.errMsgWlan, Toast.LENGTH_LONG).show();
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, maxActivityShowTime);
        }
    }

    private void showDialogForManuallyInput(){
        if (setConnectionDataManually){
            // Show user dialog to choose between last and new connection
            customInputDialog.showDialog("Set IP and PORT manually.", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Take the manually input and set ip and port
                    // TODO Check ip port formatting
                    String ip = ipField.getText().toString();
                    String port = portField.getText().toString();
                    if (!(ip.equals("")) && !(port.equals(""))) {
                        startReceiving(ipField.getText().toString(), portField.getText().toString());
                        setConnectionDataManually = false;
                        saveConnectionData(ip, port);
                        customInputDialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Connection data not set", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Cancel manually input and show barcode reader
                    customInputDialog.dismiss();
                    startBarcodeScanner();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = sharedPreferences.edit();
        editor.putBoolean("alreadyShown", true);
        editor.putBoolean("firstStart", firstStart);
        editor.apply();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        editor = sharedPreferences.edit();
        editor.putBoolean("alreadyShown", false);
        editor.apply();
    }

    private void startBarcodeScanner() {
        Intent barCodeReader = new Intent(this, BarCodeReading.class);
        startActivityForResult(barCodeReader, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle iData = data.getExtras();
            String ip = iData.getString("ip");
            String port = iData.getString("port");
            // Save data for last connection
            saveConnectionData(ip, port);
            // Start receiver thread, to show something in the tabs
            startReceiving(ip, port);
        }
        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    private void saveConnectionData(String ip, String port) {
        editor = sharedPreferences.edit();
        editor.putString("ip", ip);
        editor.putString("port", port);
        editor.apply();
    }

    private void startReceiving(String ip, String port) {
        baseData.startReceiver(ip, port);
        initTabs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wl.release();
        if (baseData.getReceiver() != null) baseData.getReceiver().stopRunRoutine();
        android.os.Process.killProcess(myPid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.popup_about):
                customAboutDialog.showDialog();
                break;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (customAboutDialog.isShowing()){
            customAboutDialog.showDialog();
        }
    }

    private void initTabs() {
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Events");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Logs");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Article");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("Info");

        tab1.setIndicator("Events");
        Intent eventIntent = new Intent(this, Events.class);
        eventIntent.putExtra("baseData", baseData);
        tab1.setContent(eventIntent);

        tab2.setIndicator("Logs");
        Intent loggingIntent = new Intent(this, Logs.class);
        loggingIntent.putExtra("baseData", baseData);
        tab2.setContent(loggingIntent);


        tab3.setIndicator("Article");
        Intent cycleTimeIntent = new Intent(this, Article.class);
        cycleTimeIntent.putExtra("baseData", baseData);
        tab3.setContent(cycleTimeIntent);

        tab4.setIndicator("Info");
        Intent machineDataIntent = new Intent(this, Info.class);
        machineDataIntent.putExtra("baseData", baseData);
        tab4.setContent(machineDataIntent);

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);

        // Load every tab directly show information in all tabs
        tabHost.setCurrentTabByTag("Events");
        tabHost.setCurrentTabByTag("Logs");
        tabHost.setCurrentTabByTag("Article");
        tabHost.setCurrentTabByTag("Info");
    }

    public void setTextFields(EditText ipField, EditText portField){
        this.ipField = ipField;
        this.portField = portField;
    }

}
