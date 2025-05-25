package main.java.com.myproject.gui;

import main.java.com.myproject.gui.panel.*;
import main.java.com.myproject.service.BudgetService;
import main.java.com.myproject.service.TransactionService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panel constants
    private static final String DAILY_RECORDS_PANEL = "DailyRecordsPanel";
    private static final String ASSETS_PANEL = "AssetsPanel";
    private static final String STATISTIC_PANEL = "StatisticPanel";
    private static final String AI_PANEL = "AIPanel";
    private static final String PROFILE_PANEL = "ProfilePanel";
    private static final String BUDGET_PANEL = "BudgetPanel"; // 新增预算面板常量

    // Panel references
    private DailyRecordsPanel dailyRecordsPanel;
    private AssetsPanel assetsPanel;
    private StatisticPanel statisticPanel;
    private AIPanel aiPanel;
    private BudgetPanel budgetPanel; // 新增预算面板引用

    // Shared services - 确保这些是共享实例
    private TransactionService transactionService;
    private BudgetService budgetService;

    public MainFrame() {
        setTitle("Finance Manager");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create shared services - 注意：这些是单例实例
        transactionService = new TransactionService();
        budgetService = new BudgetService();

        // 调试输出
        System.out.println("MainFrame: Created BudgetService instance: " + budgetService.hashCode());

        initializeUI();
    }

    private void initializeUI() {
        contentPanel = new JPanel(new BorderLayout());

        // Create the navigation panel (left side)
        JPanel navigationPanel = createNavigationPanel();
        contentPanel.add(navigationPanel, BorderLayout.WEST);

        // Create the main content panel (right side)
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Create panels with shared services - 传递相同的实例
        dailyRecordsPanel = new DailyRecordsPanel(transactionService, this);
        assetsPanel = new AssetsPanel(transactionService);
        statisticPanel = new StatisticPanel(transactionService, budgetService);
        aiPanel = new AIPanel(transactionService, budgetService);
        budgetPanel = new BudgetPanel(transactionService, budgetService, this); // 创建预算面板

        // 调试输出
        System.out.println("AIPanel budgetService hash: " + budgetService.hashCode());

        // Add the various panels
        mainPanel.add(dailyRecordsPanel, DAILY_RECORDS_PANEL);
        mainPanel.add(assetsPanel, ASSETS_PANEL);
        mainPanel.add(statisticPanel, STATISTIC_PANEL);
        mainPanel.add(aiPanel, AI_PANEL);
        mainPanel.add(budgetPanel, BUDGET_PANEL); // 添加预算面板
        mainPanel.add(new ProfilePanel(), PROFILE_PANEL);

        contentPanel.add(mainPanel, BorderLayout.CENTER);

        // Show the first panel by default
        cardLayout.show(mainPanel, DAILY_RECORDS_PANEL);

        setContentPane(contentPanel);
    }

    public void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);

        // Update panels if needed
        if (ASSETS_PANEL.equals(panelName)) {
            assetsPanel.updatePanel();
        } else if (STATISTIC_PANEL.equals(panelName)) {
            statisticPanel.updatePanel();
        } else if (AI_PANEL.equals(panelName)) {
            aiPanel.updatePanel();  // Update AI panel when switching to it
        } else if (BUDGET_PANEL.equals(panelName)) {
            budgetPanel.updatePanel(); // 更新预算面板
        }
    }

    public void updateDependentPanels() {
        // 调试输出
        System.out.println("MainFrame.updateDependentPanels() called");
        System.out.println("Current budget in MainFrame: ¥" + budgetService.getBudgetAmount());

        // Update all panels that depend on transaction or budget data
        if (assetsPanel != null) {
            assetsPanel.updatePanel();
        }
        if (statisticPanel != null) {
            statisticPanel.updatePanel();
        }
        if (aiPanel != null) {
            // 强制刷新 AI 面板
            System.out.println("Updating AI panel...");
            aiPanel.updatePanel();

            // 额外的刷新以确保显示正确
            SwingUtilities.invokeLater(() -> {
                aiPanel.revalidate();
                aiPanel.repaint();
            });
        }
    }

    // 添加一个方法来获取 budgetService（供 DailyRecordsPanel 使用）
    public BudgetService getBudgetService() {
        return budgetService;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(50, 50, 50));
        navPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Add navigation buttons - all in English now
        addNavButton(navPanel, "Daily Records", DAILY_RECORDS_PANEL);
        addNavButton(navPanel, "Budget", BUDGET_PANEL); // 添加预算按钮
        addNavButton(navPanel, "Assets", ASSETS_PANEL);
        addNavButton(navPanel, "Statistics", STATISTIC_PANEL);
        addNavButton(navPanel, "AI Assistant", AI_PANEL);
        addNavButton(navPanel, "Profile", PROFILE_PANEL);

        // Add some padding at the bottom
        navPanel.add(Box.createVerticalGlue());

        return navPanel;
    }

    private void addNavButton(JPanel panel, String text, String panelName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFocusPainted(false);

        // Style the button
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        button.addActionListener(e -> switchPanel(panelName));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(button);
    }

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}