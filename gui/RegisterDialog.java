package tripms.gui;

import tripms.model.User;
import tripms.service.DataStore;
import tripms.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private JTextField     nameField, emailField;
    private JPasswordField passField, confirmField;
    private JComboBox<String> roleCombo;
    private JLabel statusLabel;

    public RegisterDialog(Frame owner) {
        super(owner, "Create New Account", true);
        setSize(440, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel();
        root.setBackground(Theme.BG_DARK);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(30, 36, 30, 36));

        JLabel title = Theme.heading("Create Account");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = Theme.muted("Fill in the details to register");
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField    = Theme.field();    nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        emailField   = Theme.field();    emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passField    = Theme.passField(); passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        confirmField = Theme.passField(); confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        roleCombo    = Theme.combo();
        roleCombo.addItem("STUDENT");
        roleCombo.addItem("TEACHER");
        roleCombo.addItem("STAFF");
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton regBtn = Theme.primaryBtn("Create Account");
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.addActionListener(e -> doRegister());

        JButton cancelBtn = Theme.ghostBtn("Cancel");
        cancelBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cancelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBtn.addActionListener(e -> dispose());

        // Helper
        java.util.function.BiConsumer<String, JComponent> addRow = (lbl, comp) -> {
            JLabel l = Theme.label(lbl);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            comp.setAlignmentX(Component.LEFT_ALIGNMENT);
            root.add(l);
            root.add(Box.createVerticalStrut(4));
            root.add(comp);
            root.add(Box.createVerticalStrut(12));
        };

        root.add(title);
        root.add(Box.createVerticalStrut(4));
        root.add(subLbl);
        root.add(Box.createVerticalStrut(20));
        addRow.accept("Full Name", nameField);
        addRow.accept("Email Address", emailField);
        addRow.accept("Password", passField);
        addRow.accept("Confirm Password", confirmField);
        addRow.accept("Role", roleCombo);
        root.add(statusLabel);
        root.add(Box.createVerticalStrut(8));
        root.add(regBtn);
        root.add(Box.createVerticalStrut(8));
        root.add(cancelBtn);

        setContentPane(root);
        getContentPane().setBackground(Theme.BG_DARK);
    }

    private void doRegister() {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String pass    = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String role    = (String) roleCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("All fields are required."); return;
        }
        if (!email.contains("@")) {
            statusLabel.setText("Enter a valid email address."); return;
        }
        if (!pass.equals(confirm)) {
            statusLabel.setText("Passwords do not match."); return;
        }
        if (pass.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters."); return;
        }
        if (DataStore.getInstance().emailExists(email)) {
            statusLabel.setText("Email already registered. Use a different email."); return;
        }

        User user = DataStore.getInstance().registerUser(name, email, pass, role);
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nYou can now log in.",
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            statusLabel.setText("Registration failed. Please try again.");
        }
    }
}
