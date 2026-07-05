package tripms.gui;

import tripms.model.*;
import tripms.service.DataStore;
import tripms.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// ═══════════════════════════════════════════════════════════════════════════════
//  ALL BOOKINGS (Teacher / Staff)
// ═══════════════════════════════════════════════════════════════════════════════
class AllBookingsPanel extends JPanel {
    private final User currentUser;
    private JTable table;
    private DefaultTableModel model;

    public AllBookingsPanel(User user) {
        this.currentUser = user;
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));
        JPanel titles = new JPanel(); titles.setBackground(Theme.BG_DARK);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(Theme.title("All Bookings"));
        titles.add(Theme.muted("View and manage all student bookings"));
        JButton refreshBtn = Theme.ghostBtn("↻ Refresh");
        refreshBtn.addActionListener(e -> loadData());
        header.add(titles, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Booking ID","Student","Trip","Destination","Date","Amount","Payment","Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = Theme.styledTable(model);
        JScrollPane sp = Theme.scroll(table);
        sp.setBorder(new EmptyBorder(0, 28, 0, 28));
        add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottom.setBackground(Theme.BG_DARK);
        bottom.setBorder(new EmptyBorder(8, 28, 16, 28));
        JButton cancelBtn = Theme.dangerBtn("Cancel Selected");
        cancelBtn.addActionListener(e -> doCancel());
        bottom.add(cancelBtn);
        bottom.add(Theme.muted("Administrators can cancel any booking"));
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Booking> list = DataStore.getInstance().getAllBookings();
        for (Booking b : list) {
            User u = DataStore.getInstance().getUser(b.getStudentId());
            Trip t = DataStore.getInstance().getTrip(b.getTripId());
            model.addRow(new Object[]{
                    b.getBookingId(),
                    u != null ? u.getUsername() : b.getStudentId(),
                    t != null ? t.getTripName() : b.getTripId(),
                    t != null ? t.getDestination() : "—",
                    b.getBookingDate(),
                    String.format("PKR %.0f", b.getAmountPaid()),
                    b.getPaymentMethod(),
                    b.getBookingStatus().name()
            });
        }
    }

    private void doCancel() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a booking row.","",JOptionPane.WARNING_MESSAGE); return; }
        String bid    = (String) model.getValueAt(row, 0);
        String status = (String) model.getValueAt(row, 7);
        if ("CANCELLED".equals(status)) { JOptionPane.showMessageDialog(this,"Already cancelled.","",JOptionPane.INFORMATION_MESSAGE); return; }

        int c = JOptionPane.showConfirmDialog(this, "Cancel booking " + bid + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            DataStore.getInstance().cancelBooking(bid);
            loadData();
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  BUS & DRIVER PANEL
// ═══════════════════════════════════════════════════════════════════════════════
class BusDriverPanel extends JPanel {
    private DefaultTableModel model;

    public BusDriverPanel() {
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));
        header.add(Theme.title("Bus & Driver Management"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Driver ID","Name","License No.","Contact","Assigned Trip"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = Theme.styledTable(model);
        JScrollPane sp = Theme.scroll(table);
        sp.setBorder(new EmptyBorder(0, 28, 0, 28));
        add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottom.setBackground(Theme.BG_DARK);
        bottom.setBorder(new EmptyBorder(8, 28, 16, 28));

        JButton addBtn = Theme.primaryBtn("+ Add Driver");
        addBtn.addActionListener(e -> addDriver());
        bottom.add(addBtn);
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        for (Driver d : DataStore.getInstance().getAllDrivers()) {
            Trip t = DataStore.getInstance().getTrip(d.getAssignedTripId());
            model.addRow(new Object[]{
                    d.getDriverId(), d.getName(), d.getLicenseNumber(),
                    d.getContactNumber(),
                    t != null ? t.getTripName() : (d.getAssignedTripId().isEmpty() ? "Unassigned" : d.getAssignedTripId())
            });
        }
    }

    private void addDriver() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Theme.BG_CARD);
        JTextField nameF    = Theme.field();
        JTextField licenseF = Theme.field();
        JTextField contactF = Theme.field();
        form.add(Theme.label("Driver Name *")); form.add(nameF);
        form.add(Theme.label("License Number *")); form.add(licenseF);
        form.add(Theme.label("Contact Number *")); form.add(contactF);

        int res = JOptionPane.showConfirmDialog(this, form, "Add New Driver",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim();
            String lic  = licenseF.getText().trim();
            String cont = contactF.getText().trim();
            if (name.isEmpty() || lic.isEmpty() || cont.isEmpty()) {
                JOptionPane.showMessageDialog(this,"All fields are required.","Error",JOptionPane.ERROR_MESSAGE); return;
            }
            DataStore.getInstance().addDriver(name, lic, cont);
            loadData();
            JOptionPane.showMessageDialog(this, "Driver added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  ACCOMMODATION PANEL
// ═══════════════════════════════════════════════════════════════════════════════
class AccommodationPanel extends JPanel {
    public AccommodationPanel() {
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));
        header.add(Theme.title("Accommodation Management"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel scrollContent = new JPanel();
        scrollContent.setBackground(Theme.BG_DARK);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(0, 28, 28, 28));

        List<Trip> trips = DataStore.getInstance().getAllTrips();
        for (Trip t : trips) {
            JPanel card = Theme.card();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            JLabel name = Theme.heading(t.getTripName() + " — " + t.getDestination());
            name.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel accomLabel = Theme.label("Accommodation Details:");
            accomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea accomArea = Theme.textArea();
            accomArea.setText(t.getAccommodationInfo().isEmpty() ? "Not set." : t.getAccommodationInfo());
            accomArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            accomArea.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton saveBtn = Theme.ghostBtn("Update");
            saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            saveBtn.addActionListener(e -> {
                t.setAccommodationInfo(accomArea.getText().trim());
                DataStore.getInstance().updateTrip(t);
                JOptionPane.showMessageDialog(this, "Accommodation updated.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            });

            card.add(name); card.add(Box.createVerticalStrut(8));
            card.add(accomLabel); card.add(Box.createVerticalStrut(4));
            card.add(Theme.scroll(accomArea)); card.add(Box.createVerticalStrut(8));
            card.add(saveBtn);

            scrollContent.add(card);
            scrollContent.add(Box.createVerticalStrut(12));
        }
        add(Theme.scroll(scrollContent), BorderLayout.CENTER);
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  MONITOR / REPORT PANEL
// ═══════════════════════════════════════════════════════════════════════════════
class MonitorPanel extends JPanel {
    private JTextArea reportArea;
    private JComboBox<Trip> tripCombo;

    public MonitorPanel() {
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));
        header.add(Theme.title("Monitor & Reports"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Stat cards row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        statsRow.setBackground(Theme.BG_DARK);
        statsRow.setBorder(new EmptyBorder(0, 28, 16, 28));

        List<Trip> trips = DataStore.getInstance().getAllTrips();
        long openCount  = trips.stream().filter(t -> t.getStatus()==Trip.Status.OPEN).count();
        int  totalBooks = DataStore.getInstance().getAllBookings().size();
        int  totalUsers = DataStore.getInstance().getAllUsers().size();
        long cancelled  = DataStore.getInstance().getAllBookings().stream()
                .filter(b -> b.getBookingStatus()==Booking.BookingStatus.CANCELLED).count();

        statsRow.add(statCard("Total Trips",     String.valueOf(trips.size()), Theme.ACCENT));
        statsRow.add(statCard("Open Trips",      String.valueOf(openCount),   Theme.SUCCESS));
        statsRow.add(statCard("Total Bookings",  String.valueOf(totalBooks),  Theme.ACCENT2));
        statsRow.add(statCard("Cancellations",   String.valueOf(cancelled),   Theme.DANGER));
        statsRow.add(statCard("Registered Users",String.valueOf(totalUsers),  Theme.WARNING));

        add(statsRow, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.BG_DARK);
        center.setBorder(new EmptyBorder(0, 28, 28, 28));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(Theme.BG_DARK);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        tripCombo = Theme.<Trip>combo();
        for (Trip t : trips) tripCombo.addItem(t);
        tripCombo.setPreferredSize(new Dimension(260, 36));

        JButton genBtn = Theme.primaryBtn("Generate Report");
        genBtn.addActionListener(e -> generateReport());

        toolbar.add(Theme.label("Select Trip:"));
        toolbar.add(tripCombo);
        toolbar.add(genBtn);

        reportArea = Theme.textArea();
        reportArea.setFont(Theme.FONT_MONO);
        reportArea.setEditable(false);
        reportArea.setText("Select a trip and click 'Generate Report'");

        center.add(toolbar, BorderLayout.NORTH);
        center.add(Theme.scroll(reportArea), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = Theme.card();
        card.setPreferredSize(new Dimension(150, 80));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 26));
        v.setForeground(accent);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = Theme.muted(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(v); card.add(l);
        return card;
    }

    private void generateReport() {
        Trip t = (Trip) tripCombo.getSelectedItem();
        if (t == null) return;
        reportArea.setText(DataStore.getInstance().generateTripReport(t.getTripId()));
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  USER MANAGEMENT PANEL (Staff only)
// ═══════════════════════════════════════════════════════════════════════════════
class UserManagementPanel extends JPanel {
    private DefaultTableModel model;

    public UserManagementPanel() {
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));
        JPanel t = new JPanel(); t.setBackground(Theme.BG_DARK);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(Theme.title("All Users"));
        t.add(Theme.muted("View all registered students, teachers and staff"));
        header.add(t, BorderLayout.WEST);
        JButton refreshBtn = Theme.ghostBtn("↻ Refresh");
        refreshBtn.addActionListener(e -> loadData());
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"User ID","Username","Email","Role"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = Theme.styledTable(model);
        JScrollPane sp = Theme.scroll(table);
        sp.setBorder(new EmptyBorder(0, 28, 28, 28));
        add(sp, BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        for (User u : DataStore.getInstance().getAllUsers()) {
            model.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getEmail(), u.getRole()});
        }
    }
}
