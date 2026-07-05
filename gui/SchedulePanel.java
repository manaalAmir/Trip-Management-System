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
//  SCHEDULE PANEL
// ═══════════════════════════════════════════════════════════════════════════════
class SchedulePanel extends JPanel {
    public SchedulePanel(User user) {
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));
        header.add(Theme.title("Trip Schedules"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel scrollContent = new JPanel();
        scrollContent.setBackground(Theme.BG_DARK);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(0, 28, 28, 28));

        List<Trip> trips = DataStore.getInstance().getAllTrips();
        for (Trip t : trips) {
            JPanel card = Theme.card();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            JLabel name = Theme.heading(t.getTripName() + " → " + t.getDestination());
            name.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel dates = Theme.muted("📅 " + t.getStartDate() + " to " + t.getEndDate());
            dates.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel schedLabel = Theme.label("Schedule:");
            schedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea schedText = Theme.textArea();
            schedText.setText(t.getSchedule().isEmpty() ? "Schedule not set yet." : t.getSchedule());
            schedText.setEditable(false);
            schedText.setBackground(Theme.BG_FIELD);
            schedText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            schedText.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel precLabel = Theme.label("Safety Precautions:");
            precLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea precText = Theme.textArea();
            precText.setText(t.getPrecautions().isEmpty() ? "No precautions set." : t.getPrecautions());
            precText.setEditable(false);
            precText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            precText.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(name); card.add(Box.createVerticalStrut(4));
            card.add(dates); card.add(Box.createVerticalStrut(10));
            card.add(schedLabel); card.add(Box.createVerticalStrut(4));
            card.add(schedText); card.add(Box.createVerticalStrut(10));
            card.add(precLabel); card.add(Box.createVerticalStrut(4));
            card.add(precText);

            scrollContent.add(card);
            scrollContent.add(Box.createVerticalStrut(12));
        }
        add(Theme.scroll(scrollContent), BorderLayout.CENTER);
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  TRIP HISTORY PANEL
// ═══════════════════════════════════════════════════════════════════════════════
class TripHistoryPanel extends JPanel {
    public TripHistoryPanel(User user) {
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));
        header.add(Theme.title("Trip History"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Trip ID", "Trip Name", "Destination", "Dates", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Past bookings as history
        List<Booking> bookings = DataStore.getInstance().getBookingsForStudent(user.getUserId());
        for (Booking b : bookings) {
            Trip t = DataStore.getInstance().getTrip(b.getTripId());
            if (t != null) {
                model.addRow(new Object[]{
                        t.getTripId(), t.getTripName(), t.getDestination(),
                        t.getStartDate() + " → " + t.getEndDate(), t.getStatus().name()
                });
            }
        }
        // Also completed trips from history list
        List<Trip> history = DataStore.getInstance().getTripHistoryForStudent(user.getUserId());
        for (Trip t : history) {
            model.addRow(new Object[]{
                    t.getTripId(), t.getTripName(), t.getDestination(),
                    t.getStartDate() + " → " + t.getEndDate(), t.getStatus().name()
            });
        }

        JTable table = Theme.styledTable(model);
        JScrollPane sp = Theme.scroll(table);
        sp.setBorder(new EmptyBorder(0, 28, 28, 28));
        add(sp, BorderLayout.CENTER);
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  CREATE TRIP PANEL
// ═══════════════════════════════════════════════════════════════════════════════
class CreateTripPanel extends JPanel {
    private final User currentUser;
    private final MainFrame mainFrame;

    private JTextField nameField, destField, startField, endField, budgetField, limitField;
    private JComboBox<String> travelCombo, statusCombo;
    private JTextArea scheduleArea, precArea, accomArea;
    private JTextField busCountField;
    private JLabel statusLbl;

    public CreateTripPanel(User user, MainFrame frame) {
        this.currentUser = user;
        this.mainFrame   = frame;
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));
        header.add(Theme.title("Create New Trip"), BorderLayout.WEST);
        header.add(Theme.muted("Fill all required fields to create a trip"), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_DARK);
        form.setBorder(new EmptyBorder(0, 28, 28, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 12, 12);

        // left column
        JPanel left = Theme.card();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(Theme.heading("Trip Information")); left.add(Box.createVerticalStrut(16));

        nameField   = Theme.field(); nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        destField   = Theme.field(); destField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        startField  = Theme.field(); startField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); startField.setText("2026-01-01");
        endField    = Theme.field(); endField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));   endField.setText("2026-01-03");
        budgetField = Theme.field(); budgetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        limitField  = Theme.field(); limitField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        busCountField = Theme.field(); busCountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); busCountField.setText("1");

        travelCombo = Theme.<String>combo();
        travelCombo.addItem("Bus"); travelCombo.addItem("Van"); travelCombo.addItem("Train"); travelCombo.addItem("Mixed");
        travelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        addFormRow(left, "Trip Name *", nameField);
        addFormRow(left, "Destination *", destField);
        addFormRow(left, "Start Date * (YYYY-MM-DD)", startField);
        addFormRow(left, "End Date * (YYYY-MM-DD)", endField);
        addFormRow(left, "Budget per Student (PKR) *", budgetField);
        addFormRow(left, "Participation Limit *", limitField);
        addFormRow(left, "Travel Mode", travelCombo);
        addFormRow(left, "Number of Buses", busCountField);

        // right column
        JPanel right = Theme.card();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(Theme.heading("Additional Details")); right.add(Box.createVerticalStrut(16));

        scheduleArea = Theme.textArea();
        precArea     = Theme.textArea();
        accomArea    = Theme.textArea();

        JScrollPane schedSp = Theme.scroll(scheduleArea); schedSp.setPreferredSize(new Dimension(0, 90));
        JScrollPane precSp  = Theme.scroll(precArea);     precSp.setPreferredSize(new Dimension(0, 90));
        JScrollPane accomSp = Theme.scroll(accomArea);    accomSp.setPreferredSize(new Dimension(0, 90));

        addFormRowScroll(right, "Trip Schedule", schedSp);
        addFormRowScroll(right, "Safety Precautions", precSp);
        addFormRowScroll(right, "Accommodation Info", accomSp);

        // Bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        bottomBar.setBackground(Theme.BG_DARK);
        bottomBar.setBorder(new EmptyBorder(0, 28, 16, 28));

        statusLbl = new JLabel(" ");
        statusLbl.setFont(Theme.FONT_BODY);

        JButton createBtn = Theme.primaryBtn("Create Trip");
        createBtn.addActionListener(e -> doCreate());

        JButton clearBtn = Theme.ghostBtn("Clear Form");
        clearBtn.addActionListener(e -> clearForm());

        bottomBar.add(createBtn);
        bottomBar.add(clearBtn);
        bottomBar.add(statusLbl);

        g.gridx = 0; g.gridy = 0; g.weightx = 0.5; g.weighty = 1; g.fill = GridBagConstraints.BOTH;
        form.add(left, g);
        g.gridx = 1;
        form.add(right, g);

        add(Theme.scroll(form), BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel p, String lbl, JComponent c) {
        JLabel l = Theme.label(lbl); l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(4)); p.add(c); p.add(Box.createVerticalStrut(10));
    }
    private void addFormRowScroll(JPanel p, String lbl, JScrollPane sp) {
        JLabel l = Theme.label(lbl); l.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(4)); p.add(sp); p.add(Box.createVerticalStrut(10));
    }

    private void doCreate() {
        String name   = nameField.getText().trim();
        String dest   = destField.getText().trim();
        String start  = startField.getText().trim();
        String end    = endField.getText().trim();
        String budgetStr = budgetField.getText().trim();
        String limitStr  = limitField.getText().trim();

        if (name.isEmpty() || dest.isEmpty() || start.isEmpty() || end.isEmpty() || budgetStr.isEmpty() || limitStr.isEmpty()) {
            setStatus("All required fields (*) must be filled.", Theme.DANGER); return;
        }

        double budget; int limit; int buses;
        try { budget = Double.parseDouble(budgetStr); } catch (NumberFormatException ex) {
            setStatus("Budget must be a valid number.", Theme.DANGER); return;
        }
        try { limit = Integer.parseInt(limitStr); if (limit <= 0) throw new NumberFormatException(); } catch (NumberFormatException ex) {
            setStatus("Participation limit must be a positive integer.", Theme.DANGER); return;
        }
        try { buses = Integer.parseInt(busCountField.getText().trim()); } catch (NumberFormatException ex) { buses = 1; }

        int duration;
        try {
            java.time.LocalDate s = java.time.LocalDate.parse(start);
            java.time.LocalDate e2 = java.time.LocalDate.parse(end);
            if (!e2.isAfter(s)) { setStatus("End date must be after start date.", Theme.DANGER); return; }
            duration = (int)(e2.toEpochDay() - s.toEpochDay());
        } catch (Exception ex) {
            setStatus("Dates must be in YYYY-MM-DD format.", Theme.DANGER); return;
        }

        String travel = (String) travelCombo.getSelectedItem();
        Trip t = DataStore.getInstance().createTrip(name, dest, start, end, duration,
                budget, limit, currentUser.getUserId(), travel);
        t.setStatus(Trip.Status.SCHEDULED);
        t.setSchedule(scheduleArea.getText().trim());
        t.setPrecautions(precArea.getText().isEmpty()
                ? "Always stay with your group.\nCarry your ID.\nFollow teacher instructions." : precArea.getText().trim());
        t.setAccommodationInfo(accomArea.getText().trim());
        t.setBusCount(buses);

        setStatus("✓ Trip '" + name + "' created successfully! ID: " + t.getTripId(), Theme.SUCCESS);
        JOptionPane.showMessageDialog(this,
                "<html><b>Trip Created Successfully!</b><br><br>Trip: " + name + "<br>ID: " + t.getTripId() +
                "<br>Destination: " + dest + "<br>Status: SCHEDULED</html>",
                "Trip Created", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
    }

    private void clearForm() {
        nameField.setText(""); destField.setText(""); startField.setText("2026-01-01");
        endField.setText("2026-01-03"); budgetField.setText(""); limitField.setText("");
        busCountField.setText("1"); scheduleArea.setText(""); precArea.setText(""); accomArea.setText("");
    }
    private void setStatus(String msg, Color c) {
        statusLbl.setText(msg); statusLbl.setForeground(c);
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  MANAGE TRIPS PANEL
// ═══════════════════════════════════════════════════════════════════════════════
class ManageTripsPanel extends JPanel {
    private final User currentUser;
    private final MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel model;

    public ManageTripsPanel(User user, MainFrame frame) {
        this.currentUser = user; this.mainFrame = frame;
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
        titles.add(Theme.title("Manage Trips"));
        titles.add(Theme.muted("View, modify and update trip status"));

        JButton refreshBtn = Theme.ghostBtn("↻ Refresh");
        refreshBtn.addActionListener(e -> loadData());
        header.add(titles, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID","Trip Name","Destination","Start","End","Budget","Limit","Enrolled","Transport","Status"};
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

        JButton openBtn   = Theme.primaryBtn("Set → OPEN");
        JButton closeBtn  = Theme.ghostBtn("Set → CLOSED");
        JButton editBtn   = Theme.ghostBtn("Edit Trip");

        openBtn.addActionListener(e  -> setStatus(Trip.Status.OPEN));
        closeBtn.addActionListener(e -> setStatus(Trip.Status.BOOKING_CLOSED));
        editBtn.addActionListener(e  -> editTrip());

        bottom.add(openBtn); bottom.add(closeBtn); bottom.add(editBtn);
        bottom.add(Theme.muted("Select a row to perform actions"));
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Trip> trips = currentUser.getRole().equals("STAFF")
                ? DataStore.getInstance().getAllTrips()
                : DataStore.getInstance().getTripsForTeacher(currentUser.getUserId());
        for (Trip t : trips) {
            model.addRow(new Object[]{
                    t.getTripId(), t.getTripName(), t.getDestination(),
                    t.getStartDate(), t.getEndDate(),
                    String.format("%.0f", t.getBudgetPerStudent()),
                    t.getParticipationLimit(), t.getCurrentParticipants(),
                    t.getTravelMode(), t.getStatus().name()
            });
        }
    }

    private Trip getSelectedTrip() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a trip row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return DataStore.getInstance().getTrip((String) model.getValueAt(row, 0));
    }

    private void setStatus(Trip.Status s) {
        Trip t = getSelectedTrip();
        if (t == null) return;
        t.setStatus(s);
        DataStore.getInstance().updateTrip(t);
        JOptionPane.showMessageDialog(this, "Trip status updated to: " + s, "Updated", JOptionPane.INFORMATION_MESSAGE);
        loadData();
    }

    private void editTrip() {
        Trip t = getSelectedTrip();
        if (t == null) return;

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Theme.BG_CARD);
        JTextField nameF = Theme.field(); nameF.setText(t.getTripName());
        JTextField destF = Theme.field(); destF.setText(t.getDestination());
        JTextField budgF = Theme.field(); budgF.setText(String.valueOf(t.getBudgetPerStudent()));
        JTextField limF  = Theme.field(); limF.setText(String.valueOf(t.getParticipationLimit()));

        form.add(Theme.label("Trip Name")); form.add(nameF);
        form.add(Theme.label("Destination")); form.add(destF);
        form.add(Theme.label("Budget/Student")); form.add(budgF);
        form.add(Theme.label("Participation Limit")); form.add(limF);

        int res = JOptionPane.showConfirmDialog(this, form, "Edit Trip: " + t.getTripName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            if (!nameF.getText().trim().isEmpty()) t.setTripName(nameF.getText().trim());
            if (!destF.getText().trim().isEmpty())  t.setDestination(destF.getText().trim());
            try { t.setBudgetPerStudent(Double.parseDouble(budgF.getText().trim())); } catch (Exception ignored) {}
            try {
                int lim = Integer.parseInt(limF.getText().trim());
                if (lim > 0) t.setParticipationLimit(lim);
            } catch (Exception ignored) {}
            DataStore.getInstance().updateTrip(t);
            loadData();
            JOptionPane.showMessageDialog(this, "Trip updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
