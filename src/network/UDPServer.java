package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// UDP Server — sends emergency broadcasts to ALL clients at once
// Unlike TCP which is one-to-one, UDP is one-to-MANY
// Perfect for emergency alerts

public class UDPServer {

    private static final int UDP_PORT = 6000;
    private DatagramSocket socket;

    public UDPServer() {
        try {
            socket = new DatagramSocket();
            System.out.println("UDP Server ready on port " + UDP_PORT);
        } catch (Exception e) {
            System.out.println("UDP Server error: " + e.getMessage());
        }
    }

    // Broadcast a message to ALL clients listening on UDP port 6000
    // This is called automatically when a critical incident is reported
    public void broadcast(String message) {
        try {
            byte[] data = message.getBytes();

            // InetAddress.getByName("255.255.255.255") = broadcast address
            // This means: send to EVERYONE on the network
            InetAddress broadcastAddress =
                InetAddress.getByName("255.255.255.255");

            DatagramPacket packet = new DatagramPacket(
                data, data.length, broadcastAddress, UDP_PORT
            );

            socket.setBroadcast(true);
            socket.send(packet);

            System.out.println("[UDP] Broadcast sent: " + message);

        } catch (Exception e) {
            System.out.println("[UDP] Broadcast error: " + e.getMessage());
        }
    }

    // Send to a specific IP address only (for targeted alerts)
    public void sendTo(String ipAddress, String message) {
        try {
            byte[] data = message.getBytes();
            InetAddress address = InetAddress.getByName(ipAddress);
            DatagramPacket packet = new DatagramPacket(
                data, data.length, address, UDP_PORT
            );
            socket.send(packet);
            System.out.println("[UDP] Sent to " + ipAddress + ": " + message);
        } catch (Exception e) {
            System.out.println("[UDP] Send error: " + e.getMessage());
        }
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}