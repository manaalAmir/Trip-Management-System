package tripms.util;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class Theme {
    // Palette - Modern navy + teal
    public static final Color BG_DARK    = new Color(15, 23, 42);
    public static final Color BG_CARD    = new Color(30, 41, 59);
    public static final Color BG_FIELD   = new Color(51, 65, 85);
    public static final Color ACCENT     = new Color(20, 184, 166);   // teal-500
    public static final Color ACCENT2    = new Color(56, 189, 248);   // sky-400
    public static final Color DANGER     = new Color(239, 68, 68);
    public static final Color SUCCESS    = new Color(34, 197, 94);
    public static final Color WARNING    = new Color(251, 191, 36);
    public static final Color TEXT_PRI   = new Color(241, 245, 249);
    public static final Color TEXT_SEC   = new Color(148, 163, 184);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color BORDER     = new Color(51, 65, 85);

    // Fonts
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEAD   = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUB    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 12);

    public static void applyGlobal() {
        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRI);
    }

    // ── Component factories ───────────────────────────────────────────────────

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT_PRI);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_HEAD);
        l.setForeground(TEXT_PRI);
        return l;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_SEC);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JTextField field() {
        JTextField f = new JTextField();
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRI);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    public static JPasswordField passField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRI);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    public static JTextArea textArea() {
        JTextArea a = new JTextArea();
        a.setBackground(BG_FIELD);
        a.setForeground(TEXT_PRI);
        a.setCaretColor(ACCENT);
        a.setFont(FONT_BODY);
        a.setBorder(new EmptyBorder(8, 10, 8, 10));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        return a;
    }

    public static <E> JComboBox<E> combo() {
        JComboBox<E> c = new JComboBox<>();
        c.setBackground(BG_FIELD);
        c.setForeground(TEXT_PRI);
        c.setFont(FONT_BODY);
        c.setBorder(new LineBorder(BORDER, 1, true));
        return c;
    }

    public static JButton primaryBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())
                    g2.setColor(ACCENT.darker());
                else if (getModel().isRollover())
                    g2.setColor(ACCENT.brighter());;
                if (!getModel().isRollover() && !getModel().isPressed())
                    g2.setColor(ACCENT);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleBtn(b, TEXT_PRI, ACCENT);
        return b;
    }

    public static JButton dangerBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? DANGER.darker() : DANGER);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleBtn(b, Color.WHITE, DANGER);
        return b;
    }

    public static JButton ghostBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setForeground(ACCENT);
        b.setBackground(BG_CARD);
        b.setBorder(new LineBorder(ACCENT, 1, true));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    private static void styleBtn(JButton b, Color fg, Color bg) {
        b.setFont(FONT_SUB);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setBackground(BG_CARD);
        return sp;
    }

    public static JTable styledTable(javax.swing.table.TableModel model) {
        JTable t = new JTable(model);
        t.setBackground(BG_CARD);
        t.setForeground(TEXT_PRI);
        t.setFont(FONT_BODY);
        t.setRowHeight(32);
        t.setGridColor(BORDER);
        t.setShowGrid(true);
        t.getTableHeader().setBackground(BG_DARK);
        t.getTableHeader().setForeground(ACCENT);
        t.getTableHeader().setFont(FONT_SUB);
        t.setSelectionBackground(new Color(20,184,166,80));
        t.setSelectionForeground(TEXT_PRI);
        t.setFillsViewportHeight(true);
        return t;
    }

    public static JSeparator separator() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setBackground(BORDER);
        return s;
    }

    /** Colour-coded status badge label */
    public static JLabel badge(String text, Color bg) {
        JLabel l = new JLabel(" " + text + " ");
        l.setFont(FONT_SMALL);
        l.setForeground(Color.WHITE);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(2,6,2,6));
        return l;
    }

    public static Color statusColor(String status) {
        switch (status.toUpperCase()) {
            case "OPEN":       return SUCCESS;
            case "SCHEDULED":  return ACCENT2;
            case "IN_PROGRESS":return WARNING;
            case "COMPLETED":  return TEXT_MUTED;
            case "CANCELLED":  return DANGER;
            default:           return TEXT_SEC;
        }
    }

    public static void styleFrame(JFrame f) {
        f.getContentPane().setBackground(BG_DARK);
    }
}
