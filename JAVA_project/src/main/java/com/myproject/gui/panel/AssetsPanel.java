//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package main.java.com.myproject.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.java.com.myproject.service.AssetsService;
import main.java.com.myproject.service.TransactionService;

public class AssetsPanel extends JPanel {
    private AssetsService assetsService;
    private DefaultTableModel tableModel;
    private JLabel netIncomeValueLabel;
    private JLabel totalAssetsValueLabel;

    public AssetsPanel(TransactionService transactionService) {
        this.assetsService = new AssetsService(transactionService);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, 1));
        mainContainer.add(this.createMonthlyDataPanel());
        mainContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        mainContainer.add(this.createIncomeSummaryPanel());
        this.add(new JScrollPane(mainContainer), "Center");
    }

    private JPanel createMonthlyDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Monthly Income and Expenses"));
        panel.setPreferredSize(new Dimension(750, 300));
        String[] columnNames = new String[]{"Month", "Income", "Expenses", "Net", "Total Assets"};
        this.tableModel = new DefaultTableModel(columnNames, 0);
        JTable assetsTable = new JTable(this.tableModel);
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 1) {
                    this.setForeground(new Color(46, 204, 113));
                } else if (column == 2) {
                    this.setForeground(new Color(255, 99, 71));
                } else if (column == 3 && value instanceof String) {
                    this.setForeground(((String)value).contains("-") ? new Color(255, 99, 71) : new Color(46, 204, 113));
                } else {
                    this.setForeground(Color.BLACK);
                }

                this.setHorizontalAlignment(4);
                return c;
            }
        };

        for(int i = 1; i < assetsTable.getColumnCount(); ++i) {
            assetsTable.getColumnModel().getColumn(i).setCellRenderer(moneyRenderer);
        }

        this.updateMonthlyDataTable();
        panel.add(new JScrollPane(assetsTable), "Center");
        return panel;
    }

    private JPanel createIncomeSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Current Month Summary"));
        panel.setPreferredSize(new Dimension(750, 150));
        Calendar cal = Calendar.getInstance();
        int year = cal.get(1);
        int month = cal.get(2);
        double totalIncome = this.assetsService.getMonthlyIncome(year, month);
        double totalExpenses = this.assetsService.getMonthlyExpenses(year, month);
        double netIncome = totalIncome - totalExpenses;
        double totalAssets = this.assetsService.calculateTotalAssets();
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.netIncomeValueLabel = new JLabel(String.format("¥%.2f", netIncome));
        this.netIncomeValueLabel.setFont(new Font("Arial", 1, 14));
        this.netIncomeValueLabel.setForeground(netIncome >= 0.0 ? new Color(46, 204, 113) : new Color(255, 99, 71));
        this.totalAssetsValueLabel = new JLabel(String.format("¥%.2f", totalAssets));
        this.totalAssetsValueLabel.setFont(new Font("Arial", 1, 14));
        infoPanel.add(new JLabel("Current Month Net Income:"));
        infoPanel.add(this.netIncomeValueLabel);
        infoPanel.add(new JLabel("Total Assets:"));
        infoPanel.add(this.totalAssetsValueLabel);
        panel.add(infoPanel, "Center");
        return panel;
    }

    private void updateMonthlyDataTable() {
        this.tableModel.setRowCount(0);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        Calendar cal = Calendar.getInstance();
        Iterator var3 = this.assetsService.getMonthlyAssetData().iterator();

        while(var3.hasNext()) {
            AssetsService.MonthlyAssetData data = (AssetsService.MonthlyAssetData)var3.next();
            cal.set(1, data.getYear());
            cal.set(2, data.getMonth());
            double monthlyIncome = this.assetsService.getMonthlyIncome(data.getYear(), data.getMonth());
            double monthlyExpenses = this.assetsService.getMonthlyExpenses(data.getYear(), data.getMonth());
            double monthlyNet = monthlyIncome - monthlyExpenses;
            this.tableModel.addRow(new Object[]{monthFormat.format(cal.getTime()), String.format("¥%.2f", monthlyIncome), String.format("¥%.2f", monthlyExpenses), String.format("%s¥%.2f", monthlyNet < 0.0 ? "-" : "", Math.abs(monthlyNet)), String.format("¥%.2f", data.getAssetValue())});
        }

    }

    public void updatePanel() {
        this.updateMonthlyDataTable();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(1);
        int month = cal.get(2);
        double totalIncome = this.assetsService.getMonthlyIncome(year, month);
        double totalExpenses = this.assetsService.getMonthlyExpenses(year, month);
        double netIncome = totalIncome - totalExpenses;
        double totalAssets = this.assetsService.calculateTotalAssets();
        this.netIncomeValueLabel.setText(String.format("¥%.2f", netIncome));
        this.netIncomeValueLabel.setForeground(netIncome >= 0.0 ? new Color(46, 204, 113) : new Color(255, 99, 71));
        this.totalAssetsValueLabel.setText(String.format("¥%.2f", totalAssets));
    }
}
