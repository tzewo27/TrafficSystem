package utils;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// This class handles all file logging in the system
// Every important action gets written to a log file
// Uses file locking so multiple threads don't corrupt the file
// This covers Chapter 2 — System Programming

public class Logger {

    private static final String LOG_DIR  = "logs";
    private static final String LOG_FILE = "logs/traffic_system.log";
    private static final String ERR_FILE = "logs/errors.log";

    private static final DateTimeFormatter FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Create the logs folder when Logger is first used
    static {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
            log("SYSTEM", "Logger initialized — system started");
        } catch (IOException e) {
            System.out.println("Could not create logs folder: "
                + e.getMessage());
        }
    }

    // ── MAIN LOG METHOD ───────────────────────────────────────────
    // category = who is logging e.g. "LOGIN", "INCIDENT", "ALERT"
    // message  = what happened
    public static void log(String category, String message) {
        String entry = buildEntry(category, message);
        writeToFile(LOG_FILE, entry);
        System.out.println("[LOG] " + entry);
    }

    // ── ERROR LOG ─────────────────────────────────────────────────
    public static void error(String category, String message) {
        String entry = buildEntry("ERROR-" + category, message);
        writeToFile(ERR_FILE, entry);
        writeToFile(LOG_FILE, entry);
        System.out.println("[ERROR] " + entry);
    }

    // ── SPECIFIC LOG HELPERS ──────────────────────────────────────
    // Call these from anywhere in your system

    public static void logLogin(String userName, String role) {
        log("LOGIN", "User logged in: " + userName + " [" + role + "]");
    }

    public static void logLogout(String userName) {
        log("LOGOUT", "User logged out: " + userName);
    }

    public static void logIncidentReported(String type,
            String location, String severity, String reportedBy) {
        log("INCIDENT", "New incident reported by " + reportedBy
            + " — Type: " + type
            + " | Location: " + location
            + " | Severity: " + severity);
    }

    public static void logIncidentResolved(int incidentId,
            String resolvedBy) {
        log("RESOLVED", "Incident #" + incidentId
            + " resolved by " + resolvedBy);
    }

    public static void logAlert(String message) {
        log("ALERT", message);
    }

    public static void logRMI(String method, String detail) {
        log("RMI", "Remote call: " + method + " — " + detail);
    }

    public static void logUDP(String message) {
        log("UDP", "Broadcast sent: " + message);
    }

    public static void logServerStart(int port) {
        log("SERVER", "TCP Server started on port " + port);
    }

    public static void logClientConnected(String ipAddress) {
        log("SERVER", "Client connected from: " + ipAddress);
    }

    public static void logClientDisconnected(String clientName) {
        log("SERVER", "Client disconnected: " + clientName);
    }

    // ── READ LOGS — for showing in admin dashboard ─────────────────
    public static String readLogs(int lastNLines) {
        try {
            java.util.List<String> lines =
                Files.readAllLines(Paths.get(LOG_FILE));

            int start = Math.max(0, lines.size() - lastNLines);
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < lines.size(); i++) {
                sb.append(lines.get(i)).append("\n");
            }
            return sb.toString();

        } catch (IOException e) {
            return "Could not read logs: " + e.getMessage();
        }
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────

    private static String buildEntry(String category, String message) {
        String time = LocalDateTime.now().format(FORMAT);
        return "[" + time + "] [" + category + "] " + message;
    }

    // Write to file with file locking so threads don't clash
    // This is Chapter 2 — File Locking!
    private static void writeToFile(String filePath, String entry) {
        try (FileOutputStream fos =
                new FileOutputStream(filePath, true); // true = append
             FileChannel channel = fos.getChannel();
             FileLock lock = channel.lock()) { // LOCK the file

            // Write the entry
            byte[] data = (entry + System.lineSeparator()).getBytes();
            fos.write(data);

            // Lock releases automatically when try block ends

        } catch (IOException e) {
            System.out.println("Logging error: " + e.getMessage());
        }
    }
}