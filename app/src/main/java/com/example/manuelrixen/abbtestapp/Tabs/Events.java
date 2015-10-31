package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.Dialogs.CustomEventDialog;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Events extends Activity implements Receiver.EventListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private XmlPullParser xmlParser;
    private CustomEventDialog customEventDialog;

    private ArrayList<String> eventList;
    private ArrayAdapter<String> arrayAdapter;
    private int listCounter = 0;
    private int MAX_LIST_COUNTER = 100;
    private String[] listViewEntryData = new String[MAX_LIST_COUNTER];
    private Receiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_section_events);

        Button clearButton = (Button) findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(this);
        ListView eventViewer = (ListView) findViewById(R.id.eventListView);
        eventViewer.setOnItemClickListener(this);
        eventList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                eventList);
        eventViewer.setAdapter(arrayAdapter);

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlParser = xmlFactoryObject.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        customEventDialog = new CustomEventDialog(this);


        Bundle bundle = getIntent().getExtras();
        BaseData baseData = (BaseData) bundle.getSerializable("baseData");

        if (receiver == null) {
            try {
                receiver = baseData.getReceiver();
                receiver.registerListener(this);
            }catch(NullPointerException e){}
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray("listViewEntryData", listViewEntryData);
        outState.putStringArrayList("eventList", eventList);
        outState.putInt("listCounter", listCounter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            eventList = savedInstanceState.getStringArrayList("eventList");
            listViewEntryData = savedInstanceState.getStringArray("listViewEntryData");
            listCounter = savedInstanceState.getInt("listCounter");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onError() {
        if (eventList != null) {
            eventList.clear();
            setEventList("Cant connect to server.");
        }
    }

    @Override
    public void onEvent(String msg, String msgType) {
            showEvent(msgType, msg, true);
    }

    private void setEventList(String eventText){
        if (eventList != null) {
            eventList.add(0, eventText);
            arrayAdapter.notifyDataSetChanged();
            if (listCounter <= MAX_LIST_COUNTER - 1) listCounter += 1;
            else listCounter = 0;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        arrayAdapter.getItem(position);
        // Calc position because its different order
        position = (position+arrayAdapter.getCount())-(2*position)-1;
        // Show dialog with the complete event info
        if (listViewEntryData[position] != null) showEvent("e", listViewEntryData[position], false);
    }


    private void showEvent(String msgType, String eventMessage, boolean saveEvent) {
        // Split event message to get the robot state (first entry) the event domain (second entry) and the event number (third entry)
        // Save events in array
        if (msgType.equals("e")){
            if (saveEvent) listViewEntryData[listCounter] = eventMessage;
            String[] tempMessage = eventMessage.split(":");
            new XMLParsing(saveEvent, this).execute(tempMessage);
        }
    }

    @Override
    public void onClick(View v) {
        // Handle click event when clear button is pressed
        if (eventList != null){
            eventList.clear();
            arrayAdapter.notifyDataSetChanged();
            listCounter = 0;
        }
    }

    private class XMLParsing extends AsyncTask<String, Void, String[]> {
        private final boolean addEvents;
        private String[] eventMessages;
        private Vibrator vibrator;

        public XMLParsing(boolean addEvents, Context context){
            this.addEvents = addEvents;
            this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        @Override
        protected String[] doInBackground(String... eventMessages) {
            this.eventMessages = eventMessages;
            String[] eventDescription = new String[] {"", "", "", "", "", ""}; // Title, Description, Actions, Consequences, Causes
            String filename = "";
            try {
                // Choose between the .xml files
                switch (Integer.parseInt(eventMessages[1])){
                    case 1:
                        if (Integer.parseInt(eventMessages[2]) <= 175) filename = "elog/"+"opr_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 175) && (Integer.parseInt(eventMessages[2]) <= 1231)) filename = "elog/"+"opr_"+"elogtext_"+2+".xml";
                        if (Integer.parseInt(eventMessages[2]) > 1231) filename = "elog/"+"opr_"+"elogtext_"+3+".xml";
                        break;
                    case 2:
                        if (Integer.parseInt(eventMessages[2]) <= 150) filename = "elog/"+"sys_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 150) && (Integer.parseInt(eventMessages[2]) <= 248)) filename = "elog/"+"sys_"+"elogtext_"+2+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 248) && (Integer.parseInt(eventMessages[2]) <= 365)) filename = "elog/"+"sys_"+"elogtext_"+3+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 365) && (Integer.parseInt(eventMessages[2]) <= 489)) filename = "elog/"+"sys_"+"elogtext_"+4+".xml";
                        if (Integer.parseInt(eventMessages[2]) > 489) filename = "elog/"+"sys_"+"elogtext_"+5+".xml";
                        break;
                    case 3:
                        if (Integer.parseInt(eventMessages[2]) <= 4203) filename = "elog/"+"hw_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 4203) && (Integer.parseInt(eventMessages[2]) <= 7082)) filename = "elog/"+"hw_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 7082) && (Integer.parseInt(eventMessages[2]) <= 7252)) filename = "elog/"+"hw_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 7252) && (Integer.parseInt(eventMessages[2]) <= 9420)) filename = "elog/"+"hw_"+"elogtext_"+1+".xml";
                        if (Integer.parseInt(eventMessages[2]) > 9420) filename = "elog/"+"hw_"+"elogtext_"+1+".xml";
                        break;
                    case 4:
                        if (Integer.parseInt(eventMessages[2]) <= 96) filename = "elog/"+"arl_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 96) && (Integer.parseInt(eventMessages[2]) <= 224)) filename = "elog/"+"arl_"+"elogtext_"+2+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 224) && (Integer.parseInt(eventMessages[2]) <= 537)) filename = "elog/"+"arl_"+"elogtext_"+3+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 537) && (Integer.parseInt(eventMessages[2]) <= 651)) filename = "elog/"+"arl_"+"elogtext_"+4+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 651) && (Integer.parseInt(eventMessages[2]) <= 774)) filename = "elog/"+"arl_"+"elogtext_"+5+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 774) && (Integer.parseInt(eventMessages[2]) <= 1414)) filename = "elog/"+"arl_"+"elogtext_"+6+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 1414) && (Integer.parseInt(eventMessages[2]) <= 1513)) filename = "elog/"+"arl_"+"elogtext_"+7+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 1513) && (Integer.parseInt(eventMessages[2]) <= 1606)) filename = "elog/"+"arl_"+"elogtext_"+8+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 1606) && (Integer.parseInt(eventMessages[2]) <= 1712)) filename = "elog/"+"arl_"+"elogtext_"+9+".xml";
                        if (Integer.parseInt(eventMessages[2]) > 1712) filename = "elog/"+"arl_"+"elogtext_"+10+".xml";
                        break;
                    case 5:
                        if (Integer.parseInt(eventMessages[2]) <= 193) filename = "elog/"+"moc_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 193) && (Integer.parseInt(eventMessages[2]) <= 294)) filename = "elog/"+"moc_"+"elogtext_"+2+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 294) && (Integer.parseInt(eventMessages[2]) <= 382)) filename = "elog/"+"moc_"+"elogtext_"+3+".xml";
                        if (Integer.parseInt(eventMessages[2]) > 382) filename = "elog/"+"moc_"+"elogtext_"+4+".xml";
                       break;
                    case 7:
                        if (Integer.parseInt(eventMessages[2]) <= 1231) filename = "elog/"+"io_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 1231) && (Integer.parseInt(eventMessages[2]) <= 1356)) filename = "elog/"+"io_"+"elogtext_"+2+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 1356) && (Integer.parseInt(eventMessages[2]) <= 1463)) filename = "elog/"+"io_"+"elogtext_"+3+".xml";
                        if (Integer.parseInt(eventMessages[2]) > 1463) filename = "elog/"+"io_"+"elogtext_"+4+".xml";
                        break;
                    default:
                        filename = "";
                }
                InputStream in_s = getAssets().open(filename);
                xmlParser.setInput(in_s, null);
                String name = " ";
                boolean messageEntryFound = false;
                boolean messageReadOk = false;
                int event = xmlParser.getEventType();

                while ((event != XmlPullParser.END_DOCUMENT) && !messageReadOk)
                {
                    switch (event){
                        case XmlPullParser.START_TAG:
                            name = xmlParser.getName();
                            if(name.equals("Message") && !messageEntryFound) {
                                String messageNumber = xmlParser.getAttributeValue(null,"number");
                                if (messageNumber.equals(eventMessages[2])){
                                    messageEntryFound = true;
                                }
                            }
                            if(messageEntryFound && name.equals("Title")) eventDescription[0] = xmlParser.nextText();
                            if(messageEntryFound && name.equals("Description")){
                                eventDescription[1] = xmlParser.nextText();
                                eventDescription[1] = String.format(eventDescription[1], eventMessages[4], eventMessages[5], eventMessages[6], eventMessages[7], eventMessages[8]);
                            }
                            if(messageEntryFound && name.equals("Actions")){
                                eventDescription[2] = xmlParser.nextText();
                                eventDescription[2] = String.format(eventDescription[2], eventMessages[4], eventMessages[5], eventMessages[6], eventMessages[7], eventMessages[8]);
                            }
                            if(messageEntryFound && name.equals("Consequences")){
                                eventDescription[3] = xmlParser.nextText();
                                eventDescription[3] = String.format(eventDescription[3], eventMessages[4], eventMessages[5], eventMessages[6], eventMessages[7], eventMessages[8]);
                            }
                            if(messageEntryFound && name.equals("Causes")){
                                eventDescription[4] = xmlParser.nextText();
                                eventDescription[4] = String.format(eventDescription[4], eventMessages[4], eventMessages[5], eventMessages[6], eventMessages[7], eventMessages[8]);
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            name = xmlParser.getName();
                            if(name.equals("Message") && messageEntryFound){
                                messageReadOk = true;
                            }
                           break;
                    }
                    if (!messageReadOk){
                        event = xmlParser.next();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            // Get error type to set the correct image (warning, error, etc.)
            eventDescription[5] = eventMessages[3];
            return eventDescription;
        }

        protected void onPostExecute(String[] result) {
            if (addEvents) setEventList(result[0]); // Save event to list and show header in list view

            // Show dialog, when the motor is in off state
            if ((eventMessages[0].equals("0")) || !addEvents){
                customEventDialog.showDialog(result);
                if (addEvents) vibrator.vibrate(800);
            }
            Log.d("onPostExecute1", eventMessages[0]);
        }

    }
}
