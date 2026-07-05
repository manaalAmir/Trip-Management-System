package tripms.gui;

import tripms.model.*;
import tripms.service.DataStore;
import tripms.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyBookingsPanel extends JPanel {

    private final User currentUser;
    private final MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel model;

    public MyBookingsPanel(User user, MainFrame frame) {
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

        JPanel titles = new JPanel();
        titles.setBackground(Theme.BG_DARK);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(Theme.title("My Bookings"));
        titles.add(Box.createVerticalStrut(4));
        titles.add(Theme.muted("Your reserved seats and payment status"));

        JButton refreshBtn = Theme.ghostBtn("↻ Refresh");
        refreshBtn.addActionListener(e -> loadData());

        header.add(titles, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Booking ID", "Trip", "Destination", "Date", "Amount (PKR)", "Payment", "Status", "Seat"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = Theme.styledTable(model);
        table.setRowHeight(36);

        JScrollPane sp = Theme.scroll(table);
        sp.setBorder(new EmptyBorder(0, 28, 0, 28));
        add(sp, BorderLayout.CENTER);

        // Bottom: action buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        bottom.setBackground(Theme.BG_DARK);
        bottom.setBorder(new EmptyBorder(8, 28, 16, 28));

        JButton cancelBtn = Theme.dangerBtn("Cancel Selected Booking");
        cancelBtn.addActionListener(e -> doCancel());
        JLabel hint = Theme.muted("Select a row to cancel a booking. Refunds processed within 3 working days.");

        bottom.add(cancelBtn);
        bottom.add(hint);
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Booking> bookings = DataStore.getInstance().getBookingsForStudent(currentUser.getUserId());
        for (Booking b : bookings) {
            Trip t = DataStore.getInstance().getTrip(b.getTripId());
            model.addRow(new Object[]{
                    b.getBookingId(),
                    t != null ? t.getTripName()  : b.getTripId(),
                    t != null ? t.getDestination(): "—",
                    b.getBookingDate(),
                    String.format("%.0f", b.getAmountPaid()),
                    b.getPaymentMethod(),
                    b.getBookingStatus().name(),
                    b.getSeatNumber()
            });
        }
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You have no bookings yet. Go to 'Make Booking' to reserve a seat.",
                    "No Bookings", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void doCancel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String bookingId = (String) model.getValueAt(row, 0);
        String status    = (String) model.getValueAt(row, 6);

        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(this, "This booking is already cancelled.", "Already Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Cancel Booking " + bookingId + "?</b><br><br>" +
                "A refund will be processed within 3 working days.<br>" +
                "This action cannot be undone.</html>",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = DataStore.getInstance().cancelBooking(bookingId);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "<html><b>Booking Cancelled</b><br>Refund will be processed within 3 working days.<br>A notification has been sent to " + currentUser.getEmail() + "</html>",
                        "Cancellation Confirmed", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cancellation failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
