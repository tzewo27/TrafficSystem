package network;

import database.DatabaseConnection;
import utils.Logger;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TrafficServer {

    private static final int PORT = 5000;

    private static List<ClientHandler> connectedClients =
        new CopyOnWriteArrayList<>();

    private static UDPServer udpServer = new UDPServer();

    public static void main(String[] args) {

        System.out.println("Setting up database...");
        DatabaseConnection.createTables();

        Logger.logServerStart(PORT);

        // Start web dashboard
        WebServer.start();

        startWatchdogThread();

        System.out.println("TCP Server started on port " + PORT);
        System.out.println("Waiting for clients...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String ip = clientSocket.getInetAddress().toString();
                System.out.println("New connection: " + ip);
                Logger.logClientConnected(ip);

                ClientHandler handler =
                    new ClientHandler(clientSocket, connectedClients);
                connectedClients.add(handler);

                Thread t = new Thread(handler);
                t.start();

                System.out.println("Active connections: "
                    + connectedClients.size());
            }
        } catch (IOException e) {
            Logger.error("SERVER", "Crashed: " + e.getMessage());
        }
    }

    public static void broadcastEmergency(String location, String type) {
        String alert = "EMERGENCY:" + type + " at " + location
            + " — All units respond immediately!";
        udpServer.broadcast(alert);
        Logger.logUDP(alert);
    }

    private static void startWatchdogThread() {
        Thread watchdog = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000);
                    database.IncidentDAO dao = new database.IncidentDAO();
                    var critical = dao.getCriticalIncidents();
                    if (!critical.isEmpty()) {
                        String alert = "ALERT:WARNING — "
                            + critical.size()
                            + " critical incident(s) still unresolved!";
                        Logger.logAlert("Watchdog: "
                            + critical.size() + " unresolved");
                        udpServer.broadcast(alert);
                        for (ClientHandler c : connectedClients)
                            c.sendMessage(alert);
                    }
                } catch (InterruptedException e) { break; }
            }
        });
        watchdog.setDaemon(true);
        watchdog.start();
        System.out.println("Watchdog started.");
    }
}