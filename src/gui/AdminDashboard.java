package gui;

import database.IncidentDAO;
import database.UserDAO;
import models.Incident;
import models.User;
import network.TrafficClient;
import network.UDPListener;
import utils.Logger;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private TrafficClient client;
    private String adminName;
    private JTable incidentTable, userTable;
    private DefaultTableModel incidentModel, userModel;
    private JLabel totalLabel, criticalLabel, resolvedLabel;

    public AdminDashboard(TrafficClient client, String adminName) {
        this.client    = client;
        this.adminName = adminName;

        setTitle("Admin Dashboard — " + adminName);
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        loadData();
        startUDPListener();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(
            BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // ── TOP BAR ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel titleLbl = new JLabel("Admin Dashboard");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel userLbl = new JLabel("Admin: " + adminName);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLbl.setForeground(Color.GRAY);

        JPanel btnPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshBtn.addActionListener(e -> loadData());

        JButton logsBtn = new JButton("View Logs");
        logsBtn.setFocusPainted(false);
        logsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logsBtn.addActionListener(e -> showLogs());

        JButton analyticsBtn = new JButton("Analytics");
        analyticsBtn.setFocusPainted(false);
        analyticsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        analyticsBtn.addActionListener(e -> new AnalyticsScreen());

        JButton firstAidBtn = new JButton("First Aid");
        firstAidBtn.setFocusPainted(false);
        firstAidBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        firstAidBtn.addActionListener(e -> new FirstAidScreen());

        btnPanel.add(firstAidBtn);
        btnPanel.add(analyticsBtn);
        btnPanel.add(logsBtn);
        btnPanel.add(refreshBtn);

        topBar.add(titleLbl, BorderLayout.WEST);
        topBar.add(userLbl,  BorderLayout.CENTER);
        topBar.add(btnPanel, BorderLayout.EAST);

        // ── STATS ─────────────────────────────────────────────────
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel totalCard    = makeStatCard("0", "Total incidents");
        JPanel criticalCard = makeStatCard("0", "Critical");
        JPanel resolvedCard = makeStatCard("0", "Resolved");

        totalLabel    = (JLabel) totalCard.getComponent(0);
        criticalLabel = (JLabel) criticalCard.getComponent(0);
        resolvedLabel = (JLabel) resolvedCard.getComponent(0);

        criticalLabel.setForeground(new Color(163, 45, 45));
        resolvedLabel.setForeground(new Color(8, 80, 65));

        statsPanel.add(totalCard);
        statsPanel.add(criticalCard);
        statsPanel.add(resolvedCard);

        // ── TABS ──────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Incidents tab
        String[] incCols = {"ID","Type","Location",
                            "Severity","Status","Reported by"};
        incidentModel = new DefaultTableModel(incCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        incidentTable = makeTable(incidentModel);

        // Color rows by severity
        incidentTable.setDefaultRenderer(Object.class,
            new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel,
                        boolean foc, int row, int col) {
                    Component c = super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    if (!sel) {
                        String sev = (String) t.getValueAt(row, 3);
                        if ("Critical".equals(sev))
                            c.setBackground(new Color(252, 235, 235));
                        else if ("Moderate".equals(sev))
                            c.setBackground(new Color(250, 238, 218));
                        else
                            c.setBackground(Color.WHITE);
                    }
                    return c;
                }
            });

        // Incident buttons
        JButton resolveBtn = new JButton("Mark as Resolved");
        resolveBtn.setBackground(new Color(8, 80, 65));
        resolveBtn.setForeground(Color.WHITE);
        resolveBtn.setFocusPainted(false);
        resolveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        resolveBtn.addActionListener(e -> resolveSelected());

        JButton inProgressBtn = new JButton("Mark as In Progress");
        inProgressBtn.setFocusPainted(false);
        inProgressBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inProgressBtn.addActionListener(e -> markInProgress());

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(new Color(163, 45, 45));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deleteBtn.addActionListener(e -> deleteSelected());

        JPanel incBtns = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 8));
        incBtns.setOpaque(false);
        incBtns.add(resolveBtn);
        incBtns.add(inProgressBtn);
        incBtns.add(deleteBtn);

        JPanel incPanel = new JPanel(new BorderLayout(0, 0));
        incPanel.setOpaque(false);
        incPanel.add(new JScrollPane(incidentTable), BorderLayout.CENTER);
        incPanel.add(incBtns, BorderLayout.SOUTH);
        tabs.addTab("All Incidents", incPanel);

        // Users tab
        String[] userCols = {"ID","Name","Email","Role"};
        userModel = new DefaultTableModel(userCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = makeTable(userModel);
        tabs.addTab("All Users", new JScrollPane(userTable));

        // ── LAYOUT ────────────────────────────────────────────────
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setOpaque(false);
        center.add(statsPanel, BorderLayout.NORTH);
        center.add(tabs,       BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    // ── TABLE BUILDER ─────────────────────────────────────────────
    private JTable makeTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(200, 220, 240));
        return table;
    }

    // ── STAT CARD BUILDER ─────────────────────────────────────────
    private JPanel makeStatCard(String number, String label) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel numLbl = new JLabel(number, SwingConstants.CENTER);
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        numLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLbl = new JLabel(label, SwingConstants.CENTER);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLbl.setForeground(Color.GRAY);
        lblLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(numLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(lblLbl);
        return card;
    }

    // ── LOAD ALL DATA ─────────────────────────────────────────────
    private void loadData() {
        incidentModel.setRowCount(0);
        IncidentDAO dao = new IncidentDAO();
        List<Incident> incidents = dao.getAllIncidents();

        int critical = 0, resolved = 0;
        for (Incident i : incidents) {
            incidentModel.addRow(new Object[]{
                i.getId(),
                i.getType(),
                i.getLocation(),
                i.getSeverity(),
                i.getStatus(),
                i.getReportedById()
            });
            if ("Critical".equals(i.getSeverity())) critical++;
            if ("Resolved".equals(i.getStatus()))   resolved++;
        }

        totalLabel.setText(String.valueOf(incidents.size()));
        criticalLabel.setText(String.valueOf(critical));
        resolvedLabel.setText(String.valueOf(resolved));

        userModel.setRowCount(0);
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            userModel.addRow(new Object[]{
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole()
            });
        }
    }

    // ── RESOLVE SELECTED ──────────────────────────────────────────
    private void resolveSelected() {
        int row = incidentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an incident first.",
                "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) incidentModel.getValueAt(row, 0);
        IncidentDAO dao = new IncidentDAO();
        boolean done = dao.updateStatus(id, "Resolved");
        if (done) {
            Logger.logIncidentResolved(id, adminName);
            JOptionPane.showMessageDialog(this,
                "Incident #" + id + " marked as Resolved!");
            loadData();
        }
    }

    // ── MARK IN PROGRESS ──────────────────────────────────────────
    private void markInProgress() {
        int row = incidentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an incident first.",
                "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) incidentModel.getValueAt(row, 0);
        IncidentDAO dao = new IncidentDAO();
        boolean done = dao.updateStatus(id, "In Progress");
        if (done) {
            Logger.log("STATUS", "Incident #" + id
                + " → In Progress by " + adminName);
            JOptionPane.showMessageDialog(this,
                "Incident #" + id + " marked as In Progress!");
            loadData();
        }
    }

    // ── DELETE SELECTED ───────────────────────────────────────────
    private void deleteSelected() {
        int row = incidentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an incident first.",
                "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) incidentModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete incident #" + id + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            IncidentDAO dao = new IncidentDAO();
            boolean done = dao.updateStatus(id, "Deleted");
            if (done) {
                Logger.log("DELETE",
                    "Incident #" + id + " deleted by " + adminName);
                JOptionPane.showMessageDialog(this,
                    "Incident #" + id + " deleted.");
                loadData();
            }
        }
    }

    // ── LOG VIEWER ────────────────────────────────────────────────
    private void showLogs() {
        String logs = Logger.readLogs(50);

        JTextArea textArea = new JTextArea(logs);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(new Color(0, 255, 100));
        textArea.setCaretColor(Color.WHITE);
        textArea.setCaretPosition(
            textArea.getDocument().getLength());

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(720, 420));

        JOptionPane.showMessageDialog(
            this, scroll,
            "System Logs — Last 50 entries",
            JOptionPane.PLAIN_MESSAGE);
    }

    // ── UDP LISTENER ──────────────────────────────────────────────
    private void startUDPListener() {
        UDPListener listener = new UDPListener(message -> {
            SwingUtilities.invokeLater(() -> {
                if (message.startsWith("EMERGENCY:")) {
                    JOptionPane.showMessageDialog(
                        this,
                        message.substring(10),
                        "EMERGENCY ALERT",
                        JOptionPane.ERROR_MESSAGE);
                } else if (message.startsWith("ALERT:")) {
                    JOptionPane.showMessageDialog(
                        this,
                        message.substring(6),
                        "System Alert",
                        JOptionPane.WARNING_MESSAGE);
                }
                loadData();
            });
        });

        Thread udpThread = new Thread(listener);
        udpThread.setDaemon(true);
        udpThread.start();
        System.out.println("UDP listener started on admin dashboard");
    }
}