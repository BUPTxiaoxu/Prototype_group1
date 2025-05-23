package main.java.com.myproject.gui.panel;

import main.java.com.myproject.service.BudgetService;
import main.java.com.myproject.service.StatisticService;
import main.java.com.myproject.service.TransactionService;
import main.java.com.myproject.gui.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 预算管理面板 - 用于设置总预算和分类预算
 */
public class BudgetPanel extends JPanel {
    // 服务
    private BudgetService budgetService;
    private TransactionService transactionService;
    private StatisticService statisticService;
    private MainFrame mainFrame;

    // UI组件
    private JTextField totalBudgetField;
    private JLabel totalBudgetValueLabel;
    private JLabel totalSpentValueLabel;
    private JLabel remainingValueLabel;
    private JProgressBar budgetProgressBar;

    // 分类预算表格
    private DefaultTableModel categoryBudgetTableModel;
    private JTable categoryBudgetTable;

    // 分类预算编辑组件
    private JComboBox<String> categoryComboBox;
    private JTextField categoryBudgetField;
    private JButton addCategoryBudgetButton;
    private JButton deleteCategoryBudgetButton;

    // 预算分类
    private static final String[] DEFAULT_EXPENSE_CATEGORIES = {
            "Rent", "Fitness", "Medical", "Shopping", "Training", "Travel", "Individual", "Other"
    };

    /**
     * 构造函数
     * @param transactionService 交易服务
     * @param budgetService 预算服务
     * @param mainFrame 主框架
     */
    public BudgetPanel(TransactionService transactionService, BudgetService budgetService, MainFrame mainFrame) {
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.mainFrame = mainFrame;
        this.statisticService = new StatisticService(transactionService, budgetService);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        // 创建各个模块
        JPanel titlePanel = createTitlePanel();
        JPanel monthlyBudgetPanel = createMonthlyBudgetPanel();
        JPanel categoryBudgetPanel = createCategoryBudgetPanel();

        // 添加各个模块
        mainContainer.add(titlePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContainer.add(monthlyBudgetPanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContainer.add(categoryBudgetPanel);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // 初始化显示数据
        updatePanel();
    }

    /**
     * 创建标题面板
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel titleSubPanel = new JPanel();
        titleSubPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel("Budget Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Set and manage your monthly budgets");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        titleSubPanel.add(titleLabel);
        titleSubPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titleSubPanel.add(subtitleLabel);

        panel.add(titleSubPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建月度总预算设置面板
     */
    private JPanel createMonthlyBudgetPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Monthly Budget"));

        // 北部 - 设置总预算
        JPanel setBudgetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel budgetLabel = new JLabel("Set Monthly Budget: ¥");
        budgetLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalBudgetField = new JTextField(10);
        totalBudgetField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton setBudgetButton = new JButton("Set Budget");
        setBudgetButton.setFont(new Font("Arial", Font.BOLD, 14));
        setBudgetButton.setBackground(new Color(52, 152, 219));
        setBudgetButton.setForeground(Color.WHITE);
        setBudgetButton.setFocusPainted(false);

        setBudgetButton.addActionListener(e -> {
            try {
                double budgetAmount = Double.parseDouble(totalBudgetField.getText().trim());
                if (budgetAmount <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a positive budget amount",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 设置预算
                budgetService.setMonthlyBudget(budgetAmount);

                // 更新显示
                updatePanel();

                // 更新依赖面板
                if (mainFrame != null) {
                    mainFrame.updateDependentPanels();
                }

                JOptionPane.showMessageDialog(this,
                        "Monthly budget set to ¥" + String.format("%.2f", budgetAmount),
                        "Budget Updated",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setBudgetPanel.add(budgetLabel);
        setBudgetPanel.add(totalBudgetField);
        setBudgetPanel.add(setBudgetButton);

        // 中部 - 预算信息
        JPanel budgetInfoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        budgetInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel budgetTitleLabel = new JLabel("Current Monthly Budget:");
        budgetTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalBudgetValueLabel = new JLabel("¥0.00");
        totalBudgetValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalBudgetValueLabel.setForeground(new Color(52, 152, 219));

        JLabel spentLabel = new JLabel("Total Spent This Month:");
        spentLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalSpentValueLabel = new JLabel("¥0.00");
        totalSpentValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalSpentValueLabel.setForeground(new Color(231, 76, 60));

        JLabel remainingLabel = new JLabel("Remaining Budget:");
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 14));

        remainingValueLabel = new JLabel("¥0.00");
        remainingValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        remainingValueLabel.setForeground(new Color(46, 204, 113));

        budgetInfoPanel.add(budgetTitleLabel);
        budgetInfoPanel.add(totalBudgetValueLabel);
        budgetInfoPanel.add(spentLabel);
        budgetInfoPanel.add(totalSpentValueLabel);
        budgetInfoPanel.add(remainingLabel);
        budgetInfoPanel.add(remainingValueLabel);

        // 南部 - 进度条
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        budgetProgressBar = new JProgressBar(0, 100);
        budgetProgressBar.setStringPainted(true);
        budgetProgressBar.setPreferredSize(new Dimension(600, 25));
        budgetProgressBar.setFont(new Font("Arial", Font.BOLD, 12));

        progressPanel.add(new JLabel("Budget Usage:"), BorderLayout.NORTH);
        progressPanel.add(budgetProgressBar, BorderLayout.CENTER);

        // 组合面板
        panel.add(setBudgetPanel, BorderLayout.NORTH);
        panel.add(budgetInfoPanel, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 创建分类预算设置面板
     */
    private JPanel createCategoryBudgetPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Category Budgets"));

        // 北部 - 添加分类预算
        JPanel addCategoryPanel = new JPanel(new GridBagLayout());
        addCategoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 标签样式
        Font labelFont = new Font("Arial", Font.BOLD, 14);

        // 分类标签和下拉框
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        addCategoryPanel.add(categoryLabel, gbc);

        categoryComboBox = new JComboBox<>(DEFAULT_EXPENSE_CATEGORIES);
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryComboBox.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        addCategoryPanel.add(categoryComboBox, gbc);

        // 金额标签和输入框
        JLabel amountLabel = new JLabel("Budget Amount: ¥");
        amountLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        addCategoryPanel.add(amountLabel, gbc);

        categoryBudgetField = new JTextField(8);
        categoryBudgetField.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryBudgetField.setPreferredSize(new Dimension(100, 30));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        addCategoryPanel.add(categoryBudgetField, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // 添加/更新按钮 - 改进样式使其更明显
        addCategoryBudgetButton = new JButton("Add/Update");
        addCategoryBudgetButton.setFont(new Font("Arial", Font.BOLD, 14));
        addCategoryBudgetButton.setBackground(new Color(46, 204, 113)); // 更明亮的绿色
        addCategoryBudgetButton.setForeground(Color.WHITE);
        addCategoryBudgetButton.setFocusPainted(false);
        addCategoryBudgetButton.setBorderPainted(true);
        addCategoryBudgetButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(39, 174, 96), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        addCategoryBudgetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 删除按钮 - 改进样式使其更明显
        deleteCategoryBudgetButton = new JButton("Delete");
        deleteCategoryBudgetButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteCategoryBudgetButton.setBackground(new Color(231, 76, 60)); // 更明亮的红色
        deleteCategoryBudgetButton.setForeground(Color.WHITE);
        deleteCategoryBudgetButton.setFocusPainted(false);
        deleteCategoryBudgetButton.setBorderPainted(true);
        deleteCategoryBudgetButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(192, 57, 43), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        deleteCategoryBudgetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(addCategoryBudgetButton);
        buttonPanel.add(deleteCategoryBudgetButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        addCategoryPanel.add(buttonPanel, gbc);

        // 添加/更新分类预算
        addCategoryBudgetButton.addActionListener(e -> {
            try {
                String category = (String) categoryComboBox.getSelectedItem();
                double amount = Double.parseDouble(categoryBudgetField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a positive budget amount",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 保存分类预算
                budgetService.setCategoryBudget(category, amount);

                // 更新表格
                updateCategoryBudgetTable();

                categoryBudgetField.setText("");
                JOptionPane.showMessageDialog(this,
                        "Budget for " + category + " set to ¥" + String.format("%.2f", amount),
                        "Category Budget Updated",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // 删除分类预算
        deleteCategoryBudgetButton.addActionListener(e -> {
            String category = (String) categoryComboBox.getSelectedItem();

            // 检查是否存在此分类预算
            if (!budgetService.hasCategoryBudget(category)) {
                JOptionPane.showMessageDialog(this,
                        "No budget set for " + category,
                        "No Budget Found",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 删除分类预算
            budgetService.removeCategoryBudget(category);

            // 更新表格
            updateCategoryBudgetTable();

            categoryBudgetField.setText("");
            JOptionPane.showMessageDialog(this,
                    "Budget for " + category + " has been removed",
                    "Category Budget Removed",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // 中部 - 分类预算表格
        String[] columnNames = {"Category", "Budget", "Spent", "Remaining", "Usage %"};
        categoryBudgetTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 不可编辑
            }
        };

        categoryBudgetTable = new JTable(categoryBudgetTableModel);
        categoryBudgetTable.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryBudgetTable.setRowHeight(25);

        // 自定义单元格渲染器
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (column == 2) { // Spent 列使用红色
                    c.setForeground(new Color(231, 76, 60));
                } else if (column == 3) { // Remaining 列
                    // 如果剩余为负，使用红色；否则使用绿色
                    String remainingStr = (String)value;
                    if (remainingStr.startsWith("-")) {
                        c.setForeground(new Color(231, 76, 60));
                    } else {
                        c.setForeground(new Color(46, 204, 113));
                    }
                } else if (column == 4) { // Usage % 列
                    String usageStr = (String)value;
                    try {
                        float usage = Float.parseFloat(usageStr.replace("%", ""));
                        if (usage >= 100) {
                            c.setForeground(new Color(231, 76, 60)); // 红色
                        } else if (usage >= 80) {
                            c.setForeground(new Color(230, 126, 34)); // 橙色
                        } else {
                            c.setForeground(new Color(46, 204, 113)); // 绿色
                        }
                    } catch (NumberFormatException e) {
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }

                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        };

        // 设置各列渲染器
        categoryBudgetTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
        for (int i = 1; i < categoryBudgetTable.getColumnCount(); i++) {
            categoryBudgetTable.getColumnModel().getColumn(i).setCellRenderer(colorRenderer);
        }

        // 设置列宽
        categoryBudgetTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        categoryBudgetTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        categoryBudgetTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        categoryBudgetTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        categoryBudgetTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        // 为表格添加标题和边框
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel tableTitle = new JLabel("Category Budget Summary", JLabel.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(categoryBudgetTable), BorderLayout.CENTER);

        // 分隔线
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(200, 200, 200));

        // 组合面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(addCategoryPanel, BorderLayout.CENTER);
        topPanel.add(separator, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 更新分类预算表格
     */
    private void updateCategoryBudgetTable() {
        // 清空表格
        categoryBudgetTableModel.setRowCount(0);

        // 获取当前月份统计
        StatisticService.CategoryStatistics stats = statisticService.getCurrentMonthStatistics();
        Map<String, Double> expenseByCategory = stats.getExpenseByCategory();

        // 获取所有分类预算
        Map<String, Double> categoryBudgets = budgetService.getAllCategoryBudgets();

        // 填充表格数据
        for (Map.Entry<String, Double> entry : categoryBudgets.entrySet()) {
            String category = entry.getKey();
            double budgetAmount = entry.getValue();
            double spent = expenseByCategory.getOrDefault(category, 0.0);
            double remaining = budgetAmount - spent;
            double usagePercent = budgetAmount > 0 ? (spent / budgetAmount) * 100 : 0;

            Object[] rowData = {
                    category,
                    String.format("¥%.2f", budgetAmount),
                    String.format("¥%.2f", spent),
                    String.format("%s¥%.2f", remaining < 0 ? "-" : "", Math.abs(remaining)),
                    String.format("%.1f%%", usagePercent)
            };

            categoryBudgetTableModel.addRow(rowData);
        }
    }

    /**
     * 更新总预算显示
     */
    private void updateTotalBudgetDisplay() {
        // 获取当前月份统计
        StatisticService.CategoryStatistics stats = statisticService.getCurrentMonthStatistics();

        // 更新预算金额
        double budget = stats.getBudget();
        totalBudgetValueLabel.setText(String.format("¥%.2f", budget));

        // 更新已消费金额
        double spent = stats.getTotalExpenses();
        totalSpentValueLabel.setText(String.format("¥%.2f", spent));

        // 更新剩余预算
        double remaining = stats.getRemaining();
        remainingValueLabel.setText(String.format("¥%.2f", remaining));

        // 设置剩余预算颜色
        if (remaining < 0) {
            remainingValueLabel.setForeground(new Color(231, 76, 60)); // 红色
        } else if (budget > 0 && remaining < budget * 0.2) {
            remainingValueLabel.setForeground(new Color(230, 126, 34)); // 橙色
        } else {
            remainingValueLabel.setForeground(new Color(46, 204, 113)); // 绿色
        }

        // 更新进度条
        if (budget > 0) {
            int percentUsed = (int)((spent / budget) * 100);
            budgetProgressBar.setValue(Math.min(percentUsed, 100));

            if (percentUsed >= 100) {
                budgetProgressBar.setForeground(new Color(231, 76, 60)); // 红色
                budgetProgressBar.setString(String.format("%.1f%% (Over Budget)", percentUsed));
            } else if (percentUsed >= 80) {
                budgetProgressBar.setForeground(new Color(230, 126, 34)); // 橙色
                budgetProgressBar.setString(String.format("%.1f%%", percentUsed));
            } else {
                budgetProgressBar.setForeground(new Color(46, 204, 113)); // 绿色
                budgetProgressBar.setString(String.format("%.1f%%", percentUsed));
            }
        } else {
            budgetProgressBar.setValue(0);
            budgetProgressBar.setString("No Budget Set");
        }
    }

    /**
     * 更新面板数据显示
     */
    public void updatePanel() {
        // 更新预算输入框提示值
        double currentBudget = budgetService.getBudgetAmount();
        if (currentBudget > 0) {
            totalBudgetField.setText(String.format("%.2f", currentBudget));
        } else {
            totalBudgetField.setText("");
        }

        // 更新总预算显示
        updateTotalBudgetDisplay();

        // 更新分类预算表格
        updateCategoryBudgetTable();

        // 更新分类选择下拉框选项
        updateCategoryComboBoxSelection();
    }

    /**
     * 更新分类选择下拉框
     */
    private void updateCategoryComboBoxSelection() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        if (selectedCategory != null) {
            // 如果该分类已有预算，显示当前值
            if (budgetService.hasCategoryBudget(selectedCategory)) {
                double amount = budgetService.getCategoryBudget(selectedCategory);
                categoryBudgetField.setText(String.format("%.2f", amount));
            } else {
                categoryBudgetField.setText("");
            }
        }

        // 添加选择变更监听
        categoryComboBox.addActionListener(e -> {
            String category = (String) categoryComboBox.getSelectedItem();
            if (category != null && budgetService.hasCategoryBudget(category)) {
                double amount = budgetService.getCategoryBudget(category);
                categoryBudgetField.setText(String.format("%.2f", amount));
            } else {
                categoryBudgetField.setText("");
            }
        });
    }
}