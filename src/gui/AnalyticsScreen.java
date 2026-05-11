package gui;

import database.IncidentDAO;
import models.Incident;
import utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// Analytics screen — shows charts and statistics
// Built with pure Java2D — no extra libraries needed!

public class AnalyticsScreen extends JFrame {

    private IncidentDAO dao;
    private List<Incident> allIncidents;

    public AnalyticsScreen() {
        dao = new IncidentDAO();
        allIncidents = dao.getAllIncidents();

        setTitle("Analytics & Statistics");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        setVisible(true);
        Logger.log("ANALYTICS", "Analytics screen opened");
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

        JLabel titleLbl = new JLabel("Analytics & Statistics");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> {
            allIncidents = dao.getAllIncidents();
            repaint();
            buildSummaryCards();
        });

        topBar.add(titleLbl,   BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);

        // ── SUMMARY CARDS ─────────────────────────────────────────
        JPanel summaryPanel = buildSummaryPanel();

        // ── CHARTS PANEL ──────────────────────────────────────────
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        chartsPanel.setOpaque(false);

        // Bar chart — incidents by type
        BarChartPanel barChart = new BarChartPanel();
        barChart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        barChart.setBackground(Color.WHITE);

        // Pie chart — incidents by severity
        PieChartPanel pieChart = new PieChartPanel();
        pieChart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        pieChart.setBackground(Color.WHITE);

        chartsPanel.add(barChart);
        chartsPanel.add(pieChart);

        // ── MOST DANGEROUS ROADS ──────────────────────────────────
        JPanel dangerPanel = buildDangerPanel();

        // ── LAYOUT ────────────────────────────────────────────────
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(summaryPanel, BorderLayout.NORTH);
        center.add(chartsPanel,  BorderLayout.CENTER);
        center.add(dangerPanel,  BorderLayout.SOUTH);

        add(topBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    // ── SUMMARY CARDS ─────────────────────────────────────────────
    private JPanel summaryPanel;
    private JPanel buildSummaryPanel() {
        summaryPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buildSummaryCards();
        return summaryPanel;
    }

    private void buildSummaryCards() {
        summaryPanel.removeAll();

        long total    = allIncidents.size();
        long critical = allIncidents.stream()
            .filter(i -> "Critical".equals(i.getSeverity())).count();
        long resolved = allIncidents.stream()
            .filter(i -> "Resolved".equals(i.getStatus())).count();
        long open     = allIncidents.stream()
            .filter(i -> "Open".equals(i.getStatus())).count();

        summaryPanel.add(makeCard(
            String.valueOf(total),    "Total incidents",
            new Color(24, 95, 165)));
        summaryPanel.add(makeCard(
            String.valueOf(critical), "Critical",
            new Color(163, 45, 45)));
        summaryPanel.add(makeCard(
            String.valueOf(resolved), "Resolved",
            new Color(8, 80, 65)));
        summaryPanel.add(makeCard(
            String.valueOf(open),     "Still open",
            new Color(133, 79, 11)));

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private JPanel makeCard(String number, String label, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel numLbl = new JLabel(number, SwingConstants.CENTER);
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        numLbl.setForeground(color);
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

    // ── MOST DANGEROUS ROADS ──────────────────────────────────────
    private JPanel buildDangerPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel title = new JLabel("Most reported locations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Count incidents per location
        Map<String, Long> locationCount = allIncidents.stream()
            .collect(Collectors.groupingBy(
                Incident::getLocation, Collectors.counting()));

        // Sort by count descending, take top 5
        List<Map.Entry<String, Long>> top5 = locationCount.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toList());

        JPanel rows = new JPanel(new GridLayout(
            top5.size() == 0 ? 1 : top5.size(), 1, 0, 6));
        rows.setOpaque(false);

        if (top5.isEmpty()) {
            JLabel empty = new JLabel("No data yet — report some incidents!");
            empty.setForeground(Color.GRAY);
            rows.add(empty);
        } else {
            long max = top5.get(0).getValue();
            for (int i = 0; i < top5.size(); i++) {
                Map.Entry<String, Long> entry = top5.get(i);
                rows.add(makeDangerRow(
                    i + 1, entry.getKey(),
                    entry.getValue(), max));
            }
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(rows,  BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeDangerRow(int rank, String location,
                                  long count, long max) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel rankLbl = new JLabel("#" + rank);
        rankLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rankLbl.setForeground(rank == 1
            ? new Color(163, 45, 45) : Color.GRAY);
        rankLbl.setPreferredSize(new Dimension(30, 20));

        JLabel locLbl = new JLabel(location);
        locLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        locLbl.setPreferredSize(new Dimension(200, 20));

        // Progress bar showing relative danger
        JProgressBar bar = new JProgressBar(0, (int) max);
        bar.setValue((int) count);
        bar.setForeground(rank == 1
            ? new Color(163, 45, 45) : new Color(24, 95, 165));
        bar.setBackground(new Color(240, 240, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(200, 16));

        JLabel countLbl = new JLabel(count + " reports");
        countLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLbl.setForeground(Color.GRAY);
        countLbl.setPreferredSize(new Dimension(80, 20));

        row.add(rankLbl,  BorderLayout.WEST);
        row.add(locLbl,   BorderLayout.CENTER);
        row.add(bar,      BorderLayout.EAST);
        row.add(countLbl, BorderLayout.EAST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(bar);
        right.add(countLbl);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    // ══════════════════════════════════════════════════════════════
    // BAR CHART — incidents by type, drawn with Java2D
    // ══════════════════════════════════════════════════════════════
    class BarChartPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            // Title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(50, 50, 50));
            g2.drawString("Incidents by type", 10, 20);

            // Count by type
            Map<String, Long> typeCounts = allIncidents.stream()
                .collect(Collectors.groupingBy(
                    Incident::getType, Collectors.counting()));

            if (typeCounts.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(Color.GRAY);
                g2.drawString("No data yet", getWidth()/2 - 30,
                    getHeight()/2);
                return;
            }

            List<Map.Entry<String, Long>> entries =
                new ArrayList<>(typeCounts.entrySet());
            entries.sort(Map.Entry.<String, Long>
                comparingByValue().reversed());

            int chartX    = 20;
            int chartY    = 35;
            int chartW    = getWidth()  - 40;
            int chartH    = getHeight() - 70;
            long maxVal   = entries.get(0).getValue();

            Color[] colors = {
                new Color(24,  95,  165),
                new Color(163, 45,  45),
                new Color(8,   80,  65),
                new Color(133, 79,  11),
                new Color(83,  74,  183),
                new Color(216, 90,  48)
            };

            int barCount  = entries.size();
            int barWidth  = Math.min(60, (chartW / barCount) - 10);
            int gap       = (chartW - barWidth * barCount)
                            / (barCount + 1);

            // Draw baseline
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(chartX, chartY + chartH,
                        chartX + chartW, chartY + chartH);

            for (int i = 0; i < entries.size(); i++) {
                Map.Entry<String, Long> entry = entries.get(i);
                long val   = entry.getValue();
                int  barH  = (int) ((double) val / maxVal * chartH);
                int  x     = chartX + gap + i * (barWidth + gap);
                int  y     = chartY + chartH - barH;

                // Draw bar
                Color c = colors[i % colors.length];
                g2.setColor(c);
                g2.fillRoundRect(x, y, barWidth, barH, 6, 6);

                // Draw value on top
                g2.setColor(new Color(50, 50, 50));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String valStr = String.valueOf(val);
                FontMetrics fm = g2.getFontMetrics();
                int valX = x + (barWidth - fm.stringWidth(valStr)) / 2;
                g2.drawString(valStr, valX, y - 4);

                // Draw label below
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String label = entry.getKey().length() > 10
                    ? entry.getKey().substring(0, 10) + "…"
                    : entry.getKey();
                int labelX = x + (barWidth -
                    g2.getFontMetrics().stringWidth(label)) / 2;
                g2.drawString(label, labelX,
                    chartY + chartH + 14);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    // PIE CHART — incidents by severity, drawn with Java2D
    // ══════════════════════════════════════════════════════════════
    class PieChartPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            // Title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(50, 50, 50));
            g2.drawString("Incidents by severity", 10, 20);

            // Count by severity
            long critical = allIncidents.stream()
                .filter(i -> "Critical".equals(i.getSeverity())).count();
            long moderate = allIncidents.stream()
                .filter(i -> "Moderate".equals(i.getSeverity())).count();
            long low      = allIncidents.stream()
                .filter(i -> "Low".equals(i.getSeverity())).count();
            long total    = critical + moderate + low;

            if (total == 0) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(Color.GRAY);
                g2.drawString("No data yet", getWidth()/2 - 30,
                    getHeight()/2);
                return;
            }

            // Pie dimensions
            int diameter = Math.min(getWidth(), getHeight()) - 100;
            int x = (getWidth() - diameter) / 2 - 20;
            int y = 35;

            String[] labels = {"Critical", "Moderate", "Low"};
            long[]   values = {critical, moderate, low};
            Color[]  colors = {
                new Color(163, 45,  45),
                new Color(133, 79,  11),
                new Color(8,   80,  65)
            };

            double startAngle = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i] == 0) continue;
                double angle = 360.0 * values[i] / total;

                g2.setColor(colors[i]);
                g2.fillArc(x, y, diameter, diameter,
                    (int) startAngle, (int) angle);

                // Draw slice border
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawArc(x, y, diameter, diameter,
                    (int) startAngle, (int) angle);

                startAngle += angle;
            }

            // Legend
            int legendY = y + diameter + 16;
            int legendX = 20;
            for (int i = 0; i < labels.length; i++) {
                if (values[i] == 0) continue;
                g2.setColor(colors[i]);
                g2.fillRoundRect(legendX, legendY, 12, 12, 4, 4);
                g2.setColor(new Color(50, 50, 50));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                int pct = (int) (100.0 * values[i] / total);
                g2.drawString(labels[i] + " (" + pct + "%)",
                    legendX + 16, legendY + 11);
                legendX += 110;
            }
        }
    }
}