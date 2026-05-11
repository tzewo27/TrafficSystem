package database;

import models.Incident;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Handles all incident database operations
// Save, get all, get by severity, update status

public class IncidentDAO {

    // SAVE a new incident (when citizen submits a report)
    public boolean saveIncident(Incident incident) {
        String sql = """
            INSERT INTO incidents
            (type, location, severity, description, status, reported_by)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, incident.getType());
            ps.setString(2, incident.getLocation());
            ps.setString(3, incident.getSeverity());
            ps.setString(4, incident.getDescription());
            ps.setString(5, incident.getStatus());
            ps.setInt   (6, incident.getReportedById());

            int rows = ps.executeUpdate();

            // Get the auto-generated ID from MySQL and put it on our object
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) incident.setId(keys.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error saving incident: " + e.getMessage());
        }
        return false;
    }

    // GET ALL incidents — for the live dashboard
    public List<Incident> getAllIncidents() {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT * FROM incidents ORDER BY reported_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Incident inc = new Incident(
                    rs.getString("type"),
                    rs.getString("location"),
                    rs.getString("severity"),
                    rs.getString("description"),
                    rs.getInt("reported_by")
                );
                inc.setId(rs.getInt("id"));
                inc.setStatus(rs.getString("status"));
                list.add(inc);
            }

        } catch (SQLException e) {
            System.out.println("Error getting incidents: " + e.getMessage());
        }
        return list;
    }

    // GET only Critical incidents — for emergency alerts
    public List<Incident> getCriticalIncidents() {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE severity='Critical' AND status='Open'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Incident inc = new Incident(
                    rs.getString("type"),
                    rs.getString("location"),
                    rs.getString("severity"),
                    rs.getString("description"),
                    rs.getInt("reported_by")
                );
                inc.setId(rs.getInt("id"));
                inc.setStatus(rs.getString("status"));
                list.add(inc);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    // UPDATE status — when officer clicks "Resolved"
    public boolean updateStatus(int incidentId, String newStatus) {
        String sql = "UPDATE incidents SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, incidentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
            return false;
        }
    }
}