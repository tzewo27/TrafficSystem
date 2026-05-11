package network;

import models.Incident;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// This is the REMOTE INTERFACE
// It defines what methods the client can call on the server
// Every method must throw RemoteException — that's the RMI rule

public interface IncidentService extends Remote {

    // Report a new incident remotely
    String reportIncident(String type, String location,
                          String severity, String description,
                          int reportedById) throws RemoteException;

    // Get all incidents from the database remotely
    List<Incident> getAllIncidents() throws RemoteException;

    // Get only critical open incidents
    List<Incident> getCriticalIncidents() throws RemoteException;

    // Assign an officer to an incident
    String assignOfficer(int incidentId,
                         String officerName) throws RemoteException;

    // Update incident status remotely
    String updateStatus(int incidentId,
                        String newStatus) throws RemoteException;

    // Get total incident count (for analytics)
    int getTotalCount() throws RemoteException;

    // Get count by severity
    int getCountBySeverity(String severity) throws RemoteException;
}