package gui;

import network.TrafficClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReportScreen extends JFrame {

    private TrafficClient client;
    private int userId;
    private JTextField locationField, descField;
    private JComboBox<String> typeBox, severityBox;
    private JLabel statusLabel;

    private static final Color BG      = new Color(26, 26, 46);
    private static final Color CARD    = new Color(36, 36, 60);
    private static final Color ACCENT  = new Color(29, 158, 117);
    private static final Color BORDER  = new Color(55, 55, 80);
    private static final Color TEXT    = new Color(220, 220, 230);
    private static final Color SUBTEXT = new Color(140, 140, 160);
    private static final Color DANGER  = new Color(163, 45, 45);

    public ReportScreen(TrafficClient client, int userId) {
        this.client = client;
        this.userId = userId;

        setTitle("Report an Incident");
        setSize(480, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // ── HEADER ────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(CARD);
        header.setBorder(
            BorderFactory.createEmptyBorder(24, 28, 20, 28));

        JLabel icon = new JLabel("🚨 Report an Incident");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        icon.setForeground(TEXT);
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel(
            "All reports go live on the dashboard immediately");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(SUBTEXT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(icon);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        // ── EMERGENCY BANNER ──────────────────────────────────────
        JPanel emergencyBanner = new JPanel(new BorderLayout(12, 0));
        emergencyBanner.setBackground(new Color(80, 20, 20));
        emergencyBanner.setBorder(
            BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel emergencyLabel = new JLabel(
            "⚠  Someone injured? Call emergency first!");
        emergencyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emergencyLabel.setForeground(new Color(255, 180, 180));

        JButton callBtn = new JButton("📞  Call 907");
        callBtn.setBackground(DANGER);
        callBtn.setForeground(Color.WHITE);
        callBtn.setFocusPainted(false);
        callBtn.setBorderPainted(false);
        callBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        callBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        callBtn.setBorder(
            BorderFactory.createEmptyBorder(6, 14, 6, 14));
        callBtn.addActionListener(e -> showCallDialog());

        emergencyBanner.add(emergencyLabel, BorderLayout.CENTER);
        emergencyBanner.add(callBtn,        BorderLayout.EAST);

        // ── FORM ──────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG);
        form.setBorder(
            BorderFactory.createEmptyBorder(20, 28, 20, 28));

        String[] types = {
            "Car Collision",
            "Traffic Jam",
            "Flooded Road",
            "Road Construction",
            "Broken Traffic Light",
            "Dangerous Condition",
            "Vehicle Fire"
        };
        typeBox = makeCombo(types);

        String[] severities = {"Critical", "Moderate", "Low"};
        severityBox = makeCombo(severities);

        locationField = makeField("e.g. Bole Road, Addis Ababa");
        descField     = makeField("Describe what happened...");

        JButton submitBtn = makeBtn("Submit Report", ACCENT);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> submitReport());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(makeLabel("Incident type"));
        form.add(Box.createVerticalStrut(6));
        form.add(typeBox);
        form.add(Box.createVerticalStrut(16));
        form.add(makeLabel("Location"));
        form.add(Box.createVerticalStrut(6));
        form.add(locationField);
        form.add(Box.createVerticalStrut(16));
        form.add(makeLabel("Severity"));
        form.add(Box.createVerticalStrut(6));
        form.add(severityBox);
        form.add(Box.createVerticalStrut(16));
        form.add(makeLabel("Description"));
        form.add(Box.createVerticalStrut(6));
        form.add(descField);
        form.add(Box.createVerticalStrut(24));
        form.add(submitBtn);
        form.add(Box.createVerticalStrut(10));
        form.add(statusLabel);

        // ── LAYOUT ────────────────────────────────────────────────
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BG);
        topSection.add(header,          BorderLayout.NORTH);
        topSection.add(emergencyBanner, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        add(topSection, BorderLayout.NORTH);
        add(scroll,     BorderLayout.CENTER);
    }

    // ── AMBULANCE CALL DIALOG ─────────────────────────────────────
    private void showCallDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(
            BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Emergency Contacts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        String[][] contacts = {
            {"🚑  Ambulance",          "907"},
            {"🚒  Fire Brigade",       "939"},
            {"👮  Police",             "991"},
            {"🏥  St. Paul Hospital",  "011-275-3312"},
            {"🏥  Black Lion Hospital","011-155-1211"},
            {"🏥  Tikur Anbessa",      "011-551-0305"}
        };

        for (String[] c : contacts) {
            JPanel row = new JPanel(new BorderLayout(16, 0));
            row.setBackground(CARD);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            row.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 50));

            JLabel name = new JLabel(c[0]);
            name.setFont(new Font("Segoe UI", Font.BOLD, 13));
            name.setForeground(TEXT);

            JLabel num = new JLabel(c[1]);
            num.setFont(new Font("Consolas", Font.BOLD, 16));
            num.setForeground(ACCENT);

            row.add(name, BorderLayout.WEST);
            row.add(num,  BorderLayout.EAST);
            panel.add(row);
            panel.add(Box.createVerticalStrut(8));
        }

        JLabel note = new JLabel(
            "Stay calm. Give your exact location clearly.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(SUBTEXT);
        note.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(4));
        panel.add(note);

        JOptionPane.showMessageDialog(
            this, panel,
            "Emergency Contacts",
            JOptionPane.PLAIN_MESSAGE);
    }

    // ── SUBMIT REPORT ─────────────────────────────────────────────
    private void submitReport() {
        String type = ((String) typeBox.getSelectedItem())
                        .replace("|", "-");
        String location = locationField.getText().trim()
                            .replace("|", "-");
        String severity = ((String) severityBox.getSelectedItem())
                            .replace("|", "-");
        String desc = descField.getText().trim()
                        .replace("|", "-");

        if (location.isEmpty()
                || location.equals("e.g. Bole Road, Addis Ababa")) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Please enter a location!");
            return;
        }
        if (desc.isEmpty()
                || desc.equals("Describe what happened...")) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Please enter a description!");
            return;
        }

        statusLabel.setForeground(SUBTEXT);
        statusLabel.setText("Submitting...");

        String msg = "REPORT_INCIDENT:"
            + type     + "|"
            + location + "|"
            + severity + "|"
            + desc     + "|"
            + userId;

        System.out.println("[Sending report]: " + msg);

        // SwingWorker keeps GUI responsive while waiting for server
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() {
                // Uses DEDICATED report socket
                // so auto-refresh never steals this response
                return client.sendReport(msg);
            }
            protected void done() {
                try {
                    String response = get();
                    System.out.println(
                        "[Report response]: " + response);
                    if (response != null
                            && response.startsWith("SUCCESS:")) {
                        statusLabel.setForeground(ACCENT);
                        statusLabel.setText(
                            "✓ " + response.substring(8));
                        // Clear fields
                        locationField.setText(
                            "e.g. Bole Road, Addis Ababa");
                        locationField.setForeground(SUBTEXT);
                        descField.setText(
                            "Describe what happened...");
                        descField.setForeground(SUBTEXT);
                    } else {
                        statusLabel.setForeground(DANGER);
                        statusLabel.setText("Error: "
                            + (response != null
                                ? response : "No response"));
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(DANGER);
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ── UI HELPERS ────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(CARD);
        f.setForeground(SUBTEXT);
        f.setCaretColor(TEXT);
        f.setText(placeholder);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT);
                }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(SUBTEXT);
                }
            }
        });
        return f;
    }

    private JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(CARD);
        box.setForeground(TEXT);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        return box;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(
            BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return btn;
    }
}