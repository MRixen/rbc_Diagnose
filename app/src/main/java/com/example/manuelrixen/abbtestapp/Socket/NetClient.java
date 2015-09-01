package com.example.manuelrixen.abbtestapp.Socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetClient {

    /**
     * Maximum size of buffer
     */
    public static final int BUFFER_SIZE = 2048;
    private Socket socket = null;
    private Socket socket2 = null;
    private Socket socket3 = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private BufferedReader in2 = null;

    private String host = null;
    private String macAddress = null;
    private int port = 8888;
    private int cntr = 0;
    private String msgString = "";
    private String msgString2 = "";


    /**
     * Constructor with Host, Port and MAC Address
     * @param host
     * @param port
     */
    public NetClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connectWithServer() {
        try {
            if (socket == null) {
                socket = new Socket(this.host, this.port);
                //socket3 = new Socket(this.host, this.port);
                //out = new PrintWriter(socket3.getOutputStream(), true);
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
        int message = 0;
        String[] msgArray = new String[] {"",""};
        boolean bTemp = false;
        try {
            while(true) {
                    try {
                        // TODO Close application when in read-routine

                        message = in.read();
                        if (message != -1) {
                            String msgTemp = String.valueOf(Character.toChars(message));

                            if (!msgTemp.equals(";") && !msgTemp.equals(":"))
                                msgString = msgString + String.valueOf(Character.toChars(message));
                            else if (msgTemp.equals(";")) {
                                msgArray[0] = msgString;
                                msgString = "";
                                return msgArray;
                            } else if (msgTemp.equals(":")) {
                                message = in.read();
                                // TODO Read number from 9 with two values like 10,11,12, etc.
                                msgArray[1] = String.valueOf(Character.toChars(message));
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
