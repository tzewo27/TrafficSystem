package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// This class is like a phone — it knows the number to call MySQL
// Every time we want to talk to the database, we use this

public class DatabaseConnection {

    // These are the details of YOUR MySQL on XAMPP
    // localhost = your own computer
    // 3306 = the port MySQL runs on (XAMPP default)
    // traffic_system = the database name you created in phpMyAdmin
    private static final String URL      = "jdbc:mysql://localhost:3306/traffic_system";
    private static final String USER     = "root";   // XAMPP default username
    private static final String PASSWORD = "";        // XAMPP default = no password

    // This method gives us a connection to MySQL
    // We call it like: Connection conn = DatabaseConnection.getConnection();
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // This method creates ALL our tables automatically
    // Run this ONCE when the project starts for the first time
    public static void createTables() {
        // We use try-with-resources — Java automatically closes
        // the connection when we're done (no memory leaks)
        try (Connection conn = getConnection();
             var stmt = conn.createStatement()) {

            // TABLE 1: users
            // Every person who uses the system
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id       INT AUTO_INCREMENT PRIMARY KEY,
                    name     VARCHAR(100) NOT NULL,
                    email    VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    role     VARCHAR(20)  NOT NULL
                )
            """);

            // TABLE 2: incidents
            // Every accident or traffic problem reported
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS incidents (
                    id            INT AUTO_INCREMENT PRIMARY KEY,
                    type          VARCHAR(50)  NOT NULL,
                    location      VARCHAR(200) NOT NULL,
                    severity      VARCHAR(20)  NOT NULL,
                    description   TEXT,
                    status        VARCHAR(20)  DEFAULT 'Open',
                    reported_by   INT,
                    reported_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (reported_by) REFERENCES users(id)
                )
            """);

            // TABLE 3: alerts
            // Notifications sent to officers when incidents happen
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS alerts (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    incident_id INT,
                    message     TEXT NOT NULL,
                    target_role VARCHAR(20),
                    is_read     BOOLEAN DEFAULT FALSE,
                    sent_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (incident_id) REFERENCES incidents(id)
                )
            """);

            // TABLE 4: first_aid_guides
            // The life-saving instructions — your unique feature!
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS first_aid_guides (
                    id        INT AUTO_INCREMENT PRIMARY KEY,
                    title     VARCHAR(100) NOT NULL,
                    category  VARCHAR(50)  NOT NULL,
                    steps     TEXT         NOT NULL,
                    emergency VARCHAR(20)
                )
            """);

            System.out.println("All 4 tables created successfully!");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}