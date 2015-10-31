package com.example.manuelrixen.abbtestapp;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.manuelrixen.abbtestapp.Barcode.BarCodeReading;
import com.example.manuelrixen.abbtestapp.Dialogs.CustomAboutDialog;
import com.example.manuelrixen.abbtestapp.Tabs.CycleTime;
import com.example.manuelrixen.abbtestapp.Tabs.Events;
import com.example.manuelrixen.abbtestapp.Tabs.Logging;
import com.example.manuelrixen.abbtestapp.Tabs.MachineData;

import java.util.Timer;
import java.util.TimerTask;

import static android.os.Process.myPid;

public class MainActivity extends TabActivity {

    private final long maxActivityShowTime = 3000;
    private PowerManager.WakeLock wl;
    private TabHost tabHost;
    private BaseData baseData;
    private boolean firstRun = true;
    private CustomAboutDialog customAboutDialog;
    private NetworkInfo mWifi;

    // TODO Check why zonenbahn-fehler isnt shown as event
    // TODO Save last connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseData = new BaseData(this, this);

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
        if (firstRun && mWifi.isConnected()) {
            startBarcodeScanner();
            firstRun = false;
        }
        super.onStart();
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

    @Override
    protected void onPause() {
        super.onPause();
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
            // Start receiver thread, to show something in the tabs
            baseData.startReceiver(ip, port);
            initTabs();
        }
        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wl.release();
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
                customAboutDialog.show();
                break;
        }
        return true;
    }

    private void initTabs() {
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Events");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Logging");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("CycleTime");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("MachineData");

        tab1.setIndicator("Events");
        Intent eventIntent = new Intent(this, Events.class);
        eventIntent.putExtra("baseData", baseData);
        tab1.setContent(eventIntent);

        tab2.setIndicator("Logging");
        Intent loggingIntent = new Intent(this, Logging.class);
        loggingIntent.putExtra("baseData", baseData);
        tab2.setContent(loggingIntent);


        tab3.setIndicator("Cycle Time");
        Intent cycleTimeIntent = new Intent(this, CycleTime.class);
        cycleTimeIntent.putExtra("baseData", baseData);
        tab3.setContent(cycleTimeIntent);

        tab4.setIndicator("Machine Data");
        Intent machineDataIntent = new Intent(this, MachineData.class);
        machineDataIntent.putExtra("baseData", baseData);
        tab4.setContent(machineDataIntent);

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);

        // Load every tab directly show information in all tabs
        tabHost.setCurrentTabByTag("Events");
        tabHost.setCurrentTabByTag("Logging");
        tabHost.setCurrentTabByTag("CycleTime");
        tabHost.setCurrentTabByTag("MachineData");
    }
}
