package gui;

import database.UserDAO;
import models.User;
import network.TrafficClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterScreen extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JLabel statusLabel;
    private TrafficClient client;

    private static final Color BG      = new Color(26, 26, 46);
    private static final Color CARD    = new Color(36, 36, 60);
    private static final Color ACCENT  = new Color(29, 158, 117);
    private static final Color BORDER  = new Color(55, 55, 80);
    private static final Color TEXT    = new Color(220, 220, 230);
    private static final Color SUBTEXT = new Color(140, 140, 160);
    private static final Color DANGER  = new Color(163, 45, 45);

    public RegisterScreen(TrafficClient client) {
        this.client = client;

        setTitle("Create Account");
        setSize(420, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(CARD);
        header.setBorder(
            BorderFactory.createEmptyBorder(28, 40, 22, 40));

        JLabel icon = new JLabel("👤", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(
            "Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel(
            "Join the Traffic System",
            SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(SUBTEXT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(icon);
        header.add(Box.createVerticalStrut(8));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        // FORM
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG);
        form.setBorder(
            BorderFactory.createEmptyBorder(24, 36, 24, 36));

        nameField     = makeField("Full name");
        emailField    = makeField("Email address");
        passwordField = makePassField("Password");

        String[] roles = {"driver", "officer", "admin"};
        roleBox = new JComboBox<>(roles);
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleBox.setBackground(CARD);
        roleBox.setForeground(TEXT);
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        roleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton registerBtn = makeBtn("Create Account", ACCENT);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerBtn.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 46));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> doRegister());

        JButton backBtn = new JButton(
            "Already have an account? Sign in");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(ACCENT);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> dispose());

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(makeLabel("Full name"));
        form.add(Box.createVerticalStrut(6));
        form.add(nameField);
        form.add(Box.createVerticalStrut(12));
        form.add(makeLabel("Email"));
        form.add(Box.createVerticalStrut(6));
        form.add(emailField);
        form.add(Box.createVerticalStrut(12));
        form.add(makeLabel("Password"));
        form.add(Box.createVerticalStrut(6));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(12));
        form.add(makeLabel("Role"));
        form.add(Box.createVerticalStrut(6));
        form.add(roleBox);
        form.add(Box.createVerticalStrut(20));
        form.add(registerBtn);
        form.add(Box.createVerticalStrut(10));
        form.add(backBtn);
        form.add(Box.createVerticalStrut(8));
        form.add(statusLabel);

        add(header, BorderLayout.NORTH);
        add(form,   BorderLayout.CENTER);
    }

    private void doRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role     = (String) roleBox.getSelectedItem();

        if (name.isEmpty() || name.equals("Full name")) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Please enter your full name");
            return;
        }
        if (email.isEmpty() || email.equals("Email address")) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Please enter your email");
            return;
        }
        if (password.isEmpty() || password.equals("Password")) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Please enter a password");
            return;
        }

        statusLabel.setForeground(SUBTEXT);
        statusLabel.setText("Creating account...");

        UserDAO dao   = new UserDAO();
        User user     = new User(name, email, password, role);
        boolean saved = dao.saveUser(user);

        if (saved) {
            statusLabel.setForeground(ACCENT);
            statusLabel.setText(
                "Account created! You can now log in.");
            nameField.setText("Full name");
            nameField.setForeground(SUBTEXT);
            emailField.setText("Email address");
            emailField.setForeground(SUBTEXT);
            passwordField.setText("");
        } else {
            statusLabel.setForeground(DANGER);
            statusLabel.setText(
                "Error — email may already be registered.");
        }
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

    private JPasswordField makePassField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(CARD);
        f.setForeground(SUBTEXT);
        f.setCaretColor(TEXT);
        f.setEchoChar((char) 0);
        f.setText(placeholder);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(f.getPassword())
                        .equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT);
                    f.setEchoChar('*');
                }
            }
            public void focusLost(FocusEvent e) {
                if (f.getPassword().length == 0) {
                    f.setEchoChar((char) 0);
                    f.setText(placeholder);
                    f.setForeground(SUBTEXT);
                }
            }
        });
        return f;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
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