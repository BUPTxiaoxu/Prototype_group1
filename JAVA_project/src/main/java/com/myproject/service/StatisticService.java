package main.java.com.myproject.service;

import main.java.com.myproject.model.Transaction;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticService {
    private TransactionService transactionService;
    private BudgetService budgetService;

    // Default categories
    private static final String[] DEFAULT_EXPENSE_CATEGORIES = {
            "Rent", "Fitness", "Medical", "Shopping", "Training", "Travel", "Individual", "Other"
    };

    private static final String[] DEFAULT_INCOME_CATEGORIES = {
            "Salary", "Investment", "Gift", "Other"
    };

    public StatisticService(TransactionService transactionService, BudgetService budgetService) {
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    public main.java.com.myproject.service.StatisticService.CategoryStatistics getCurrentMonthStatistics() {
        // Get current month
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        return getMonthStatistics(year, month);
    }

    public main.java.com.myproject.service.StatisticService.CategoryStatistics getMonthStatistics(int year, int month) {
        List<Transaction> transactions = transactionService.getAllTransactions();

        // Initialize category maps with default categories
        Map<String, Double> expenseByCategory = new HashMap<>();
        Map<String, Double> incomeByCategory = new HashMap<>();

        for (String cat : DEFAULT_EXPENSE_CATEGORIES) {
            expenseByCategory.put(cat, 0.0);
        }

        for (String cat : DEFAULT_INCOME_CATEGORIES) {
            incomeByCategory.put(cat, 0.0);
        }

        double totalExpenses = 0;
        double totalIncome = 0;

        // Calculate totals by category
        for (Transaction transaction : transactions) {
            Calendar transCal = Calendar.getInstance();
            transCal.setTime(transaction.getDate());

            if (transCal.get(Calendar.YEAR) == year && transCal.get(Calendar.MONTH) == month) {
                String category = transaction.getCategory();
                if (category == null || category.trim().isEmpty()) {
                    category = "Other";
                }

                if (transaction.isExpense()) {
                    double amount = Math.abs(transaction.getAmount());
                    expenseByCategory.put(category, expenseByCategory.getOrDefault(category, 0.0) + amount);
                    totalExpenses += amount;
                } else {
                    double amount = transaction.getAmount();
                    incomeByCategory.put(category, incomeByCategory.getOrDefault(category, 0.0) + amount);
                    totalIncome += amount;
                }
            }
        }

        // 这里是如果没设置输出输入默认值，我先给注销了
//        if (totalExpenses == 0) {
//            expenseByCategory.put("Rent", 2000.0);
//            expenseByCategory.put("Shopping", 1500.0);
//            expenseByCategory.put("Food", 1000.0);
//            expenseByCategory.put("Transportation", 500.0);
//            totalExpenses = 5000.0;
//        }
//
//        if (totalIncome == 0) {
//            incomeByCategory.put("Salary", 5000.0);
//            incomeByCategory.put("Investment", 1000.0);
//            totalIncome = 6000.0;
//        }

        double budget = budgetService.getBudgetAmount();
        //if (budget == 0) budget = 6000.0; // 可以自己设置一个

        double remaining = budget - totalExpenses;

        return new main.java.com.myproject.service.StatisticService.CategoryStatistics(
                expenseByCategory,
                incomeByCategory,
                totalExpenses,
                totalIncome,
                budget,
                remaining
        );
    }

    // Inner class to hold category statistics
    public static class CategoryStatistics {
        private Map<String, Double> expenseByCategory;
        private Map<String, Double> incomeByCategory;
        private double totalExpenses;
        private double totalIncome;
        private double budget;
        private double remaining;

        public CategoryStatistics(
                Map<String, Double> expenseByCategory,
                Map<String, Double> incomeByCategory,
                double totalExpenses,
                double totalIncome,
                double budget,
                double remaining) {
            this.expenseByCategory = expenseByCategory;
            this.incomeByCategory = incomeByCategory;
            this.totalExpenses = totalExpenses;
            this.totalIncome = totalIncome;
            this.budget = budget;
            this.remaining = remaining;
        }

        public Map<String, Double> getExpenseByCategory() {
            return expenseByCategory;
        }

        public Map<String, Double> getIncomeByCategory() {
            return incomeByCategory;
        }

        public double getTotalExpenses() {
            return totalExpenses;
        }

        public double getTotalIncome() {
            return totalIncome;
        }

        public double getBudget() {
            return budget;
        }

        public double getRemaining() {
            return remaining;
        }

        public boolean isOverBudget() {
            return remaining < 0;
        }

        public boolean isNearBudgetLimit() {
            return remaining > 0 && remaining < budget * 0.2;
        }
    }
}