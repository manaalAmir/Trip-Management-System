package tripms.gui;

import tripms.model.*;
import tripms.service.DataStore;
import tripms.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TripListPanel extends JPanel {

    private final User currentUser;
    private final MainFrame mainFrame;

    public TripListPanel(User user, MainFrame frame) {
        this.currentUser = user;
        this.mainFrame   = frame;
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));

        JLabel title = Theme.title("Available Trips");
        JLabel sub   = Theme.muted("Browse and explore upcoming school trips");

        JPanel titleBox = new JPanel();
        titleBox.setBackground(Theme.BG_DARK);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(sub);

        header.add(titleBox, BorderLayout.WEST);

        if (currentUser.getRole().equals("STUDENT")) {
            JButton bookBtn = Theme.primaryBtn("+ Book a Seat");
            bookBtn.addActionListener(e -> mainFrame.showPanel("book"));
            header.add(bookBtn, BorderLayout.EAST);
        }

        add(header, BorderLayout.NORTH);

        // Trip cards grid
        JPanel grid = new JPanel();
        grid.setBackground(Theme.BG_DARK);
        grid.setLayout(new WrapLayout(FlowLayout.LEFT, 16, 16));
        grid.setBorder(new EmptyBorder(0, 20, 20, 20));

        List<Trip> trips = DataStore.getInstance().getAllTrips();
        for (Trip t : trips) {
            grid.add(buildTripCard(t));
        }

        if (trips.isEmpty()) {
            JLabel empty = Theme.muted("No trips found.");
            empty.setBorder(new EmptyBorder(40, 40, 40, 40));
            grid.add(empty);
        }

        add(Theme.scroll(grid), BorderLayout.CENTER);
    }

    private JPanel buildTripCard(Trip trip) {
        JPanel card = new JPanel();
        card.setBackground(Theme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(16, 18, 16, 18)));
        card.setPreferredSize(new Dimension(280, 230));
        card.setMaximumSize(new Dimension(280, 230));

        // Status badge
        Color sc = Theme.statusColor(trip.getStatus().name());
        JLabel badge = Theme.badge(trip.getStatus().name(), sc);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel(trip.getTripName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(Theme.TEXT_PRI);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dest = Theme.label("📍 " + trip.getDestination());
        dest.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dates = Theme.muted("📅 " + trip.getStartDate() + " → " + trip.getEndDate());
        dates.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel budget = Theme.label("💰 PKR " + String.format("%.0f", trip.getBudgetPerStudent()) + " / student");
        budget.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Participants progress
        int pct = trip.getParticipationLimit() > 0
                ? (int)(100.0 * trip.getCurrentParticipants() / trip.getParticipationLimit()) : 0;
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setForeground(pct >= 90 ? Theme.DANGER : pct >= 60 ? Theme.WARNING : Theme.ACCENT);
        bar.setBackground(Theme.BG_FIELD);
        bar.setBorderPainted(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel seats = Theme.muted(trip.getCurrentParticipants() + "/" + trip.getParticipationLimit() + " seats filled");
        seats.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel travel = Theme.muted("🚌 " + trip.getTravelMode() + "  |  " + trip.getDurationDays() + " days");
        travel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(badge);
        card.add(Box.createVerticalStrut(8));
        card.add(name);
        card.add(Box.createVerticalStrut(6));
        card.add(dest);
        card.add(Box.createVerticalStrut(3));
        card.add(dates);
        card.add(Box.createVerticalStrut(6));
        card.add(budget);
        card.add(Box.createVerticalStrut(10));
        card.add(bar);
        card.add(Box.createVerticalStrut(3));
        card.add(seats);
        card.add(Box.createVerticalStrut(6));
        card.add(travel);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.ACCENT, 1, true),
                        new EmptyBorder(16, 18, 16, 18)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                        new EmptyBorder(16, 18, 16, 18)));
            }
        });

        return card;
    }

    /** Simple wrap layout for card grid */
    static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int tw = target.getWidth();
                if (tw == 0) tw = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxw = tw - insets.left - insets.right - hgap * 2;
                int height = vgap, rowH = 0, rowW = 0;
                for (Component m : target.getComponents()) {
                    if (!m.isVisible()) continue;
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowW + d.width > maxw && rowW > 0) {
                        height += rowH + vgap;
                        rowH = 0; rowW = 0;
                    }
                    rowW += d.width + hgap;
                    rowH = Math.max(rowH, d.height);
                }
                height += rowH + vgap + insets.top + insets.bottom;
                return new Dimension(tw, height);
            }
        }
    }
}
