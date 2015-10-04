package com.example.manuelrixen.abbtestapp.Socket;

import android.util.Log;
import android.util.TimingLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetClient {

    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private String host = null;
    private int port;


    public NetClient(String host, int port) {
        this.host = host;
        this.port = port;

    }

    public boolean connectWithServer() {
        try {
            if (socket == null) {
                socket = new Socket(this.host, this.port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void disConnectWithServer() {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            in.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
    }

    public void sendDataWithString(String message) {
        if (message != null) {
   //         out.write(message);
 //           out.flush();

            out.println(message);
        }
    }

    public String[] receiveDataFromServer() {
        String incomingMessage = "";
        String[] msgArray = new String[] {"",""};
        boolean  commandMessage = false;
        boolean  normalMessage = false;
        boolean itsACommand = false;

        try {
            while(true) {
                    try {
                        // TODO Close application when in read-routine

                        // Read and convert message
                        try {
                            incomingMessage = String.valueOf(Character.toChars(in.read()));
                        }
                        catch(NumberFormatException e){
                            incomingMessage = "";
                        }
                        catch(IllegalArgumentException e){
                            incomingMessage = "";
                        }

                        if (incomingMessage.length() != 0) {

                            if (!incomingMessage.equals(":") && commandMessage){
                                itsACommand = true;
                                msgArray[1] += incomingMessage;
                            }

                            if (!incomingMessage.equals(":") && !commandMessage) normalMessage = true;

                            // Check if msg should be read as command
                            if (incomingMessage.equals(":") && !commandMessage){
                                commandMessage = true;
                            }
                            else if(incomingMessage.equals(":") && commandMessage)
                            {
                                if (itsACommand) normalMessage = false;
                                if (!itsACommand) normalMessage = true;
                                commandMessage = false;
                            }


                            // Read message as normal characters
                            if (!incomingMessage.equals(";") && !commandMessage && normalMessage){
                                msgArray[0] += incomingMessage;
                                itsACommand = false;
                            }
                            else if (incomingMessage.equals(";") && !commandMessage && normalMessage){
                                normalMessage = false;
                                return msgArray;
                            }
                        }
                        else return new String[] {" ", " "};

                    } catch (NullPointerException e) {
                        return new String[] {"error", "error"};
                    }
            }
        } catch (IOException e) {
            return new String[] {"error", "error"};
        }

    }
}
