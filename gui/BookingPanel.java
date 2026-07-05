package tripms.gui;

import tripms.model.*;
import tripms.service.DataStore;
import tripms.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BookingPanel extends JPanel {

    private final User currentUser;
    private final MainFrame mainFrame;

    private JComboBox<Trip> tripCombo;
    private JComboBox<String> paymentCombo;
    private JLabel tripNameLbl, destLbl, dateLbl, budgetLbl, seatsLbl, travelLbl, scheduleLbl;
    private JLabel statusLbl;

    public BookingPanel(User user, MainFrame frame) {
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
        header.add(Theme.title("Book a Seat"), BorderLayout.WEST);
        header.add(Theme.muted("Reserve your seat for an upcoming trip"), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.BG_DARK);
        center.setBorder(new EmptyBorder(0, 28, 28, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 14, 14);

        // ── Left: form ───────────────────────────────────────────────────────
        JPanel form = Theme.card();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel formTitle = Theme.heading("Select Trip & Payment");
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tripLbl = Theme.label("Select Trip");
        tripLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        tripCombo = Theme.<Trip>combo();
        List<Trip> open = DataStore.getInstance().getOpenTrips();
        for (Trip t : open) tripCombo.addItem(t);
        tripCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tripCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tripCombo.addActionListener(e -> refreshDetails());

        JLabel payLbl = Theme.label("Payment Method");
        payLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        paymentCombo = Theme.<String>combo();
        paymentCombo.addItem("Card");
        paymentCombo.addItem("Online Transfer");
        paymentCombo.addItem("Cash");
        paymentCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        paymentCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLbl = new JLabel(" ");
        statusLbl.setFont(Theme.FONT_BODY);
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton bookBtn = Theme.primaryBtn("Confirm Booking & Pay");
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        bookBtn.addActionListener(e -> doBook());

        form.add(formTitle);
        form.add(Box.createVerticalStrut(18));
        addRow(form, tripLbl, tripCombo);
        addRow(form, payLbl, paymentCombo);
        form.add(statusLbl);
        form.add(Box.createVerticalStrut(16));
        form.add(bookBtn);
        form.add(Box.createVerticalGlue());

        // ── Right: trip detail card ──────────────────────────────────────────
        JPanel detail = Theme.card();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));

        JLabel detailTitle = Theme.heading("Trip Details");
        detailTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        tripNameLbl = Theme.label("—");
        tripNameLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tripNameLbl.setForeground(Theme.TEXT_PRI);
        tripNameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        destLbl     = infoRow("📍 Destination", "—");
        dateLbl     = infoRow("📅 Dates",       "—");
        budgetLbl   = infoRow("💰 Budget",       "—");
        seatsLbl    = infoRow("🎫 Seats",        "—");
        travelLbl   = infoRow("🚌 Transport",   "—");
        scheduleLbl = Theme.muted("—");
        scheduleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        detail.add(detailTitle);
        detail.add(Box.createVerticalStrut(14));
        detail.add(tripNameLbl);
        detail.add(Box.createVerticalStrut(12));
        detail.add(destLbl); detail.add(Box.createVerticalStrut(6));
        detail.add(dateLbl); detail.add(Box.createVerticalStrut(6));
        detail.add(budgetLbl); detail.add(Box.createVerticalStrut(6));
        detail.add(seatsLbl); detail.add(Box.createVerticalStrut(6));
        detail.add(travelLbl); detail.add(Box.createVerticalStrut(10));
        detail.add(Theme.separator()); detail.add(Box.createVerticalStrut(8));
        JLabel schedLabel = Theme.label("Schedule"); schedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detail.add(schedLabel); detail.add(Box.createVerticalStrut(4));
        detail.add(scheduleLbl);
        detail.add(Box.createVerticalGlue());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4; gbc.weighty = 1;
        center.add(form, gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        center.add(detail, gbc);

        add(center, BorderLayout.CENTER);

        if (!open.isEmpty()) refreshDetails();
        else { statusLbl.setForeground(Theme.WARNING); statusLbl.setText("No open trips available at the moment."); }
    }

    private void addRow(JPanel p, JLabel lbl, JComponent comp) {
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(comp);
        p.add(Box.createVerticalStrut(12));
    }

    private JLabel infoRow(String label, String value) {
        JLabel l = new JLabel(label + ": " + value);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_SEC);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void refreshDetails() {
        Trip t = (Trip) tripCombo.getSelectedItem();
        if (t == null) return;
        tripNameLbl.setText(t.getTripName());
        destLbl.setText("📍 Destination: " + t.getDestination());
        dateLbl.setText("📅 Dates: " + t.getStartDate() + " → " + t.getEndDate() + " (" + t.getDurationDays() + " days)");
        budgetLbl.setText("💰 Budget: PKR " + String.format("%.0f", t.getBudgetPerStudent()) + " per student");
        seatsLbl.setText("🎫 Seats: " + t.getCurrentParticipants() + "/" + t.getParticipationLimit());
        travelLbl.setText("🚌 Transport: " + t.getTravelMode());
        scheduleLbl.setText("<html>" + t.getSchedule().replace("\n","<br>") + "</html>");
        statusLbl.setText(" ");
    }

    private void doBook() {
        Trip t = (Trip) tripCombo.getSelectedItem();
        if (t == null) {
            statusLbl.setForeground(Theme.DANGER);
            statusLbl.setText("Please select a trip."); return;
        }
        String method = (String) paymentCombo.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("<html><b>Confirm Booking</b><br><br>" +
                        "Trip: %s<br>Destination: %s<br>Amount: PKR %.0f<br>Payment: %s<br><br>" +
                        "Proceed to payment?</html>",
                        t.getTripName(), t.getDestination(), t.getBudgetPerStudent(), method),
                "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        Booking b = DataStore.getInstance().bookSeat(currentUser.getUserId(), t.getTripId(), method);

        if (b != null) {
            statusLbl.setForeground(Theme.SUCCESS);
            statusLbl.setText("✓ Booking confirmed! Seat: " + b.getSeatNumber() + " | Booking ID: " + b.getBookingId());
            JOptionPane.showMessageDialog(this,
                    String.format("<html><b>✓ Booking Confirmed!</b><br><br>" +
                            "Booking ID : %s<br>Seat Number: %s<br>Trip       : %s<br>Amount Paid: PKR %.0f<br>" +
                            "A confirmation email has been sent to %s</html>",
                            b.getBookingId(), b.getSeatNumber(),
                            t.getTripName(), b.getAmountPaid(),
                            currentUser.getEmail()),
                    "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
            refreshDetails();
        } else {
            statusLbl.setForeground(Theme.DANGER);
            boolean alreadyBooked = DataStore.getInstance()
                    .getBookingsForStudent(currentUser.getUserId()).stream()
                    .anyMatch(bk -> bk.getTripId().equals(t.getTripId())
                            && bk.getBookingStatus() == Booking.BookingStatus.CONFIRMED);
            if (alreadyBooked)
                statusLbl.setText("You already have a confirmed booking for this trip.");
            else if (t.isFull())
                statusLbl.setText("Sorry, this trip is full. No seats available.");
            else
                statusLbl.setText("Booking failed. Please try again.");
        }
    }
}
