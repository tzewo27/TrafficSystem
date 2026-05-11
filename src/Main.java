

import database.DatabaseConnection;
import gui.LoginScreen;
import gui.SplashScreen;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        // Show splash screen
        SwingUtilities.invokeLater(() -> new SplashScreen());

        // Wait for splash
        Thread.sleep(3000);

        // Make sure all 4 tables exist
        DatabaseConnection.createTables();

        // Launch login
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}
