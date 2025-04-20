package main.java.com.myproject.gui.panel;

import main.java.com.myproject.model.Transaction;
import main.java.com.myproject.model.MonthlyExpense;
import main.java.com.myproject.service.BudgetService;
import main.java.com.myproject.service.TransactionService;
import main.java.com.myproject.gui.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyRecordsPanel extends JPanel {
    private JPanel chartPanel;
    private DefaultTableModel transactionsTableModel;
    private TransactionService transactionService;
    private BudgetService budgetService;
    private MainFrame mainFrame;
    private JLabel totalSpentValueLabel;
    private JLabel budgetValueLabel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DailyRecordsPanel(TransactionService transactionService, MainFrame mainFrame) {
        this.transactionService = transactionService;
        this.mainFrame = mainFrame;
        this.budgetService = new BudgetService();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        JPanel monthlyExpensePanel = createMonthlyExpenseModule();
        JPanel recentTransactionsPanel = createRecentTransactionsModule();
        JPanel budgetAllocationPanel = createBudgetAllocationModule();

        mainContainer.add(monthlyExpensePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        mainContainer.add(recentTransactionsPanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        mainContainer.add(budgetAllocationPanel);

        add(new JScrollPane(mainContainer), BorderLayout.CENTER);
    }

    private JPanel createMonthlyExpenseModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Monthly Expenses"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        panel.setPreferredSize(new Dimension(750, 300));

        chartPanel = new JPanel();
        chartPanel.setBackground(new Color(240, 240, 240));
        updateChart();

        panel.add(chartPanel, BorderLayout.CENTER);

        JButton importBillButton = new JButton("Import Bill");
        importBillButton.addActionListener(e -> showImportOptions());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(importBillButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRecentTransactionsModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.setPreferredSize(new Dimension(750, 200));

        String[] columnNames = {"Date", "Description", "Amount", "Category", "Type"};
        transactionsTableModel = new DefaultTableModel(columnNames, 0);
        JTable transactionsTable = new JTable(transactionsTableModel);

        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String typeValue = (String) table.getValueAt(row, 4);
                c.setForeground("Expense".equals(typeValue) ?
                        new Color(255, 99, 71) : new Color(46, 204, 113));

                return c;
            }
        });

        // Hide Type column
        transactionsTable.getColumnModel().getColumn(4).setMinWidth(0);
        transactionsTable.getColumnModel().getColumn(4).setMaxWidth(0);

        panel.add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBudgetAllocationModule() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Budget Allocation"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.setPreferredSize(new Dimension(750, 150));

        JPanel budgetInfoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        budgetInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel totalSpentLabel = new JLabel("Total Spent This Month:");
        totalSpentValueLabel = new JLabel("¥0.00");
        totalSpentValueLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel budgetLabel = new JLabel("Monthly Budget:");
        budgetValueLabel = new JLabel("¥0.00");
        budgetValueLabel.setFont(new Font("Arial", Font.BOLD, 14));

        budgetInfoPanel.add(totalSpentLabel);
        budgetInfoPanel.add(totalSpentValueLabel);
        budgetInfoPanel.add(budgetLabel);
        budgetInfoPanel.add(budgetValueLabel);

        panel.add(budgetInfoPanel, BorderLayout.CENTER);

        JButton setBudgetButton = new JButton("Set Budget");
        setBudgetButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter your monthly budget:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    budgetService.setMonthlyBudget(Double.parseDouble(input));
                    updateBudgetDisplay();
                } catch (NumberFormatException ex) { }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(setBudgetButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showImportOptions() {
        JDialog importDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Import Bill", true);
        importDialog.setLayout(new GridLayout(2, 1, 10, 10));
        importDialog.setSize(300, 150);
        importDialog.setLocationRelativeTo(this);

        JButton importFileButton = new JButton("Import CSV File");
        JButton manualEntryButton = new JButton("Manual Entry");

        importFileButton.addActionListener(e -> {
            importDialog.dispose();
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                transactionService.importTransactionsFromFile(fileChooser.getSelectedFile());
                updateAll();
            }
        });

        manualEntryButton.addActionListener(e -> {
            importDialog.dispose();
            showAddTransactionDialog();
        });

        importDialog.add(importFileButton);
        importDialog.add(manualEntryButton);
        importDialog.setVisible(true);
    }

    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add Transaction", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel(new CardLayout());
        JPanel tabPanel = new JPanel(new GridLayout(1, 2));

        JButton expenseButton = new JButton("Expense");
        JButton incomeButton = new JButton("Income");

        expenseButton.setBackground(new Color(0, 184, 148));
        expenseButton.setForeground(Color.WHITE);

        tabPanel.add(expenseButton);
        tabPanel.add(incomeButton);
        mainPanel.add(tabPanel, BorderLayout.NORTH);

        JPanel expensePanel = createExpensePanel(dialog);
        JPanel incomePanel = createIncomePanel(dialog);

        contentPanel.add(expensePanel, "Expense");
        contentPanel.add(incomePanel, "Income");

        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();

        expenseButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Expense");
            expenseButton.setBackground(new Color(0, 184, 148));
            expenseButton.setForeground(Color.WHITE);
            incomeButton.setBackground(new Color(240, 240, 240));
            incomeButton.setForeground(Color.BLACK);
        });

        incomeButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "Income");
            incomeButton.setBackground(new Color(0, 184, 148));
            incomeButton.setForeground(Color.WHITE);
            expenseButton.setBackground(new Color(240, 240, 240));
            expenseButton.setForeground(Color.BLACK);
        });

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createExpensePanel(JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel amountPanel = new JPanel(new BorderLayout());
        JLabel amountLabel = new JLabel("-¥", JLabel.RIGHT);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 24));
        amountLabel.setForeground(new Color(255, 99, 71));

        JTextField amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.BOLD, 24));

        amountPanel.add(amountLabel, BorderLayout.WEST);
        amountPanel.add(amountField, BorderLayout.CENTER);

        // Categories panel
        JPanel categoriesPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        categoriesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        String[] categoryNames = {"Rent", "Fitness", "Medical", "Shopping",
                "Training", "Travel", "Individual", "Other"};
        JTextField categoryField = new JTextField();

        for (String name : categoryNames) {
            JButton btn = new JButton(name);
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            btn.addActionListener(e -> categoryField.setText(name));
            categoriesPanel.add(btn);
        }

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField dateField = new JTextField(dateFormat.format(new Date()));
        JTextField descriptionField = new JTextField();
        categoryField.setEditable(false);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Selected Category:"));
        formPanel.add(categoryField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add Expense");

        cancelButton.addActionListener(e -> dialog.dispose());

        addButton.addActionListener(e -> {
            try {
                double amount = -Math.abs(Double.parseDouble(amountField.getText().trim()));
                transactionService.addManualTransaction(
                        dateField.getText().trim(),
                        descriptionField.getText().trim(),
                        amount,
                        categoryField.getText().trim(),
                        Transaction.TransactionType.EXPENSE
                );
                updateAll();
                dialog.dispose();
            } catch (Exception ex) { }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(categoriesPanel, BorderLayout.CENTER);
        centerPanel.add(formPanel, BorderLayout.SOUTH);

        panel.add(amountPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createIncomePanel(JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel amountPanel = new JPanel(new BorderLayout());
        JLabel amountLabel = new JLabel("+¥", JLabel.RIGHT);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 24));
        amountLabel.setForeground(new Color(46, 204, 113));

        JTextField amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.BOLD, 24));

        amountPanel.add(amountLabel, BorderLayout.WEST);
        amountPanel.add(amountField, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField dateField = new JTextField(dateFormat.format(new Date()));
        JTextField descriptionField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(
                new String[]{"Salary", "Investment", "Gift", "Other"});

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add Income");

        cancelButton.addActionListener(e -> dialog.dispose());

        addButton.addActionListener(e -> {
            try {
                double amount = Math.abs(Double.parseDouble(amountField.getText().trim()));
                transactionService.addManualTransaction(
                        dateField.getText().trim(),
                        descriptionField.getText().trim(),
                        amount,
                        (String) categoryCombo.getSelectedItem(),
                        Transaction.TransactionType.INCOME
                );
                updateAll();
                dialog.dispose();
            } catch (Exception ex) { }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        panel.add(amountPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateAll() {
        updateTransactionsTable();
        updateBudgetDisplay();
        updateChart();
        //mainFrame.updateAssetsPanel();

    }

    private void updateTransactionsTable() {
        transactionsTableModel.setRowCount(0);

        for (Transaction transaction : transactionService.getAllTransactions()) {
            transactionsTableModel.addRow(new Object[]{
                    dateFormat.format(transaction.getDate()),
                    transaction.getDescription(),
                    String.format("%s¥%.2f",
                            transaction.isExpense() ? "-" : "+",
                            Math.abs(transaction.getAmount())),
                    transaction.getCategory(),
                    transaction.isExpense() ? "Expense" : "Income"
            });
        }
    }

    private void updateBudgetDisplay() {
        Calendar cal = Calendar.getInstance();
        double totalSpent = 0;

        for (Transaction transaction : transactionService.getAllTransactions()) {
            Calendar transCal = Calendar.getInstance();
            transCal.setTime(transaction.getDate());

            if (transCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                    transCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                    transaction.isExpense()) {
                totalSpent += Math.abs(transaction.getAmount());
            }
        }

        double currentBudget = budgetService.getBudgetAmount();

        totalSpentValueLabel.setText(String.format("¥%.2f", totalSpent));
        budgetValueLabel.setText(String.format("¥%.2f", currentBudget));

        totalSpentValueLabel.setForeground(
                totalSpent > currentBudget && currentBudget > 0 ? Color.RED : Color.BLACK);
    }

    private void updateChart() {
        Calendar cal = Calendar.getInstance();
        List<MonthlyExpense> monthlyExpenses = transactionService.getMonthlyExpensesForYear(cal.get(Calendar.YEAR));

        chartPanel.removeAll();
        chartPanel.setLayout(new BorderLayout());

        JLabel chartTitle = new JLabel("Monthly Expenses - " + cal.get(Calendar.YEAR), JLabel.CENTER);
        chartTitle.setFont(new Font("Arial", Font.BOLD, 16));

        // Find max amount for scaling
        double maxAmount = 1000;
        for (MonthlyExpense expense : monthlyExpenses) {
            if (expense.getAmount() > maxAmount) maxAmount = expense.getAmount();
        }
        maxAmount = Math.ceil(maxAmount / 100) * 100;

        final double finalMaxAmount = maxAmount;
        JPanel graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth(), height = getHeight() - 40;
                int leftMargin = 60, bottomMargin = height;

                // Draw axes
                g2d.setColor(Color.BLACK);
                g2d.drawLine(leftMargin, 10, leftMargin, bottomMargin);
                g2d.drawLine(leftMargin, bottomMargin, width - 10, bottomMargin);

                // Draw points and lines
                String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int monthWidth = (width - leftMargin - 20) / 11;
                int[] xPoints = new int[12];
                int[] yPoints = new int[12];

                for (int i = 0; i < 12; i++) {
                    int x = leftMargin + i * monthWidth;
                    int y = bottomMargin - (int)(bottomMargin * monthlyExpenses.get(i).getAmount() / finalMaxAmount);

                    xPoints[i] = x;
                    yPoints[i] = y;

                    // Draw month name
                    g2d.drawString(monthNames[i], x - 10, bottomMargin + 15);

                    // Draw data point
                    g2d.setColor(new Color(0, 184, 148));
                    g2d.fillOval(x - 4, y - 4, 8, 8);

                    // Draw amount if non-zero
                    if (monthlyExpenses.get(i).getAmount() > 0) {
                        g2d.setColor(Color.BLACK);
                        g2d.drawString(String.format("¥%.0f", monthlyExpenses.get(i).getAmount()), x - 15, y - 10);
                    }
                }

                // Draw connecting lines
                g2d.setColor(new Color(0, 184, 148));
                g2d.setStroke(new BasicStroke(2f));
                for (int i = 0; i < 11; i++) {
                    if (monthlyExpenses.get(i).getAmount() > 0 || monthlyExpenses.get(i+1).getAmount() > 0) {
                        g2d.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
                    }
                }
            }
        };

        JPanel lineChartPanel = new JPanel(new BorderLayout());
        lineChartPanel.add(graphPanel, BorderLayout.CENTER);

        chartPanel.add(chartTitle, BorderLayout.NORTH);
        chartPanel.add(lineChartPanel, BorderLayout.CENTER);

        chartPanel.revalidate();
        chartPanel.repaint();
    }
}