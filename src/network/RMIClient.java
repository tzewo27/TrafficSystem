package network;

import models.Incident;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.ArrayList;

// The GUI uses this to call methods on the server remotely
// It's like a TV remote — you press a button here,
// something happens over there on the server

public class RMIClient {

    private static final String HOST         = "localhost";
    private static final int    RMI_PORT     = 1099;
    private static final String SERVICE_NAME = "IncidentService";

    private IncidentService service;

    // Connect to the RMI server
    public boolean connect() {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, RMI_PORT);
            service = (IncidentService) registry.lookup(SERVICE_NAME);
            System.out.println("RMI Client connected to " + SERVICE_NAME);
            return true;
        } catch (Exception e) {
            System.out.println("RMI connection failed: " + e.getMessage());
            return false;
        }
    }

    // Report a new incident
    public String reportIncident(String type, String location,
                                  String severity, String description,
                                  int userId) {
        try {
            return service.reportIncident(
                type, location, severity, description, userId);
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }

    // Get all incidents for the dashboard
    public List<Incident> getAllIncidents() {
        try {
            return service.getAllIncidents();
        } catch (Exception e) {
            System.out.println("RMI error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get critical incidents only
    public List<Incident> getCriticalIncidents() {
        try {
            return service.getCriticalIncidents();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Assign an officer to an incident
    public String assignOfficer(int incidentId, String officerName) {
        try {
            return service.assignOfficer(incidentId, officerName);
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }

    // Update status of an incident
    public String updateStatus(int incidentId, String newStatus) {
        try {
            return service.updateStatus(incidentId, newStatus);
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }

    // Get total incident count
    public int getTotalCount() {
        try {
            return service.getTotalCount();
        } catch (Exception e) {
            return 0;
        }
    }

    // Get count by severity — used for analytics
    public int getCountBySeverity(String severity) {
        try {
            return service.getCountBySeverity(severity);
        } catch (Exception e) {
            return 0;
        }
    }

    // Check if connected
    public boolean isConnected() {
        return service != null;
    }
}