package main.java.com.myproject.gui.panel;

import main.java.com.myproject.service.AIService;
import main.java.com.myproject.service.BudgetService;
import main.java.com.myproject.service.StatisticService;
import main.java.com.myproject.service.TransactionService;
import main.java.com.myproject.util.StreamingUtil;

import javax.swing.*;
import java.awt.*;

/**
 * AI面板 - 显示AI财务建议
 */
public class AIPanel extends JPanel {
    private AIService aiService;
    private JTextArea adviceArea;
    private JButton getAdviceButton;
    private JLabel statusLabel;
    private BudgetService budgetService;
    private JLabel currentBudgetLabel;  // 预算显示标签

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
        // Create main container
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
        add(scrollPane, BorderLayout.CENTER);

        // 在所有组件都初始化完成后，更新预算显示
        updateBudgetDisplay();
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
    }
}