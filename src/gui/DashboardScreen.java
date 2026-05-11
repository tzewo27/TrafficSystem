package gui;

import database.IncidentDAO;
import models.Incident;
import network.TrafficClient;
import network.UDPListener;
import utils.Logger;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DashboardScreen extends JFrame {

    private TrafficClient client;
    private String userName;
    private int userId;
    private JTable incidentTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel, criticalLabel,
                   resolvedLabel, openLabel;

    private static final Color BG      = new Color(248, 250, 252);
    private static final Color WHITE   = Color.WHITE;
    private static final Color BLUE    = new Color(37, 99, 235);
    private static final Color RED     = new Color(220, 38, 38);
    private static final Color GREEN   = new Color(5, 150, 105);
    private static final Color AMBER   = new Color(217, 119, 6);
    private static final Color TEXT    = new Color(15, 23, 42);
    private static final Color SUBTEXT = new Color(100, 116, 139);
    private static final Color BORDER  = new Color(226, 232, 240);
    private static final Color SIDEBAR = new Color(30, 41, 59);
    private static final Color SIDEBG  = new Color(15, 23, 42);

    public DashboardScreen(TrafficClient client,
                           String userName, int userId) {
        this.client   = client;
        this.userName = userName;
        this.userId   = userId;

        setTitle("Traffic System — Dashboard");
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 550));
        getContentPane().setBackground(BG);

        buildUI();
        loadIncidents();
        startAutoRefresh();
        startUDPListener();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(),    BorderLayout.CENTER);
    }

    // ── SIDEBAR ───────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(SIDEBAR);
        s.setPreferredSize(new Dimension(220, 0));

        // Logo
        JPanel logo = new JPanel();
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBackground(SIDEBG);
        logo.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        logo.setMaximumSize(new Dimension(220, 80));

        JLabel t = new JLabel("🚦 Traffic System");
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Addis Ababa City");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(100, 116, 139));

        logo.add(t);
        logo.add(Box.createVerticalStrut(3));
        logo.add(sub);
        s.add(logo);
        s.add(Box.createVerticalStrut(8));

        s.add(sideSection("MAIN"));
        s.add(sideItem("Dashboard",   true,  () -> {}));
        s.add(sideItem("Report",      false,
            () -> new ReportScreen(client, userId)));
        s.add(sideItem("Analytics",   false,
            () -> new AnalyticsScreen()));
        s.add(Box.createVerticalStrut(4));
        s.add(sideSection("EMERGENCY"));
        s.add(sideItem("First Aid",   false,
            () -> new FirstAidScreen()));
        s.add(Box.createVerticalStrut(4));
        s.add(sideSection("SYSTEM"));
        s.add(sideItem("Logs",        false,
            () -> showLogs()));

        s.add(Box.createVerticalGlue());

        // User at bottom
        JPanel userCard = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 14, 14));
        userCard.setBackground(SIDEBG);
        userCard.setMaximumSize(new Dimension(220, 62));

        JPanel av = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String initials = userName.length() > 0
                    ? userName.substring(0,1).toUpperCase() : "U";
                g2.drawString(initials,
                    (getWidth()-fm.stringWidth(initials))/2,
                    (getHeight()+fm.getAscent()
                        -fm.getDescent())/2);
            }
        };
        av.setPreferredSize(new Dimension(34, 34));
        av.setOpaque(false);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel nameL = new JLabel(userName);
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameL.setForeground(Color.WHITE);
        JLabel roleL = new JLabel("Officer");
        roleL.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleL.setForeground(SUBTEXT);
        info.add(nameL);
        info.add(roleL);

        userCard.add(av);
        userCard.add(info);
        s.add(userCard);
        return s;
    }

    private JLabel sideSection(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(71, 85, 105));
        l.setBorder(BorderFactory.createEmptyBorder(12,20,4,20));
        l.setMaximumSize(new Dimension(220, 30));
        return l;
    }

    private JPanel sideItem(String title, boolean active,
                             Runnable action) {
        JPanel p = new JPanel(new FlowLayout(
            FlowLayout.LEFT, 20, 10));
        p.setBackground(active
            ? new Color(37, 99, 235, 30) : SIDEBAR);
        p.setMaximumSize(new Dimension(220, 42));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (active)
            p.setBorder(BorderFactory.createMatteBorder(
                0, 3, 0, 0, BLUE));

        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI",
            active ? Font.BOLD : Font.PLAIN, 13));
        l.setForeground(active
            ? Color.WHITE : new Color(148, 163, 184));
        p.add(l);

        p.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
            public void mouseEntered(MouseEvent e) {
                if (!active)
                    p.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(MouseEvent e) {
                if (!active) p.setBackground(SIDEBAR);
            }
        });
        return p;
    }

    // ── MAIN ──────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(WHITE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,BORDER),
            BorderFactory.createEmptyBorder(14,24,14,24)));

        JPanel tLeft = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 0));
        tLeft.setOpaque(false);

        JLabel titleL = new JLabel("Live Dashboard");
        titleL.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleL.setForeground(TEXT);

        JLabel liveBadge = new JLabel("● Live");
        liveBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        liveBadge.setForeground(new Color(22, 101, 52));
        liveBadge.setOpaque(true);
        liveBadge.setBackground(new Color(220, 252, 231));
        liveBadge.setBorder(BorderFactory.createEmptyBorder(
            3,10,3,10));

        tLeft.add(titleL);
        tLeft.add(liveBadge);

        JPanel tRight = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tRight.setOpaque(false);

        JButton refreshBtn = outlineBtn("Refresh");
        refreshBtn.addActionListener(e -> loadIncidents());

        JButton reportBtn = filledBtn(
            "+ Report Incident", RED);
        reportBtn.addActionListener(
            e -> openReport());

        tRight.add(refreshBtn);
        tRight.add(reportBtn);

        topbar.add(tLeft,  BorderLayout.WEST);
        topbar.add(tRight, BorderLayout.EAST);

        // Content
        JPanel content = new JPanel(new BorderLayout(0,16));
        content.setBackground(BG);
        content.setBorder(
            BorderFactory.createEmptyBorder(20,24,20,24));

        content.add(buildStats(),   BorderLayout.NORTH);
        content.add(buildTable(),   BorderLayout.CENTER);

        main.add(topbar,  BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        return main;
    }

    // ── OPEN REPORT — refreshes after close ───────────────────────
    private void openReport() {
        ReportScreen rs = new ReportScreen(client, userId);
        rs.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                // Reload table when report window closes
                Timer t = new Timer(500, ev -> loadIncidents());
                t.setRepeats(false);
                t.start();
            }
        });
    }

    // ── STAT CARDS ────────────────────────────────────────────────
    private JPanel buildStats() {
        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));

        totalLabel    = addStat(p, "0", "Total Incidents",
            BLUE,  "+0 today");
        criticalLabel = addStat(p, "0", "Critical",
            RED,   "needs attention");
        resolvedLabel = addStat(p, "0", "Resolved",
            GREEN, "completed");
        openLabel     = addStat(p, "0", "Still Open",
            AMBER, "in progress");
        return p;
    }

    private JLabel addStat(JPanel parent, String val,
            String lbl, Color color, String sub) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,0,0,BORDER),
            BorderFactory.createEmptyBorder(16,18,16,18)));

        // Top color stripe
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createMatteBorder(
                    3,0,0,0,color)),
            BorderFactory.createEmptyBorder(14,18,14,18)));

        JLabel valL = new JLabel(val);
        valL.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valL.setForeground(color);

        JLabel lblL = new JLabel(lbl);
        lblL.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblL.setForeground(TEXT);

        JLabel subL = new JLabel(sub);
        subL.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subL.setForeground(SUBTEXT);

        card.add(valL);
        card.add(Box.createVerticalStrut(2));
        card.add(lblL);
        card.add(Box.createVerticalStrut(2));
        card.add(subL);

        parent.add(card);
        return valL;
    }

    // ── TABLE ─────────────────────────────────────────────────────
    private JPanel buildTable() {
        JPanel section = new JPanel(new BorderLayout(0,10));
        section.setOpaque(false);

        // Header row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel sTitle = new JLabel("Recent Incidents");
        sTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sTitle.setForeground(TEXT);

        JPanel btns = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);

        JButton analyticsBtn = filledBtn("Analytics", BLUE);
        analyticsBtn.addActionListener(
            e -> new AnalyticsScreen());

        JButton firstAidBtn = filledBtn("First Aid", AMBER);
        firstAidBtn.addActionListener(
            e -> new FirstAidScreen());

        JButton progressBtn = outlineBtn("In Progress");
        progressBtn.addActionListener(e -> markInProgress());

        JButton resolveBtn = filledBtn("Mark Resolved", GREEN);
        resolveBtn.addActionListener(e -> resolveSelected());

        btns.add(analyticsBtn);
        btns.add(firstAidBtn);
        btns.add(progressBtn);
        btns.add(resolveBtn);

        header.add(sTitle, BorderLayout.WEST);
        header.add(btns,   BorderLayout.EAST);

        // Table
        String[] cols = {"#","Type","Location","Severity","Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        incidentTable = new JTable(tableModel);
        incidentTable.setBackground(WHITE);
        incidentTable.setForeground(TEXT);
        incidentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        incidentTable.setRowHeight(42);
        incidentTable.setShowGrid(false);
        incidentTable.setIntercellSpacing(new Dimension(0,0));
        incidentTable.setSelectionBackground(
            new Color(37,99,235,20));
        incidentTable.setSelectionForeground(TEXT);
        incidentTable.setFillsViewportHeight(true);

        JTableHeader th = incidentTable.getTableHeader();
        th.setBackground(new Color(249,250,251));
        th.setForeground(SUBTEXT);
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setBorder(BorderFactory.createMatteBorder(
            0,0,1,0,BORDER));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 38));

        int[] widths = {50,180,210,110,110};
        for (int i = 0; i < widths.length; i++)
            incidentTable.getColumnModel()
                .getColumn(i).setPreferredWidth(widths[i]);

        incidentTable.setDefaultRenderer(Object.class,
            new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel,
                        boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(
                        t,v,sel,foc,row,col);
                    setBackground(sel
                        ? new Color(37,99,235,15) : WHITE);
                    setForeground(TEXT);
                    setFont(new Font("Segoe UI",Font.PLAIN,13));
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                            0,0,1,0,new Color(243,244,246)),
                        BorderFactory.createEmptyBorder(
                            0,16,0,16)));

                    if (col==3 && !sel) {
                        String sv = v!=null?v.toString():"";
                        switch(sv) {
                            case "Critical":
                                setBackground(
                                    new Color(254,242,242));
                                setForeground(RED);
                                setFont(new Font(
                                    "Segoe UI",Font.BOLD,12));
                                break;
                            case "Moderate":
                                setBackground(
                                    new Color(255,251,235));
                                setForeground(AMBER);
                                setFont(new Font(
                                    "Segoe UI",Font.BOLD,12));
                                break;
                            default:
                                setBackground(
                                    new Color(240,253,244));
                                setForeground(GREEN);
                                setFont(new Font(
                                    "Segoe UI",Font.BOLD,12));
                        }
                    }
                    if (col==4 && !sel) {
                        String st = v!=null?v.toString():"";
                        setFont(new Font(
                            "Segoe UI",Font.BOLD,12));
                        switch(st) {
                            case "Open":
                                setForeground(AMBER); break;
                            case "Resolved":
                                setForeground(GREEN); break;
                            case "In Progress":
                                setForeground(BLUE); break;
                            default:
                                setForeground(SUBTEXT);
                        }
                    }
                    if (col==0) {
                        setForeground(SUBTEXT);
                        setFont(new Font(
                            "Segoe UI",Font.PLAIN,12));
                    }
                    if (col==1)
                        setFont(new Font(
                            "Segoe UI",Font.BOLD,13));
                    return this;
                }
            });

        JScrollPane scroll = new JScrollPane(incidentTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        scroll.setBackground(WHITE);

        section.add(header, BorderLayout.NORTH);
        section.add(scroll, BorderLayout.CENTER);
        return section;
    }

    // ── LOAD FROM DATABASE DIRECTLY ───────────────────────────────
    // This is the KEY FIX — load directly from DB,
    // not through the socket, so there is NO interference
    public void loadIncidents() {
        SwingWorker<List<Incident>, Void> worker =
            new SwingWorker<>() {
            protected List<Incident> doInBackground() {
                return new IncidentDAO().getAllIncidents();
            }
            protected void done() {
                try {
                    List<Incident> list = get();
                    tableModel.setRowCount(0);
                    int critical=0, resolved=0, open=0;

                    for (Incident i : list) {
                        tableModel.addRow(new Object[]{
                            i.getId(),
                            i.getType(),
                            i.getLocation(),
                            i.getSeverity(),
                            i.getStatus()
                        });
                        if ("Critical".equals(i.getSeverity()))
                            critical++;
                        if ("Resolved".equals(i.getStatus()))
                            resolved++;
                        if ("Open".equals(i.getStatus()))
                            open++;
                    }

                    totalLabel.setText(
                        String.valueOf(list.size()));
                    criticalLabel.setText(
                        String.valueOf(critical));
                    resolvedLabel.setText(
                        String.valueOf(resolved));
                    openLabel.setText(
                        String.valueOf(open));

                } catch (Exception e) {
                    System.out.println(
                        "Load error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ── ACTIONS ───────────────────────────────────────────────────
    private void resolveSelected() {
        int row = incidentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an incident from the table!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(
            tableModel.getValueAt(row,0).toString());
        new IncidentDAO().updateStatus(id, "Resolved");
        Logger.logIncidentResolved(id, userName);
        loadIncidents();
        JOptionPane.showMessageDialog(this,
            "Incident #"+id+" marked as Resolved! ✓",
            "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    private void markInProgress() {
        int row = incidentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an incident from the table!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(
            tableModel.getValueAt(row,0).toString());
        new IncidentDAO().updateStatus(id, "In Progress");
        loadIncidents();
    }

    private void showLogs() {
        String logs = utils.Logger.readLogs(50);
        JTextArea ta = new JTextArea(logs);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setBackground(new Color(15,23,42));
        ta.setForeground(new Color(52,211,153));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(700,400));
        ta.setCaretPosition(ta.getDocument().getLength());
        JOptionPane.showMessageDialog(this, sp,
            "System Logs", JOptionPane.PLAIN_MESSAGE);
    }

    private void startAutoRefresh() {
        new Timer(4000, e -> loadIncidents()).start();
    }

    private void startUDPListener() {
        UDPListener listener = new UDPListener(msg ->
            SwingUtilities.invokeLater(() -> {
                if (msg.startsWith("EMERGENCY:"))
                    JOptionPane.showMessageDialog(this,
                        msg.substring(10),
                        "EMERGENCY ALERT",
                        JOptionPane.ERROR_MESSAGE);
                else if (msg.startsWith("ALERT:"))
                    JOptionPane.showMessageDialog(this,
                        msg.substring(6),
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);
                loadIncidents();
            }));
        Thread t = new Thread(listener);
        t.setDaemon(true);
        t.start();
    }

    // ── BUTTON HELPERS ────────────────────────────────────────────
    private JButton filledBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(
            8,14,8,14));
        return b;
    }

    private JButton outlineBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(WHITE);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(7,14,7,14)));
        return b;
    }
}