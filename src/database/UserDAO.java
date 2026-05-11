package database;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// This file knows how to save and find Users in the database
// DAO = Data Access Object (fancy name for: talks to the database)

public class UserDAO {

    // SAVE a new user to the database
    // Called when someone registers for the first time
    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        // The ? marks are placeholders — we fill them in below
        // This protects against hackers (SQL injection)

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            int rows = ps.executeUpdate(); // actually runs the INSERT
            return rows > 0; // true if it worked

        } catch (SQLException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    // CHECK LOGIN — does this email + password exist in database?
    // Returns the User if found, null if wrong email/password
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery(); // runs the SELECT

            if (rs.next()) { // if we found a matching row
                User user = new User(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                );
                user.setId(rs.getInt("id"));
                return user; // login success!
            }

        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }

        return null; // login failed
    }

    // GET ALL USERS — used by admin dashboard
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) { // loop through every row
                User user = new User(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                );
                user.setId(rs.getInt("id"));
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("Error getting users: " + e.getMessage());
        }

        return users;
    }
}