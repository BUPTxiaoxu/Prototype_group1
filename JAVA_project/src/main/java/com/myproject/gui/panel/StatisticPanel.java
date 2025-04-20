}package main.java.com.myproject.gui.panel;

import main.java.com.myproject.service.BudgetService;
import main.java.com.myproject.service.StatisticService;
import main.java.com.myproject.service.StatisticService.CategoryStatistics;
import main.java.com.myproject.service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

public class StatisticPanel extends JPanel {
    private StatisticService statisticService;

    // Chart panels
    private JPanel expensePieChartPanel;
    private JPanel incomePieChartPanel;

    // Budget labels
    private JLabel budgetValueLabel;
    private JLabel remainingValueLabel;

    // Colors for pie chart segments
    private static final Color[] CHART_COLORS = {
            new Color(52, 152, 219),    // Blue
            new Color(46, 204, 113),    // Green
            new Color(155, 89, 182),    // Purple
            new Color(230, 126, 34),    // Orange
            new Color(231, 76, 60),     // Red
            new Color(241, 196, 15),    // Yellow
            new Color(26, 188, 156),    // Turquoise
            new Color(149, 165, 166)    // Gray
    };

    public StatisticPanel() {
        this(new TransactionService(), new BudgetService());
    }

    public StatisticPanel(TransactionService transactionService, BudgetService budgetService) {
        this.statisticService = new StatisticService(transactionService, budgetService);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        // Create chart panels
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        expensePieChartPanel = createPieChartPanel("Expense Categories");
        incomePieChartPanel = createPieChartPanel("Income Categories");

        chartsPanel.add(expensePieChartPanel);
        chartsPanel.add(incomePieChartPanel);

        // Create budget panel
        JPanel budgetPanel = createBudgetPanel();

        // Add panels to main container
        mainContainer.add(chartsPanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContainer.add(budgetPanel);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Add a component listener to ensure charts are drawn after layout
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateCharts();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                updateCharts();
            }
        });

        // Update with initial data
        updateCharts();
    }

    private JPanel createPieChartPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Pie chart will be drawn in updateCharts method
            }
        };
        chartPanel.setBackground(Color.WHITE);

        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBudgetPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Monthly Budget Summary"));
        panel.setPreferredSize(new Dimension(750, 150));

        JPanel budgetInfoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        budgetInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel budgetLabel = new JLabel("Current Month Budget:");
        budgetValueLabel = new JLabel("¥0.00");
        budgetValueLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel remainingLabel = new JLabel("Remaining to Spend:");
        remainingValueLabel = new JLabel("¥0.00");
        remainingValueLabel.setFont(new Font("Arial", Font.BOLD, 14));

        budgetInfoPanel.add(budgetLabel);
        budgetInfoPanel.add(budgetValueLabel);
        budgetInfoPanel.add(remainingLabel);
        budgetInfoPanel.add(remainingValueLabel);

        panel.add(budgetInfoPanel, BorderLayout.CENTER);
        return panel;
    }

    public void updateCharts() {
        // Get current month statistics from service
        CategoryStatistics stats = statisticService.getCurrentMonthStatistics();

        // Update expense pie chart
        updatePieChart(expensePieChartPanel, stats.getExpenseByCategory(), stats.getTotalExpenses(), true);

        // Update income pie chart
        updatePieChart(incomePieChartPanel, stats.getIncomeByCategory(), stats.getTotalIncome(), false);

        // Update budget display
        budgetValueLabel.setText(String.format("¥%.2f", stats.getBudget()));
        remainingValueLabel.setText(String.format("¥%.2f", stats.getRemaining()));

        // Set color for remaining amount
        if (stats.isOverBudget()) {
            remainingValueLabel.setForeground(new Color(231, 76, 60)); // Red if over budget
        } else if (stats.isNearBudgetLimit()) {
            remainingValueLabel.setForeground(new Color(230, 126, 34)); // Orange if less than 20% left
        } else {
            remainingValueLabel.setForeground(new Color(46, 204, 113)); // Green if sufficient budget remains
        }

        // Force repaint
        expensePieChartPanel.revalidate();
        expensePieChartPanel.repaint();
        incomePieChartPanel.revalidate();
        incomePieChartPanel.repaint();
    }

    private void updatePieChart(JPanel chartPanel, Map<String, Double> dataByCategory, double total, boolean isExpense) {
        // Get the actual chart component
        Component[] components = chartPanel.getComponents();
        if (components.length == 0) return;

        JPanel piePanel = (JPanel) components[0];
        piePanel.removeAll();

        // Create a new panel for the pie chart and legends
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Create legend panel
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(Color.WHITE);

        int colorIndex = 0;
        for (Map.Entry<String, Double> entry : dataByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = value / total;

            // Skip tiny slices
            if (percentage < 0.01) continue;

            // Select color
            Color segmentColor = CHART_COLORS[colorIndex % CHART_COLORS.length];
            colorIndex++;

            // Create legend item
            JPanel legendItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
            legendItem.setBackground(Color.WHITE);

            JPanel colorBox = new JPanel();
            colorBox.setBackground(segmentColor);
            colorBox.setPreferredSize(new Dimension(15, 15));

            String labelText = String.format("%s: ¥%.2f (%.1f%%)",
                    category, value, percentage * 100);
            JLabel legendLabel = new JLabel(labelText);

            legendItem.add(colorBox);
            legendItem.add(legendLabel);
            legendPanel.add(legendItem);
        }

        // Create pie chart panel
        JPanel pieChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Calculate dimensions
                int width = getWidth();
                int height = getHeight();
                int pieSize = Math.min(width, height) - 40;
                int centerX = width / 2;
                int centerY = height / 2;

                // No data case
                if (total <= 0 || dataByCategory.isEmpty()) {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillOval(centerX - pieSize/2, centerY - pieSize/2, pieSize, pieSize);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("No data", centerX - 25, centerY);
                    return;
                }

                // Draw pie segments
                int startAngle = 0;
                int colorIndex = 0;

                for (Map.Entry<String, Double> entry : dataByCategory.entrySet()) {
                    String category = entry.getKey();
                    double value = entry.getValue();
                    double percentage = value / total;
                    int arcAngle = (int) (percentage * 360);

                    // Skip tiny slices
                    if (arcAngle <= 0) continue;

                    // Select color and draw segment
                    Color segmentColor = CHART_COLORS[colorIndex % CHART_COLORS.length];
                    g2d.setColor(segmentColor);
                    g2d.fillArc(centerX - pieSize/2, centerY - pieSize/2, pieSize, pieSize, startAngle, arcAngle);

                    startAngle += arcAngle;
                    colorIndex++;
                }

                // Draw center circle for donut chart effect
                g2d.setColor(Color.WHITE);
                g2d.fillOval(centerX - pieSize/4, centerY - pieSize/4, pieSize/2, pieSize/2);

                // Draw total in center
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String totalText = String.format("¥%.2f", total);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(totalText, centerX - fm.stringWidth(totalText)/2, centerY + fm.getAscent()/2);
            }
        };

        pieChart.setBackground(Color.WHITE);
        pieChart.setPreferredSize(new Dimension(200, 200));

        // Add to content panel
        contentPanel.add(pieChart, BorderLayout.CENTER);
        contentPanel.add(legendPanel, BorderLayout.EAST);

        piePanel.setLayout(new BorderLayout());
        piePanel.add(contentPanel);

        piePanel.revalidate();
        piePanel.repaint();
    }

    public void updatePanel() {
        updateCharts();
    }
