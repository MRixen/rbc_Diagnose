package com.example.manuelrixen.abbtestapp;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


import com.example.manuelrixen.abbtestapp.Barcode.BarCodeReading;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;
import com.example.manuelrixen.abbtestapp.Tabs.CycleTime;
import com.example.manuelrixen.abbtestapp.Tabs.Events;
import com.example.manuelrixen.abbtestapp.Tabs.Logging;
import com.example.manuelrixen.abbtestapp.Tabs.MachineData;

import java.util.Locale;

import static android.os.Process.myPid;


public class MainActivity extends BaseClass implements android.app.ActionBar.TabListener {

    private static final int MAX_TABS_COUNT = 4;
    private PowerManager.WakeLock wl;
    private Receiver[] receiver = new Receiver[3];
    private Thread[] rThread = new Thread[3];

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    private boolean firstRun = true;
    private CycleTime cycleTime;
    private MachineData machineData;
    private Logging logging;
    private Events events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT < 16) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//        else {
//            View decorView = getWindow().getDecorView();
//            // Hide the status bar.
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
        setContentView(R.layout.activity_main);

        cycleTime = new CycleTime();
        machineData = new MachineData();
        logging = new Logging();
        events = new Events();

        initTabs();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstRun){
            startBarcodeScanner();
            firstRun = false;
        }
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
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {

    }

    private void initTabs() {
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final android.app.ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return machineData;
                case 1:
                    return cycleTime;
                case 2:
                    return logging;
                case 3:
                    return events;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return MAX_TABS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }

    private void startBarcodeScanner() {
        Intent barCodeReader = new Intent(this, BarCodeReading.class);
        startActivityForResult(barCodeReader, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Bundle iData = data.getExtras();
            String ip = iData.getString("ip");
            String port = iData.getString("port");

            startReceiver(0, false, ip, port, cycleTime, machineData, logging);
        }
        if(resultCode == RESULT_CANCELED){
            finish();
        }
    }

    private void startReceiver(int number, boolean normalView, String ip, String port, CycleTime cycleTime, MachineData machineData, Logging logging) {
        receiver[number] = new Receiver(this, ip, port, normalView, number, cycleTime, machineData, logging);
        rThread[number] = new Thread(receiver[number]);
        rThread[number].start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
