package network;

import database.IncidentDAO;
import database.UserDAO;
import models.Incident;
import models.User;
import utils.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private List<ClientHandler> allClients;
    private String clientName = "Unknown";

    public ClientHandler(Socket socket, List<ClientHandler> allClients) {
        this.socket = socket;
        this.allClients = allClients;
        try {
            in  = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            out.println("WELCOME:Connected to Traffic System Server");
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("[Server received]: " + message);
                handleMessage(message);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + clientName);
        } finally {
            Logger.logClientDisconnected(clientName);
            allClients.remove(this);
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private synchronized void handleMessage(String message) {
        if (message == null || message.trim().isEmpty()) return;

        String[] parts  = message.split(":", 2);
        String command  = parts[0].trim();
        String data     = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {

            case "REPORT_INCIDENT": {
                String[] fields = data.split("\\|", 5);
                if (fields.length >= 5) {
                    try {
                        Incident inc = new Incident(
                            fields[0].trim(),
                            fields[1].trim(),
                            fields[2].trim(),
                            fields[3].trim(),
                            Integer.parseInt(fields[4].trim())
                        );
                        IncidentDAO dao = new IncidentDAO();
                        boolean saved   = dao.saveIncident(inc);
                        if (saved) {
                            Logger.logIncidentReported(
                                fields[0], fields[1],
                                fields[2], clientName);
                            out.println("SUCCESS:Incident #"
                                + inc.getId() + " saved!");
                            if ("Critical".equals(fields[2].trim())) {
                                broadcastToAll("ALERT:Critical at "
                                    + fields[1]);
                                TrafficServer.broadcastEmergency(
                                    fields[1], fields[0]);
                            }
                        } else {
                            out.println("ERROR:Database save failed");
                        }
                    } catch (Exception e) {
                        out.println("ERROR:" + e.getMessage());
                    }
                } else {
                    out.println("ERROR:Invalid data format");
                }
                break;
            }

            case "GET_INCIDENTS": {
                IncidentDAO dao = new IncidentDAO();
                List<Incident> list = dao.getAllIncidents();
                out.println("INCIDENT_COUNT:" + list.size());
                for (Incident i : list) {
                    out.println("INCIDENT:"
                        + i.getId()          + "|"
                        + i.getType()        + "|"
                        + i.getLocation()    + "|"
                        + i.getSeverity()    + "|"
                        + i.getStatus());
                }
                break;
            }

            case "LOGIN": {
                String[] d = data.split("\\|", 2);
                if (d.length == 2) {
                    UserDAO userDAO = new UserDAO();
                    User user = userDAO.login(d[0].trim(), d[1].trim());
                    if (user != null) {
                        clientName = user.getName();
                        Logger.logLogin(user.getName(), user.getRole());
                        out.println("LOGIN_SUCCESS:"
                            + user.getId()   + "|"
                            + user.getName() + "|"
                            + user.getRole());
                    } else {
                        Logger.error("LOGIN", "Failed: " + d[0]);
                        out.println("LOGIN_FAIL:Wrong email or password");
                    }
                }
                break;
            }

            case "UPDATE_STATUS": {
                String[] d = data.split("\\|", 2);
                if (d.length == 2) {
                    IncidentDAO dao = new IncidentDAO();
                    boolean ok = dao.updateStatus(
                        Integer.parseInt(d[0].trim()), d[1].trim());
                    if (ok) {
                        if ("Resolved".equals(d[1].trim()))
                            Logger.logIncidentResolved(
                                Integer.parseInt(d[0].trim()), clientName);
                        out.println("SUCCESS:Status updated");
                        broadcastToAll("STATUS_UPDATE:"
                            + d[0] + "|" + d[1]);
                    } else {
                        out.println("ERROR:Update failed");
                    }
                }
                break;
            }

            default:
                out.println("ERROR:Unknown command " + command);
        }
    }

    private void broadcastToAll(String message) {
        for (ClientHandler c : allClients) c.out.println(message);
    }

    public void sendMessage(String message) {
        if (out != null) out.println(message);
    }
}