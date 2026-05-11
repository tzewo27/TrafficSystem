package models;

import java.time.LocalDateTime;

// An Alert is a message the system sends to officers/admins
// when a new incident is reported

public class Alert {

    private int id;
    private int incidentId;   // which incident triggered this alert
    private String message;   // "Critical accident on Bole Road!"
    private String targetRole;// who should see this: "officer", "admin", or "all"
    private boolean isRead;   // has the officer seen it yet?
    private LocalDateTime sentAt;

    public Alert(int incidentId, String message, String targetRole) {
        this.incidentId = incidentId;
        this.message = message;
        this.targetRole = targetRole;
        this.isRead = false;          // starts as unread
        this.sentAt = LocalDateTime.now();
    }

    // Getters
    public int getId()            { return id; }
    public int getIncidentId()    { return incidentId; }
    public String getMessage()    { return message; }
    public String getTargetRole() { return targetRole; }
    public boolean isRead()       { return isRead; }
    public LocalDateTime getSentAt() { return sentAt; }

    // Setters
    public void setId(int id)         { this.id = id; }
    public void setRead(boolean read) { this.isRead = read; }

    @Override
    public String toString() {
        return "Alert[incidentId=" + incidentId + ", to=" + targetRole +
               ", message=" + message + "]";
    }
}