package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

// UDP Listener — runs silently in background on every client
// When a broadcast arrives it shows a popup alert immediately
// This runs on its own daemon thread so it never blocks the GUI

public class UDPListener implements Runnable {

    private static final int UDP_PORT = 6000;
    private static final int BUFFER_SIZE = 1024;
    private boolean running = true;

    // This interface lets the GUI react when a message arrives
    // We pass in what to do — show popup, play sound, update table etc.
    public interface AlertHandler {
        void onAlertReceived(String message);
    }

    private AlertHandler handler;

    public UDPListener(AlertHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            socket.setBroadcast(true);
            byte[] buffer = new byte[BUFFER_SIZE];

            System.out.println("[UDP] Listening for broadcasts on port "
                + UDP_PORT);

            // Keep listening forever until stopped
            while (running) {
                DatagramPacket packet =
                    new DatagramPacket(buffer, buffer.length);

                // This line WAITS until a packet arrives
                socket.receive(packet);

                String message = new String(
                    packet.getData(), 0, packet.getLength()
                );

                System.out.println("[UDP] Received broadcast: " + message);

                // Tell the GUI to handle this message
                if (handler != null) {
                    handler.onAlertReceived(message);
                }
            }
        } catch (Exception e) {
            if (running) {
                System.out.println("[UDP] Listener error: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
    }
}