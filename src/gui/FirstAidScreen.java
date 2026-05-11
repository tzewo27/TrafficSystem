package gui;

import javax.swing.*;
import java.awt.*;

public class FirstAidScreen extends JFrame {

    public FirstAidScreen() {
        setTitle("Emergency First Aid");
        setSize(420, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Emergency call button
        JButton callBtn = new JButton("CALL 907 — Ambulance");
        callBtn.setBackground(new Color(163, 45, 45));
        callBtn.setForeground(Color.WHITE);
        callBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        callBtn.setFocusPainted(false);
        callBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        // Guide selector
        String[] guides = {"CPR Instructions",
                           "Bleeding Control",
                           "Shock Treatment",
                           "Burns First Aid"};
        JComboBox<String> guideBox = new JComboBox<>(guides);
        guideBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        guideBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Steps panel
        JPanel stepsPanel = new JPanel();
        stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
        stepsPanel.setBackground(new Color(252, 235, 235));
        stepsPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        String[][] allSteps = {
            {"Call 907 immediately",
             "Lay person flat on back",
             "Place hands on center of chest",
             "Push down hard 30 times",
             "Give 2 rescue breaths",
             "Repeat until help arrives"},
            {"Apply firm pressure to wound",
             "Use clean cloth or bandage",
             "Do not remove the cloth",
             "Elevate injured area if possible",
             "Call 907 if bleeding won't stop"},
            {"Lay person down, elevate legs",
             "Loosen tight clothing",
             "Keep person warm with blanket",
             "Do not give food or water",
             "Monitor breathing, call 907"},
            {"Cool burn with cool water 10 min",
             "Do not use ice or butter",
             "Cover with clean bandage",
             "Do not break blisters",
             "Seek medical help immediately"}
        };

        showSteps(stepsPanel, allSteps[0]);

        guideBox.addActionListener(e -> {
            showSteps(stepsPanel, allSteps[guideBox.getSelectedIndex()]);
            stepsPanel.revalidate();
            stepsPanel.repaint();
        });

        JScrollPane scroll = new JScrollPane(stepsPanel);
        scroll.setBorder(null);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(callBtn);
        panel.add(Box.createVerticalStrut(16));
        panel.add(makeLabel("Select guide:"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(guideBox);
        panel.add(Box.createVerticalStrut(12));
        panel.add(scroll);

        add(panel);
    }

    private void showSteps(JPanel panel, String[] steps) {
        panel.removeAll();
        for (int i = 0; i < steps.length; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
            row.setOpaque(false);

            JLabel num = new JLabel(String.valueOf(i + 1));
            num.setFont(new Font("Segoe UI", Font.BOLD, 13));
            num.setForeground(new Color(163, 45, 45));
            num.setPreferredSize(new Dimension(20, 20));

            JLabel step = new JLabel(steps[i]);
            step.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            row.add(num);
            row.add(step);
            panel.add(row);
        }
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}