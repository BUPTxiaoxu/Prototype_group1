//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package main.java.com.myproject.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import main.java.com.myproject.gui.panel.AIPanel;
import main.java.com.myproject.gui.panel.AssetsPanel;
import main.java.com.myproject.gui.panel.DailyRecordsPanel;
import main.java.com.myproject.gui.panel.ProfilePanel;
import main.java.com.myproject.gui.panel.StatisticPanel;
import main.java.com.myproject.service.BudgetService;
import main.java.com.myproject.service.TransactionService;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private JPanel mainPanel;

    private CardLayout cardLayout;
    private static final String DAILY_RECORDS_PANEL = "DailyRecordsPanel";
    private static final String ASSETS_PANEL = "AssetsPanel";
    private static final String STATISTIC_PANEL = "StatisticPanel";
    private static final String AI_PANEL = "AIPanel";
    private static final String PROFILE_PANEL = "ProfilePanel";
    private DailyRecordsPanel dailyRecordsPanel;
    private AssetsPanel assetsPanel;
    private StatisticPanel statisticPanel;
    private TransactionService transactionService;
    private BudgetService budgetService;

    public MainFrame() {
        this.setTitle("My Application");
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo((Component)null);
        this.transactionService = new TransactionService();
        this.budgetService = new BudgetService();
        this.initializeUI();
    }

    private void initializeUI() {
        this.contentPanel = new JPanel(new BorderLayout());
        JPanel navigationPanel = this.createNavigationPanel();
        this.contentPanel.add(navigationPanel, "West");
        this.mainPanel = new JPanel();
        this.cardLayout = new CardLayout();
        this.mainPanel.setLayout(this.cardLayout);
        this.dailyRecordsPanel = new DailyRecordsPanel(this.transactionService, this);
        this.assetsPanel = new AssetsPanel(this.transactionService);
        this.statisticPanel = new StatisticPanel(this.transactionService, this.budgetService);
        this.mainPanel.add(this.dailyRecordsPanel, "DailyRecordsPanel");
        this.mainPanel.add(this.assetsPanel, "AssetsPanel");
        this.mainPanel.add(this.statisticPanel, "StatisticPanel");
        this.mainPanel.add(new AIPanel(), "AIPanel");
        this.mainPanel.add(new ProfilePanel(), "ProfilePanel");
        this.contentPanel.add(this.mainPanel, "Center");
        this.cardLayout.show(this.mainPanel, "DailyRecordsPanel");
        this.setContentPane(this.contentPanel);
    }

    public void switchPanel(String panelName) {
        this.cardLayout.show(this.mainPanel, panelName);
        if ("AssetsPanel".equals(panelName)) {
            this.assetsPanel.updatePanel();
        } else if ("StatisticPanel".equals(panelName)) {
            this.statisticPanel.updatePanel();
        }

    }

    public void updateDependentPanels() {
        if (this.assetsPanel != null) {
            this.assetsPanel.updatePanel();
        }

        if (this.statisticPanel != null) {
            this.statisticPanel.updatePanel();
        }

    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, 1));
        navPanel.setBackground(new Color(50, 50, 50));
        navPanel.setPreferredSize(new Dimension(200, this.getHeight()));
        this.addNavButton(navPanel, "Daily Records", "DailyRecordsPanel");
        this.addNavButton(navPanel, "Assets", "AssetsPanel");
        this.addNavButton(navPanel, "Statistics", "StatisticPanel");
        this.addNavButton(navPanel, "AI", "AIPanel");
        this.addNavButton(navPanel, "Profile", "ProfilePanel");
        navPanel.add(Box.createVerticalGlue());
        return navPanel;
    }

    private void addNavButton(JPanel panel, String text, String panelName) {
        JButton button = new JButton(text);
        button.setAlignmentX(0.5F);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", 0, 14));
        button.addActionListener((e) -> {
            this.switchPanel(panelName);
        });
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(button);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception var2) {
            Exception e = var2;
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
