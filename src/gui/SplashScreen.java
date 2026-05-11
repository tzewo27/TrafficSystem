package gui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        setSize(480, 280);
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);

        // Show for 3 seconds then close
        Timer timer = new Timer(3000, e -> dispose());
        timer.setRepeats(false);
        timer.start();
    }

    private void buildUI() {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Dark background
                g2.setColor(new Color(26, 26, 46));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Accent line
                g2.setColor(new Color(29, 158, 117));
                g2.fillRect(0, getHeight() - 4,
                    getWidth(), 4);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 30, 40));

        JLabel icon = new JLabel("🚦");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Traffic Incident System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel(
            "Addis Ababa Science and Technology University");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(170, 170, 170));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dept = new JLabel("Department of Software Engineering");
        dept.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dept.setForeground(new Color(170, 170, 170));
        dept.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setForeground(new Color(29, 158, 117));
        progress.setBackground(new Color(50, 50, 70));
        progress.setBorderPainted(false);
        progress.setPreferredSize(new Dimension(400, 4));
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));

        JLabel loading = new JLabel("Loading system...");
        loading.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loading.setForeground(new Color(120, 120, 140));
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createVerticalStrut(12));
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(sub);
        panel.add(dept);
        panel.add(Box.createVerticalStrut(24));
        panel.add(progress);
        panel.add(Box.createVerticalStrut(8));
        panel.add(loading);

        add(panel);
    }
}