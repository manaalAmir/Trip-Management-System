package tripms;

import tripms.gui.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel as base, then override with our dark theme
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
