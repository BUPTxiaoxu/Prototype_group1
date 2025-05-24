package main.java.com.myproject.gui.panel;

import main.java.com.myproject.model.Transaction;
import main.java.com.myproject.service.AIService;
import main.java.com.myproject.service.BudgetService;
import main.java.com.myproject.service.StatisticService;
import main.java.com.myproject.service.TransactionService;
import main.java.com.myproject.util.StreamingUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * AI面板 - 显示AI财务建议和智能分类
 */
public class AIPanel extends JPanel {
    private AIService aiService;
    private TransactionService transactionService;
    private JTextArea adviceArea;
    private JButton getAdviceButton;
    private JButton classifyButton;
    private JLabel statusLabel;
    private BudgetService budgetService;
    private JLabel currentBudgetLabel;  // 预算显示标签

    // AI分类相关组件
    private JTable classificationTable;
    private DefaultTableModel classificationTableModel;
    private JTextArea classificationResultArea;
    private JProgressBar classificationProgress;

    /**
     * 默认构造函数，向后兼容
     */
    public AIPanel() {
        // Empty constructor for backwards compatibility
        setLayout(new BorderLayout());
        addPlaceholder();
    }

    /**
     * 主构造函数
     * @param transactionService 交易服务
     * @param budgetService 预算服务
     */
    public AIPanel(TransactionService transactionService, BudgetService budgetService) {
        this.transactionService = transactionService;
        StatisticService statisticService = new StatisticService(transactionService, budgetService);
        this.aiService = new AIService(transactionService, budgetService, statisticService);
        this.budgetService = budgetService;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initializeComponents();
    }

    /**
     * 添加占位符
     */
    private void addPlaceholder() {
        JLabel placeholderLabel = new JLabel("AI features require account services to function properly", JLabel.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(placeholderLabel, BorderLayout.CENTER);
    }

    /**
     * 初始化所有组件
     */
    private void initializeComponents() {
        // Create tabbed pane for multiple AI features
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Financial Advice Tab
        JPanel adviceTab = createAdviceTab();
        tabbedPane.addTab("Financial Advice", adviceTab);

        // AI Classification Tab
        JPanel classificationTab = createClassificationTab();
        tabbedPane.addTab("AI Classification", classificationTab);

        add(tabbedPane, BorderLayout.CENTER);

        // 在所有组件都初始化完成后，更新预算显示
        updateBudgetDisplay();
    }

    /**
     * 创建财务建议标签页
     */
    private JPanel createAdviceTab() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        // Title panel
        JPanel titlePanel = createTitlePanel();

        // Control panel
        JPanel controlPanel = createControlPanel();

        // Advice display panel
        JPanel advicePanel = createAdvicePanel();

        // Add all panels
        mainContainer.add(titlePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContainer.add(controlPanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContainer.add(advicePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.add(scrollPane, BorderLayout.CENTER);

        return tabPanel;
    }

    /**
     * 创建AI分类标签页
     */
    private JPanel createClassificationTab() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("AI Transaction Classification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Automatically classify your transactions using AI");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        classifyButton = new JButton("Classify This Month's Transactions");
        classifyButton.setFont(new Font("Arial", Font.BOLD, 16));
        classifyButton.setPreferredSize(new Dimension(300, 50));
        classifyButton.setBackground(new Color(155, 89, 182));
        classifyButton.setForeground(Color.WHITE);
        classifyButton.setFocusPainted(false);
        classifyButton.setBorderPainted(false);
        classifyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        classifyButton.addActionListener(e -> performAIClassification());

        classificationProgress = new JProgressBar();
        classificationProgress.setPreferredSize(new Dimension(300, 25));
        classificationProgress.setStringPainted(true);
        classificationProgress.setVisible(false);

        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.Y_AXIS));
        buttonWrapper.add(classifyButton);
        buttonWrapper.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonWrapper.add(classificationProgress);

        controlPanel.add(buttonWrapper);

        // Results panel with split pane
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        resultsPanel.setPreferredSize(new Dimension(800, 400));

        // Split pane for results
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        // Left panel - Classification table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Classification Results"));

        String[] columnNames = {"Category", "Amount Spent", "Transaction Count", "Percentage"};
        classificationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        classificationTable = new JTable(classificationTableModel);
        classificationTable.setFont(new Font("Arial", Font.PLAIN, 12));
        classificationTable.setRowHeight(25);

        // Custom cell renderer for amounts
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (column == 1) { // Amount column
                    setForeground(new Color(231, 76, 60));
                } else if (column == 3) { // Percentage column
                    setForeground(new Color(52, 152, 219));
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        };

        classificationTable.getColumnModel().getColumn(1).setCellRenderer(amountRenderer);
        classificationTable.getColumnModel().getColumn(2).setCellRenderer(amountRenderer);
        classificationTable.getColumnModel().getColumn(3).setCellRenderer(amountRenderer);

        JScrollPane tableScrollPane = new JScrollPane(classificationTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Right panel - Detailed results
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Classification Details"));

        classificationResultArea = new JTextArea();
        classificationResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        classificationResultArea.setEditable(false);
        classificationResultArea.setLineWrap(true);
        classificationResultArea.setWrapStyleWord(true);
        classificationResultArea.setText("Click 'Classify This Month's Transactions' to see AI classification results.");

        JScrollPane detailScrollPane = new JScrollPane(classificationResultArea);
        detailPanel.add(detailScrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(tablePanel);
        splitPane.setRightComponent(detailPanel);

        resultsPanel.add(splitPane, BorderLayout.CENTER);

        // Add all components to main container
        mainContainer.add(titlePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContainer.add(controlPanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContainer.add(resultsPanel);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.add(scrollPane, BorderLayout.CENTER);

        return tabPanel;
    }

    /**
     * 执行AI分类
     */
    private void performAIClassification() {
        classifyButton.setEnabled(false);
        classificationProgress.setVisible(true);
        classificationProgress.setIndeterminate(true);
        classificationProgress.setString("Analyzing transactions...");

        // Clear previous results
        classificationTableModel.setRowCount(0);
        classificationResultArea.setText("Processing...\n\n");

        SwingWorker<Map<String, List<Transaction>>, String> worker = new SwingWorker<>() {
            @Override
            protected Map<String, List<Transaction>> doInBackground() throws Exception {
                // Get current month transactions
                Calendar cal = Calendar.getInstance();
                int currentYear = cal.get(Calendar.YEAR);
                int currentMonth = cal.get(Calendar.MONTH);

                List<Transaction> allTransactions = transactionService.getAllTransactions();
                Map<String, List<Transaction>> classifiedTransactions = new HashMap<>();

                // Initialize categories
                String[] categories = {"Rent", "Fitness", "Medical", "Shopping",
                        "Training", "Travel", "Individual", "Other"};
                for (String category : categories) {
                    classifiedTransactions.put(category, new ArrayList<>());
                }

                int totalTransactions = 0;
                int processedTransactions = 0;

                // Count total transactions for this month
                for (Transaction transaction : allTransactions) {
                    cal.setTime(transaction.getDate());
                    if (cal.get(Calendar.YEAR) == currentYear &&
                            cal.get(Calendar.MONTH) == currentMonth &&
                            transaction.isExpense()) {
                        totalTransactions++;
                    }
                }

                // Process each transaction
                for (Transaction transaction : allTransactions) {
                    cal.setTime(transaction.getDate());
                    if (cal.get(Calendar.YEAR) == currentYear &&
                            cal.get(Calendar.MONTH) == currentMonth &&
                            transaction.isExpense()) {

                        // Use AI to classify based on description ONLY
                        String category = classifyTransactionUsingAI(transaction.getDescription());

                        List<Transaction> categoryList = classifiedTransactions.get(category);
                        if (categoryList == null) {
                            categoryList = new ArrayList<>();
                            classifiedTransactions.put(category, categoryList);
                        }
                        categoryList.add(transaction);

                        processedTransactions++;
                        int progress = (int)((processedTransactions / (double)totalTransactions) * 100);
                        publish("Processing: " + progress + "%");
                    }
                }

                return classifiedTransactions;
            }

            @Override
            protected void process(List<String> chunks) {
                String latest = chunks.get(chunks.size() - 1);
                classificationProgress.setString(latest);
            }

            @Override
            protected void done() {
                try {
                    Map<String, List<Transaction>> results = get();
                    displayClassificationResults(results);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AIPanel.this,
                            "Error during classification: " + e.getMessage(),
                            "Classification Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    classifyButton.setEnabled(true);
                    classificationProgress.setVisible(false);
                }
            }
        };

        worker.execute();
    }

    private String classifyTransactionUsingAI(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "Other";
        }

        try {
            // Build prompt for AI classification
            String prompt = "Please classify the following transaction description into one of these categories: " +
                    "Rent, Fitness, Medical, Shopping, Training, Travel, Individual, Other.\n\n" +
                    "Transaction description: \"" + description + "\"\n\n" +
                    "Reply with ONLY the category name, nothing else.";

            // Call AI service for classification
            String aiResponse = aiService.processQuery(prompt);

            // Parse AI response
            if (aiResponse != null && !aiResponse.isEmpty()) {
                String category = aiResponse.trim();
                // Validate category
                if (isValidCategory(category)) {
                    return category;
                }
            }

            // If AI fails to return a valid category, default to "Other"
            return "Other";

        } catch (Exception e) {
            // If AI service fails, default to "Other"
            System.err.println("AI classification failed: " + e.getMessage());
            return "Other";
        }
    }

    /**
     * Validate if the category is one of the predefined categories
     */
    private boolean isValidCategory(String category) {
        String[] validCategories = {"Rent", "Fitness", "Medical", "Shopping",
                "Training", "Travel", "Individual", "Other"};
        for (String valid : validCategories) {
            if (valid.equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 显示分类结果
     */
    private void displayClassificationResults(Map<String, List<Transaction>> classifiedTransactions) {
        double totalAmount = 0;
        StringBuilder detailsBuilder = new StringBuilder();
        detailsBuilder.append("═══════════════════════════════════════════════════\n");
        detailsBuilder.append("           AI CLASSIFICATION RESULTS               \n");
        detailsBuilder.append("═══════════════════════════════════════════════════\n\n");

        // Calculate total amount
        for (Map.Entry<String, List<Transaction>> entry : classifiedTransactions.entrySet()) {
            for (Transaction t : entry.getValue()) {
                totalAmount += Math.abs(t.getAmount());
            }
        }

        // Sort categories by amount
        List<Map.Entry<String, List<Transaction>>> sortedEntries = new ArrayList<>(classifiedTransactions.entrySet());
        sortedEntries.sort((a, b) -> {
            double amountA = a.getValue().stream().mapToDouble(t -> Math.abs(t.getAmount())).sum();
            double amountB = b.getValue().stream().mapToDouble(t -> Math.abs(t.getAmount())).sum();
            return Double.compare(amountB, amountA);
        });

        // Display results
        int categoryIndex = 1;
        for (Map.Entry<String, List<Transaction>> entry : sortedEntries) {
            String category = entry.getKey();
            List<Transaction> transactions = entry.getValue();

            if (!transactions.isEmpty()) {
                double categoryAmount = transactions.stream()
                        .mapToDouble(t -> Math.abs(t.getAmount()))
                        .sum();
                double percentage = (categoryAmount / totalAmount) * 100;

                // Add to table
                Object[] rowData = {
                        category,
                        String.format("¥%.2f", categoryAmount),
                        transactions.size(),
                        String.format("%.1f%%", percentage)
                };
                classificationTableModel.addRow(rowData);

                // Add to details with improved formatting
                detailsBuilder.append(String.format("【%d. %s】\n", categoryIndex++, category));
                detailsBuilder.append("───────────────────────────────────────────────────\n");
                detailsBuilder.append(String.format("   Total Amount: ¥%.2f (%.1f%%)\n", categoryAmount, percentage));
                detailsBuilder.append(String.format("   Transactions: %d\n", transactions.size()));
                detailsBuilder.append("───────────────────────────────────────────────────\n");
                detailsBuilder.append("   Top Transactions:\n");

                // List top 5 transactions
                transactions.sort((a, b) -> Double.compare(Math.abs(b.getAmount()), Math.abs(a.getAmount())));
                int count = 0;
                for (Transaction t : transactions) {
                    if (count >= 5) {
                        detailsBuilder.append(String.format("   ... and %d more transactions\n",
                                transactions.size() - 5));
                        break;
                    }
                    count++;
                    String description = t.getDescription();

                    // 确保描述长度一致，为金额预留足够空间
                    int maxDescLength = 25; // 减少描述长度，为金额预留更多空间
                    if (description.length() > maxDescLength) {
                        description = description.substring(0, maxDescLength - 3) + "...";
                    }

                    // 使用固定宽度格式，确保金额始终在同一列对齐
                    detailsBuilder.append(String.format("   %d) %-28s %8s\n",
                            count, description, String.format("¥%.2f", Math.abs(t.getAmount()))));
                }
                detailsBuilder.append("\n");
            }
        }

        // Add summary section
        detailsBuilder.append("═══════════════════════════════════════════════════\n");
        detailsBuilder.append("                    SUMMARY                        \n");
        detailsBuilder.append("═══════════════════════════════════════════════════\n");
        detailsBuilder.append(String.format("Total Monthly Expenses: ¥%.2f\n", totalAmount));
        detailsBuilder.append(String.format("Number of Categories: %d\n",
                sortedEntries.stream().filter(e -> !e.getValue().isEmpty()).count()));
        detailsBuilder.append(String.format("Total Transactions: %d\n",
                sortedEntries.stream().mapToInt(e -> e.getValue().size()).sum()));

        // Find highest spending category
        if (!sortedEntries.isEmpty() && !sortedEntries.get(0).getValue().isEmpty()) {
            String topCategory = sortedEntries.get(0).getKey();
            double topAmount = sortedEntries.get(0).getValue().stream()
                    .mapToDouble(t -> Math.abs(t.getAmount()))
                    .sum();
            detailsBuilder.append(String.format("\nHighest Spending Category: %s (¥%.2f)\n",
                    topCategory, topAmount));
        }

        detailsBuilder.append("\n═══════════════════════════════════════════════════\n");
        detailsBuilder.append("Classification completed at: " +
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        classificationResultArea.setText(detailsBuilder.toString());
        classificationResultArea.setCaretPosition(0);
    }

    /**
     * 创建标题面板
     * @return 标题面板
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Title and subtitle
        JPanel titleSubPanel = new JPanel();
        titleSubPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel("AI Financial Advisor");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Get personalized financial advice based on your data");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        titleSubPanel.add(titleLabel);
        titleSubPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titleSubPanel.add(subtitleLabel);

        // Current budget display
        JPanel budgetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        currentBudgetLabel = new JLabel("Current Budget: ¥0.00");
        currentBudgetLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentBudgetLabel.setForeground(new Color(52, 152, 219));
        budgetPanel.add(currentBudgetLabel);

        panel.add(titleSubPanel, BorderLayout.CENTER);
        panel.add(budgetPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 创建控制面板
     * @return 控制面板
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Get Advice"));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // 创建获取建议按钮
        getAdviceButton = new JButton("Get AI Financial Advice");
        getAdviceButton.setFont(new Font("Arial", Font.BOLD, 16));
        getAdviceButton.setPreferredSize(new Dimension(300, 50));
        getAdviceButton.setBackground(new Color(52, 152, 219));
        getAdviceButton.setForeground(Color.WHITE);
        getAdviceButton.setFocusPainted(false);
        getAdviceButton.setBorderPainted(false);
        getAdviceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加按钮点击事件
        getAdviceButton.addActionListener(e -> getAIAdvice());

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setPreferredSize(new Dimension(400, 20));

        panel.add(getAdviceButton);
        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        panel.add(statusLabel);

        return panel;
    }

    /**
     * 创建建议显示面板
     * @return 建议显示面板
     */
    private JPanel createAdvicePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("AI Recommendations"));
        panel.setPreferredSize(new Dimension(700, 400)); // 增加高度

        adviceArea = new JTextArea();
        adviceArea.setFont(new Font("Arial", Font.PLAIN, 14));
        adviceArea.setEditable(false);
        adviceArea.setBackground(new Color(248, 249, 250));
        adviceArea.setLineWrap(true);
        adviceArea.setWrapStyleWord(true);
        adviceArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add sample text
        adviceArea.setText("Click the button above to get AI financial advice...");

        JScrollPane scrollPane = new JScrollPane(adviceArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 更新预算显示
     */
    private void updateBudgetDisplay() {
        // Update current budget label
        double budget = budgetService.getBudgetAmount();
        currentBudgetLabel.setText("Current Budget: ¥" + String.format("%.2f", budget));

        if (budget <= 0) {
            currentBudgetLabel.setForeground(new Color(231, 76, 60));  // Red if no budget
        } else {
            currentBudgetLabel.setForeground(new Color(52, 152, 219));  // Blue if budget set
        }
    }

    /**
     * 获取AI建议的主方法
     */
    private void getAIAdvice() {
        // Update budget display first
        updateBudgetDisplay();

        // Show loading status
        statusLabel.setText("Getting AI advice...");
        statusLabel.setForeground(new Color(39, 174, 96));
        getAdviceButton.setEnabled(false);

        // 使用流式输出
        adviceArea.setText("");
        processStreamingAdvice();
    }

    /**
     * 处理流式获取建议
     */
    private void processStreamingAdvice() {
        // 使用流式输出API
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // 显示正在分析的消息
                    SwingUtilities.invokeLater(() -> {
                        adviceArea.setText("Analyzing your financial data...\n\n");
                    });

                    // 检查预算
                    double budget = budgetService.getBudgetAmount();
                    if (budget <= 0) {
                        SwingUtilities.invokeLater(() -> {
                            adviceArea.append("Note: No budget has been set. Please set a monthly budget for more accurate advice.\n\n");
                        });
                    }

                    // 使用流式回调
                    aiService.getStreamingFinancialAdvice(new StreamingUtil.StreamCallback() {
                        @Override
                        public void onToken(String token) {
                            // 在EDT线程中更新UI
                            SwingUtilities.invokeLater(() -> {
                                adviceArea.append(token);
                                // 自动滚动到底部
                                adviceArea.setCaretPosition(adviceArea.getDocument().getLength());
                            });
                        }

                        @Override
                        public void onComplete() {
                            SwingUtilities.invokeLater(() -> {
                                statusLabel.setText("Advice retrieved successfully");
                                statusLabel.setForeground(new Color(39, 174, 96));
                                getAdviceButton.setEnabled(true);
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            SwingUtilities.invokeLater(() -> {
                                adviceArea.append("\n\nError: " + errorMessage);
                                statusLabel.setText("Error retrieving advice");
                                statusLabel.setForeground(new Color(231, 76, 60));
                                getAdviceButton.setEnabled(true);
                            });
                        }
                    });

                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        adviceArea.setText("Sorry, unable to get AI advice. Please check network connection and API configuration.\n\n" +
                                "Showing sample advice:\n" + aiService.getSampleAdvice());
                        statusLabel.setText("Failed to retrieve advice, showing sample");
                        statusLabel.setForeground(new Color(231, 76, 60));
                        getAdviceButton.setEnabled(true);
                    });
                    return null;
                }
            }
        };

        worker.execute();
    }

    /**
     * 更新面板内容
     * 当其他面板中的数据变化时调用
     */
    public void updatePanel() {
        // When panel updates, refresh budget display and clear previous advice
        updateBudgetDisplay();

        if (adviceArea != null) {
            adviceArea.setText("Click the button above to get updated AI financial advice...");
        }
        if (statusLabel != null) {
            statusLabel.setText("");
        }

        // Clear classification results
        if (classificationTableModel != null) {
            classificationTableModel.setRowCount(0);
        }
        if (classificationResultArea != null) {
            classificationResultArea.setText("Click 'Classify This Month's Transactions' to see AI classification results.");
        }
    }
}