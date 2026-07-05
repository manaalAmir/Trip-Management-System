package tripms.gui;

import tripms.model.User;
import tripms.service.DataStore;
import tripms.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField     emailField;
    private JPasswordField passField;
    private JLabel         statusLabel;

    public LoginFrame() {
        super("Trip Management System — Login");
        Theme.applyGlobal();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);

        // ── Top banner ───────────────────────────────────────────────────────
        JPanel banner = new JPanel();
        banner.setBackground(Theme.BG_DARK);
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(new EmptyBorder(50, 40, 30, 40));

        JLabel icon = new JLabel("✈");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setForeground(Theme.ACCENT);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = Theme.title("Trip Management System");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = Theme.muted("School Trip Administration Portal");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        banner.add(icon);
        banner.add(Box.createVerticalStrut(10));
        banner.add(title);
        banner.add(Box.createVerticalStrut(6));
        banner.add(sub);

        // ── Card ─────────────────────────────────────────────────────────────
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 36, 28, 36));

        JLabel loginLbl = Theme.heading("Sign In");
        loginLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLbl = Theme.label("Email Address");
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField = Theme.field();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLbl = Theme.label("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passField = Theme.passField();
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = Theme.primaryBtn("Sign In");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> doLogin());

        passField.addActionListener(e -> doLogin());

        JPanel dividerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        dividerRow.setBackground(Theme.BG_CARD);
        dividerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dividerRow.add(Theme.separator());

        JLabel noAccount = Theme.muted("Don't have an account?");
        noAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton regBtn = Theme.ghostBtn("Create Account");
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        regBtn.addActionListener(e -> openRegister());

        card.add(loginLbl);
        card.add(Box.createVerticalStrut(20));
        card.add(emailLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(passField);
        card.add(Box.createVerticalStrut(6));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(Theme.separator());
        card.add(Box.createVerticalStrut(12));
        card.add(noAccount);
        card.add(Box.createVerticalStrut(6));
        card.add(regBtn);

        // ── Demo hint ────────────────────────────────────────────────────────
        JPanel hint = new JPanel();
        hint.setBackground(Theme.BG_DARK);
        hint.setLayout(new BoxLayout(hint, BoxLayout.Y_AXIS));
        hint.setBorder(new EmptyBorder(10, 40, 20, 40));
        JLabel hintLbl = Theme.muted("Demo: ali@school.edu / pass123  |  khan@school.edu / teach123  |  admin@school.edu / admin123");
        hintLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.add(hintLbl);

        JPanel center = new JPanel();
        center.setBackground(Theme.BG_DARK);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(0, 30, 0, 30));
        center.add(card);

        root.add(banner, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(hint,   BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }
        User user = DataStore.getInstance().login(email, pass);
        if (user == null) {
            statusLabel.setText("Invalid email or password. Please try again.");
            passField.setText("");
            return;
        }
        dispose();
        new MainFrame(user);
    }

    private void openRegister() {
        new RegisterDialog(this);
    }
}
