package main.java.com.myproject.gui.panel;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {

    public ProfilePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a placeholder label
        JLabel placeholderLabel = new JLabel("Profile Panel Content Goes Here");
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 18));
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);

        add(placeholderLabel, BorderLayout.CENTER);
    }
}