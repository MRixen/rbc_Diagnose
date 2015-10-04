package com.example.manuelrixen.abbtestapp;

import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;


import com.example.manuelrixen.abbtestapp.Barcode.BarCodeReading;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;
import com.example.manuelrixen.abbtestapp.Tabs.CycleTime;
import com.example.manuelrixen.abbtestapp.Tabs.EntryPoint;
import com.example.manuelrixen.abbtestapp.Tabs.Events;
import com.example.manuelrixen.abbtestapp.Tabs.Logging;
import com.example.manuelrixen.abbtestapp.Tabs.MachineData;

import java.util.Locale;

import static android.os.Process.myPid;

// Add tabs to action bar

public class MainActivity extends TabActivity implements android.app.ActionBar.TabListener {

    private PowerManager.WakeLock wl;
    private TabHost tabHost;
    private BaseData baseData;

    // TODO Check why zonenbahn-fehler isnt shown as event
    // TODO Add different pictures to dialog (warning, info, error)
    // TODO Change order of list entries
    // TODO Save last connection / Change entry point to: Choose between last connection and new connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseData = new BaseData(this);

        // create the TabHost that will contain the Tabs
        tabHost = getTabHost();
        initTabs();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
//            for (int i = 0; i <= receiver.length-1; i++) {
//                if(receiver[i] != null) receiver[i].stopRunRoutine();
//            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wl.release();
        android.os.Process.killProcess(myPid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Remove item from list
        //if(titleName.equals(getResources().getString(R.string.title_activity_execute))) menu.removeItem(R.id.popup_properties);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case(R.id.popup_properties):
//                Intent settingsIntent = new Intent(this, Settings.class);
//                startActivity(settingsIntent);
                break;
        }
        return true;
    }

    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
//        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
    }

    private void initTabs() {

        TabHost.TabSpec tab0 = tabHost.newTabSpec("Entry Point");
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Events");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Logging");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("CycleTime");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("MachineData");


        tab0.setIndicator("Entry Point");
        Intent entryPointIntent = new Intent(this, EntryPoint.class);
        entryPointIntent.putExtra("baseData", baseData);
        tab0.setContent(entryPointIntent);

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
        tabHost.addTab(tab0);
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);
        // Load all tabs

        tabHost.setCurrentTabByTag("Events");
        tabHost.setCurrentTabByTag("Logging");
        tabHost.setCurrentTabByTag("CycleTime");
        tabHost.setCurrentTabByTag("MachineData");
        tabHost.setCurrentTabByTag("Entry Point");
    }
}
