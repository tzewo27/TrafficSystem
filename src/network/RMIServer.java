package network;

import database.DatabaseConnection;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// This starts the RMI registry on port 1099
// Think of it like a phonebook — clients look up
// "IncidentService" and get connected to our implementation

public class RMIServer {

    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "IncidentService";

    public static void main(String[] args) {
        try {
            // Make sure database tables exist
            System.out.println("Setting up database...");
            DatabaseConnection.createTables();

            // Create the service implementation
            IncidentService service = new IncidentServiceImpl();

            // Create the RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);

            // Register our service with the name "IncidentService"
            // Clients will look it up by this exact name
            registry.rebind(SERVICE_NAME, service);

            System.out.println("RMI Server started on port " + RMI_PORT);
            System.out.println("Service registered as: " + SERVICE_NAME);
            System.out.println("Waiting for remote calls...");
            System.out.println("Press Ctrl+C to stop.");

            // Keep running forever
            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            System.out.println("RMI Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}