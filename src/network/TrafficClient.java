package network;

import java.io.*;
import java.net.*;

public class TrafficClient {

    private static final String HOST = "localhost";
    private static final int    PORT = 5000;

    private Socket mainSocket;
    private BufferedReader mainIn;
    private PrintWriter mainOut;

    private Socket reportSocket;
    private BufferedReader reportIn;
    private PrintWriter reportOut;

    private RMIClient rmiClient;

    public boolean connect() {
        try {
            mainSocket = new Socket(HOST, PORT);
            mainIn  = new BufferedReader(new InputStreamReader(
                mainSocket.getInputStream()));
            mainOut = new PrintWriter(
                mainSocket.getOutputStream(), true);
            mainIn.readLine(); // welcome message

            reportSocket = new Socket(HOST, PORT);
            reportIn  = new BufferedReader(new InputStreamReader(
                reportSocket.getInputStream()));
            reportOut = new PrintWriter(
                reportSocket.getOutputStream(), true);
            reportIn.readLine(); // welcome message

            rmiClient = new RMIClient();
            rmiClient.connect();

            System.out.println("Connected to server successfully");
            return true;
        } catch (IOException e) {
            System.out.println("Cannot connect: " + e.getMessage());
            return false;
        }
    }

    // Main socket — for login, get incidents, update status
    public synchronized String sendAndReceive(String message) {
        try {
            if (message != null && !message.isEmpty())
                mainOut.println(message);
            return mainIn.readLine();
        } catch (IOException e) {
            System.out.println("Main socket error: " + e.getMessage());
            return null;
        }
    }

    // Dedicated report socket — ONLY for submitting incidents
    public String sendReport(String message) {
        try {
            synchronized (reportOut) {
                reportOut.println(message);
                return reportIn.readLine();
            }
        } catch (IOException e) {
            System.out.println("Report error: " + e.getMessage());
            return null;
        }
    }

    public RMIClient getRMI() { return rmiClient; }

    public void disconnect() {
        try {
            if (mainSocket   != null) mainSocket.close();
            if (reportSocket != null) reportSocket.close();
        } catch (IOException e) {}
    }
}