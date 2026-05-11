package network;

import database.IncidentDAO;
import models.Incident;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

// This is the IMPLEMENTATION of the remote interface
// It extends UnicastRemoteObject — that's what makes it work over the network
// All the real database logic lives here on the SERVER

public class IncidentServiceImpl
        extends UnicastRemoteObject
        implements IncidentService {

    private IncidentDAO incidentDAO;

    // Constructor must throw RemoteException — RMI requirement
    public IncidentServiceImpl() throws RemoteException {
        super();
        this.incidentDAO = new IncidentDAO();
    }

    // Called remotely when citizen submits a report
    @Override
    public String reportIncident(String type, String location,
                                  String severity, String description,
                                  int reportedById) throws RemoteException {
        Incident incident = new Incident(
            type, location, severity, description, reportedById
        );
        boolean saved = incidentDAO.saveIncident(incident);

        if (saved) {
            System.out.println("[RMI] New incident reported: "
                + type + " at " + location);
            return "SUCCESS:Incident saved with ID " + incident.getId();
        } else {
            return "ERROR:Could not save incident";
        }
    }

    // Called remotely to get all incidents for the dashboard
    @Override
    public List<Incident> getAllIncidents() throws RemoteException {
        System.out.println("[RMI] Client requested all incidents");
        return incidentDAO.getAllIncidents();
    }

    // Called remotely to get critical incidents for alerts
    @Override
    public List<Incident> getCriticalIncidents() throws RemoteException {
        System.out.println("[RMI] Client requested critical incidents");
        return incidentDAO.getCriticalIncidents();
    }

    // Called remotely by dispatcher to assign an officer
    // This is the most impressive RMI feature — dispatcher on one
    // computer assigns officer on another computer!
    @Override
    public String assignOfficer(int incidentId,
                                 String officerName) throws RemoteException {
        boolean updated = incidentDAO.updateStatus(incidentId, "In Progress");
        if (updated) {
            System.out.println("[RMI] Officer " + officerName
                + " assigned to incident " + incidentId);
            return "SUCCESS:Officer " + officerName
                + " assigned to incident " + incidentId;
        } else {
            return "ERROR:Could not assign officer";
        }
    }

    // Called remotely to update incident status
    @Override
    public String updateStatus(int incidentId,
                                String newStatus) throws RemoteException {
        boolean updated = incidentDAO.updateStatus(incidentId, newStatus);
        System.out.println("[RMI] Incident " + incidentId
            + " status updated to " + newStatus);
        return updated ? "SUCCESS:Status updated" : "ERROR:Update failed";
    }

    // Called remotely for analytics
    @Override
    public int getTotalCount() throws RemoteException {
        return incidentDAO.getAllIncidents().size();
    }

    // Called remotely for analytics by severity
    @Override
    public int getCountBySeverity(String severity) throws RemoteException {
        return (int) incidentDAO.getAllIncidents()
            .stream()
            .filter(i -> i.getSeverity().equals(severity))
            .count();
    }
}