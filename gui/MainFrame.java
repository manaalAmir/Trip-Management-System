package tripms.gui;

import tripms.model.*;
import tripms.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Shell frame: sidebar nav + content panel.
 * Panels are swapped in the CONTENT area based on the selected nav item.
 */
public class MainFrame extends JFrame {

    private final User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Nav button references to allow highlight
    private JButton activeBtn;

    public MainFrame(User user) {
        super("Trip Management System");
        this.currentUser = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);

        setContentPane(root);

        // show default panel
        showPanel("trips");
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(Theme.BG_CARD);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(210, 0));
        side.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Logo area
        JPanel logo = new JPanel();
        logo.setBackground(Theme.BG_DARK);
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBorder(new EmptyBorder(20, 16, 16, 16));
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel logoIcon = new JLabel("✈ TripMS");
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logoIcon.setForeground(Theme.ACCENT);
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleTag = new JLabel(currentUser.getRole());
        roleTag.setFont(Theme.FONT_SMALL);
        roleTag.setForeground(Theme.TEXT_MUTED);
        roleTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        logo.add(logoIcon);
        logo.add(Box.createVerticalStrut(4));
        logo.add(roleTag);
        side.add(logo);

        // User info
        JPanel userInfo = new JPanel();
        userInfo.setBackground(Theme.BG_CARD);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBorder(new EmptyBorder(12, 16, 12, 16));
        userInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel uname = Theme.label("👤 " + currentUser.getUsername());
        uname.setFont(Theme.FONT_SUB);
        uname.setForeground(Theme.TEXT_PRI);
        uname.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel uemail = Theme.muted(currentUser.getEmail());
        uemail.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInfo.add(uname);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(uemail);
        side.add(userInfo);
        side.add(navSeparator("MENU"));

        // Role-based navigation
        String role = currentUser.getRole();

        addNavBtn(side, "🗺  Browse Trips",    "trips");
        addNavBtn(side, "📅  Trip Schedule",   "schedule");

        if (role.equals("STUDENT")) {
            addNavBtn(side, "🎫  My Bookings",   "mybookings");
            addNavBtn(side, "🕐  Trip History",  "history");
            addNavBtn(side, "💳  Make Booking",  "book");
        }

        if (role.equals("TEACHER") || role.equals("STAFF")) {
            addNavBtn(side, "➕  Create Trip",   "createtrip");
            addNavBtn(side, "✏️  Manage Trips",  "managetrips");
            addNavBtn(side, "👥  All Bookings",  "allbookings");
        }

        if (role.equals("STAFF")) {
            addNavBtn(side, "🚌  Bus & Drivers", "buses");
            addNavBtn(side, "🏨  Accommodation", "accommodation");
            addNavBtn(side, "📊  Monitor / Report","monitor");
            addNavBtn(side, "👤  All Users",     "users");
        }

        side.add(Box.createVerticalGlue());
        side.add(navSeparator(""));

        JButton logoutBtn = navButton("🚪  Log Out", Theme.DANGER);
        logoutBtn.addActionListener(e -> doLogout());
        side.add(logoutBtn);
        side.add(Box.createVerticalStrut(12));

        return side;
    }

    private JPanel navSeparator(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        p.setBorder(new EmptyBorder(6, 16, 0, 16));
        if (!text.isEmpty()) {
            JLabel l = Theme.muted(text);
            l.setFont(new Font("Segoe UI", Font.BOLD, 10));
            p.add(l, BorderLayout.WEST);
        }
        return p;
    }

    private void addNavBtn(JPanel parent, String label, String panelKey) {
        JButton btn = navButton(label, Theme.TEXT_SEC);
        btn.addActionListener(e -> {
            setActiveBtn(btn);
            showPanel(panelKey);
        });
        parent.add(btn);
    }

    private JButton navButton(String label, Color fg) {
        JButton b = new JButton(label);
        b.setFont(Theme.FONT_BODY);
        b.setForeground(fg);
        b.setBackground(Theme.BG_CARD);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b != activeBtn) b.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (b != activeBtn) b.setBackground(Theme.BG_CARD);
            }
        });
        return b;
    }

    private void setActiveBtn(JButton btn) {
        if (activeBtn != null) {
            activeBtn.setBackground(Theme.BG_CARD);
            activeBtn.setForeground(Theme.TEXT_SEC);
        }
        activeBtn = btn;
        btn.setBackground(new Color(20, 184, 166, 40));
        btn.setForeground(Theme.ACCENT);
    }

    // ── Content area ─────────────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.BG_DARK);

        // Register all panels
        contentPanel.add(new TripListPanel(currentUser, this),        "trips");
        contentPanel.add(new SchedulePanel(currentUser),              "schedule");
        contentPanel.add(new BookingPanel(currentUser, this),         "book");

        if (currentUser.getRole().equals("STUDENT")) {
            contentPanel.add(new MyBookingsPanel(currentUser, this),  "mybookings");
            contentPanel.add(new TripHistoryPanel(currentUser),       "history");
        }

        if (!currentUser.getRole().equals("STUDENT")) {
            contentPanel.add(new CreateTripPanel(currentUser, this),  "createtrip");
            contentPanel.add(new ManageTripsPanel(currentUser, this), "managetrips");
            contentPanel.add(new AllBookingsPanel(currentUser),       "allbookings");
        }

        if (currentUser.getRole().equals("STAFF")) {
            contentPanel.add(new BusDriverPanel(),                    "buses");
            contentPanel.add(new AccommodationPanel(),                "accommodation");
            contentPanel.add(new MonitorPanel(),                      "monitor");
            contentPanel.add(new UserManagementPanel(),               "users");
        }

        return contentPanel;
    }

    public void showPanel(String key) { cardLayout.show(contentPanel, key); }

    /** Refresh a panel by replacing it */
    public void refreshPanel(String key, JPanel newPanel) {
        contentPanel.add(newPanel, key);
        cardLayout.show(contentPanel, key);
    }

    private void doLogout() {
        int c = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?", "Log Out",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}
