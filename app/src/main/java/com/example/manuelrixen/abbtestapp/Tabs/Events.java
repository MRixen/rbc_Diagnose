package com.example.manuelrixen.abbtestapp.Tabs;

/**
 * Created by Manuel.Rixen on 23.08.2015.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseClass;
import com.example.manuelrixen.abbtestapp.CustomDialog;
import com.example.manuelrixen.abbtestapp.R;
import com.example.manuelrixen.abbtestapp.Socket.Receiver;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Events extends Fragment implements Receiver.FirstEventListener, Receiver.SecondEventListener  {

    private TextView eventViewer;
    private BaseClass baseClass = new BaseClass();
    private boolean firstStart = true;
    private XmlPullParserFactory xmlFactoryObject;
    private XmlPullParser xmlParser;
    private CustomDialog customDialog;

    private boolean firstRun = true;
    private boolean initOk = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_4, container, false);

        eventViewer = (TextView) rootView.findViewById(R.id.eventTextField);
        eventViewer.setMovementMethod(new ScrollingMovementMethod());

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlParser = xmlFactoryObject.newPullParser();
            Log.d("xml_parser", "created");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.d("xml_parser", "error1");
        }
        customDialog = new CustomDialog(getActivity());

        // For testing
//        String[] tempMessage = new String[] {"1","0","1"};
//        new XMLParsing().execute(tempMessage);


        return rootView;
    }

    public Receiver.FirstEventListener getFirstEventListener() {
        return this;
    }

    @Override
    public void onError1() {
        Log.d("Console", "onError1");
        eventViewer.setText("Cant connect to server.");
    }

    @Override
    public void onEvent1(boolean normal, String msg, String msgType) {
        if(initOk){
            showEvent(msgType, msg, normal);
        }
    }

    @Override
    public void onError2() {
        Log.d("Console", "onError2");
        eventViewer.setText("Cant connect to server.");
    }

    @Override
    public void onEvent2(boolean normal, String msg, String msgType) {
        if(initOk){
            showEvent(msgType, msg, normal);
        }
    }

    private void showEvent(String msgType, String eventMessage, boolean normal) {
        // Split event message to get the motors state (first entry) the event domain (second entry) and the event number (third entry)
        String[] tempMessage = eventMessage.split("_");

        // TODO Add code to show warning messages, info messages, etc. in textview (not in dialog)
        // TODO Switch automatically to event tab when a dialog message appear
        if (msgType.equals("e") && tempMessage[0].equals("1")){
            // Show dialog, when the motors in off state
            Log.d("showEvent", "tempMessage[0]: " + tempMessage[0]);
            new XMLParsing().execute(tempMessage);
        }
        else if (msgType.equals("e") &&  tempMessage[0].equals("0")) {
            // Show event messages in text view (only warnings and infos)
            // eventViewer.setText(msgTemp + "\n" + eventViewer.getText());
        }
    }

    public void setInitOk() {
        this.initOk = true;
    }

    private class XMLParsing extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... eventMessages) {
            String[] eventDescription = new String[] {"", "", ""};
            String filename = "";
            try {
                // Choose between the .xml files
                switch (Integer.parseInt(eventMessages[1])){
                    case 1:
                        if (Integer.parseInt(eventMessages[2]) <= 175) filename = "elog/"+"opr_"+"elogtext_"+1+".xml";
                        if ((Integer.parseInt(eventMessages[2]) > 175) && (Integer.parseInt(eventMessages[2]) <= 1231)) filename = "elog/"+"opr_"+"elogtext_"+2+".xml";
                        if (Integer.parseInt(eventMessages[2]) > 1231) filename = "elog/"+"opr_"+"elogtext_"+3+".xml";
                        Log.d("showEvent", "filename: " + filename);
                        Log.d("showEvent", "eventMessages[2]: " + eventMessages[2]);
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
                        // For testing
//                        filename = "xmldata/testxml.xml";
                        filename = "";
                }
                InputStream in_s = getActivity().getAssets().open(filename);
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
                            // TODO Add Consequences as attribute, etc.
                            // TODO Add possiblity to insert the arguments inside xml-string
                            if(messageEntryFound && name.equals("Title")) eventDescription[0] = xmlParser.nextText();
                            if(messageEntryFound && name.equals("Description")) eventDescription[1] = xmlParser.nextText();
//                            if(messageEntryFound && name.equals("Actions")) eventDescription[2] = xmlParser.nextText();
                            break;

                        case XmlPullParser.END_TAG:
                            name = xmlParser.getName();
                            if(name.equals("Message") && messageEntryFound){
                                messageReadOk = true;
                                Log.d("xmlParser", "messageReadOk");
                            }
                           break;
                    }
                    if (!messageReadOk){
                        event = xmlParser.next();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("xml_parser1", String.valueOf(e));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Log.d("xml_parser2", String.valueOf(e));
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("xml_parser3", String.valueOf(e));
            } catch (NullPointerException e){
                e.printStackTrace();
                Log.d("xml_parser4", String.valueOf(e));
            }
            return eventDescription;
        }

        protected void onPostExecute(String[] result) {
            customDialog.showDialog(result);
        }

    }
}
