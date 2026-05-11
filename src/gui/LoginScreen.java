package gui;

import network.TrafficClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private TrafficClient client;

    private static final Color BG      = new Color(245, 247, 250);
    private static final Color WHITE   = Color.WHITE;
    private static final Color ACCENT  = new Color(37, 99, 235);
    private static final Color DANGER  = new Color(220, 38, 38);
    private static final Color TEXT    = new Color(17, 24, 39);
    private static final Color SUBTEXT = new Color(107, 114, 128);
    private static final Color BORDER  = new Color(209, 213, 219);
    private static final Color SUCCESS = new Color(5, 150, 105);

    public LoginScreen() {
        client = new TrafficClient();
        boolean connected = client.connect();
        if (!connected) {
            JOptionPane.showMessageDialog(null,
                "Cannot connect to server!\n"
                + "Make sure TrafficServer is running first.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Traffic System — Sign in");
        setSize(440, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(40, 40, 40, 40);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(36, 36, 36, 36)));

        // Logo
        JLabel icon = new JLabel("🚦");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Traffic System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(SUBTEXT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        emailField = makeField("Enter your email");
        passwordField = new JPasswordField();
        stylePass(passwordField, "Enter your password");

        // Login button
        JButton loginBtn = new JButton("Sign in");
        loginBtn.setBackground(ACCENT);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(new Color(29, 78, 216));
            }
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(ACCENT);
            }
        });

        emailField.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());

        // Divider
        JPanel divider = new JPanel(new GridLayout(1, 3, 8, 0));
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JSeparator s1 = new JSeparator();
        s1.setForeground(BORDER);
        JLabel orLbl = new JLabel("or", SwingConstants.CENTER);
        orLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orLbl.setForeground(SUBTEXT);
        JSeparator s2 = new JSeparator();
        s2.setForeground(BORDER);
        divider.add(s1); divider.add(orLbl); divider.add(s2);

        // Register button
        JButton registerBtn = new JButton(
            "Create a new account");
        registerBtn.setBackground(WHITE);
        registerBtn.setForeground(ACCENT);
        registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        registerBtn.setFocusPainted(false);
        registerBtn.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 40));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> new RegisterScreen(client));

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(makeLabel("Email address"));
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(divider);
        card.add(Box.createVerticalStrut(16));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);

        add(card, gbc);
    }

    private void doLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || email.equals("Enter your email")) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Please enter your email");
            return;
        }
        if (password.isEmpty()) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("Please enter your password");
            return;
        }

        statusLabel.setForeground(SUBTEXT);
        statusLabel.setText("Signing in...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() {
                return client.sendAndReceive(
                    "LOGIN:" + email + "|" + password);
            }
            protected void done() {
                try {
                    String resp = get();
                    if (resp != null
                            && resp.startsWith("LOGIN_SUCCESS:")) {
                        String[] p = resp.substring(14).split("\\|");
                        int    id   = Integer.parseInt(p[0]);
                        String name = p[1];
                        String role = p[2];
                        dispose();
                        if ("admin".equals(role))
                            new AdminDashboard(client, name);
                        else
                            new DashboardScreen(client, name, id);
                    } else {
                        statusLabel.setForeground(DANGER);
                        statusLabel.setText(
                            "Wrong email or password. Try again.");
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(DANGER);
                    statusLabel.setText("Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private JTextField makeField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(SUBTEXT);
        f.setBackground(WHITE);
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
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT),
                    BorderFactory.createEmptyBorder(
                        10, 14, 10, 14)));
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(SUBTEXT);
                }
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(
                        10, 14, 10, 14)));
            }
        });
        return f;
    }

    private void stylePass(JPasswordField f, String placeholder) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(SUBTEXT);
        f.setBackground(WHITE);
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
                    f.setEchoChar('•');
                }
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT),
                    BorderFactory.createEmptyBorder(
                        10, 14, 10, 14)));
            }
            public void focusLost(FocusEvent e) {
                if (f.getPassword().length == 0) {
                    f.setEchoChar((char) 0);
                    f.setText(placeholder);
                    f.setForeground(SUBTEXT);
                }
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(
                        10, 14, 10, 14)));
            }
        });
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}