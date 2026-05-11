// for GUI
//terminal 1
//cd c:\Users\tizit\OneDrive\Desktop\AP-project\TrafficSystem
//javac -cp "lib\*;src" -d bin src\Main.java src\models\*.java src\database\*.java src\network\*.java src\gui\*.java
//java -cp "bin;lib\*" network.TrafficServer
//terminal2
//cd c:\Users\tizit\OneDrive\Desktop\AP-project\TrafficSystem
//java -cp "bin;lib\*" Main


//for RMI
// Terminal 1 — compile everything:
// cd c:\Users\tizit\OneDrive\Desktop\AP-project\TrafficSystem
// javac -cp "lib\*;src" -d bin src\Main.java src\models\*.java src\database\*.java src\network\*.java src\gui\*.java
// Terminal 2 — start RMI server:
// java -cp "bin;lib\*" network.RMIServer
// You should see:
// Setting up database...
// RMI Server started on port 1099
// Service registered as: IncidentService
// Waiting for remote calls...
// Terminal 3 — start TCP server:
// java -cp "bin;lib\*" network.TrafficServer
// Terminal 4 — run the GUI:
// java -cp "bin;lib\*" Main
//http://localhost:8080

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